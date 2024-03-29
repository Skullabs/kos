/*
 * Copyright 2019 Skullabs Contributors (https://github.com/skullabs)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kos.api;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import kos.core.Lang;
import kos.core.client.RestClientSerializer;
import kos.core.events.DefaultEventBusMessageCodecFactory;
import kos.core.exception.PredicateExceptionHandler;
import kos.core.validation.DefaultValidation;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

import static kos.api.Serializer.INVALID_SERIALIZER;

@Getter @Setter
@Accessors(chain = true)
@SuppressWarnings("all")
public class MutableKosContext implements KosContext
{

    private final AvailablePayloadStrategies availablePayloadStrategies = new AvailablePayloadStrategies();

    private final Map<String, Serializer> serializers;
    private final Map<String, RestClientSerializer> restClientSerializers;
    private final ImplementationLoader spi;
    private ImplementationLoader implementationLoader;

    private PayloadSerializationStrategy payloadSerializationStrategy;
    private int defaultStatusForEmptyResponses = 204;
    private HttpServerOptions httpServerOptions;

    private Serializer defaultSerializer;
    private EventBusMessageCodecFactory defaultEventBusCodecFactory;
    private RestClientSerializer defaultRestClientSerializer;
    private Vertx defaultVertx;
    private WebClient defaultVertxWebClient;
    private StringConverter stringConverter;
    private ExceptionHandler exceptionHandler;
    private Validation defaultValidation;
    private ConfigRetriever configRetriever;
    private JsonObject applicationConfig;

    public MutableKosContext(){
        this(new ImplementationLoader.SPIImplementationLoader());
    }

    public MutableKosContext(ImplementationLoader spi) {
        this.spi = spi;
        this.restClientSerializers = loadRestClientSerializers();
        this.serializers = loadSerializers();
        this.implementationLoader = spi;
        this.defaultSerializer = getSerializers().get("application/json");
        this.defaultEventBusCodecFactory = new DefaultEventBusMessageCodecFactory();
        this.payloadSerializationStrategy = new SingleSerializerStrategy(getDefaultSerializer());
        this.httpServerOptions = new HttpServerOptions().setPort(9000);
        this.exceptionHandler = new PredicateExceptionHandler();
        this.defaultValidation = new DefaultValidation();
        this.stringConverter = new StringConverter.DefaultStringConverter();
        this.defaultRestClientSerializer = getRestClientSerializers().get("application/json");
    }

    public ConfigRetriever getConfigRetriever() {
        if (configRetriever == null)
            configRetriever = loadConfigRetriever();
        return configRetriever;
    }

    private ConfigRetriever loadConfigRetriever() {
        val retrieverOptions = new ConfigRetrieverOptions();
        return ConfigRetriever.create(getDefaultVertx(), retrieverOptions);
    }

    public Vertx getDefaultVertx() {
        if (defaultVertx == null)
            defaultVertx = Vertx.vertx();
        return defaultVertx;
    }

    public WebClient getDefaultVertxWebClient() {
        if (defaultVertxWebClient == null)
            defaultVertxWebClient = WebClient.create(getDefaultVertx());
        return defaultVertxWebClient;
    }

    private Map<String, Serializer> loadSerializers() {
        val serializers = new HashMap<String, Serializer>();

        val json = new Serializer.JsonSerializer();
        serializers.put(json.contentType(), json);

        val plainText = new Serializer.PlainTextSerializer(this);
        serializers.put(plainText.contentType(), plainText);

        return serializers;
    }

    private Map<String, RestClientSerializer> loadRestClientSerializers() {
        val serializers = new HashMap<String, RestClientSerializer>();

        val json = new RestClientSerializer.JsonRestClientSerializer();
        serializers.put(json.contentType(), json);
        return serializers;
    }

    public Serializer getSerializerForContentType(String contentType){
        return getSerializers().computeIfAbsent(contentType, INVALID_SERIALIZER);
    }

    public JsonObject getApplicationConfig() {
        if (applicationConfig != null) return applicationConfig;

        Future<JsonObject> future = Future.future(getConfigRetriever()::getConfig);
        future.onComplete(obj -> {
            if (obj.succeeded())
                applicationConfig = obj.result();
        });

        return Lang.waitFor(future);
    }

    @Override public <T> Future<T> computeBlocking(SupplierThatMightFail<T> supplier)
    {
        val promise = Promise.<T>promise();
        getDefaultVertx().executeBlocking(future -> {
            try {
                val result = supplier.get();
                future.complete(result);
            } catch (Throwable cause) {
                future.fail(cause);
            }
        }, promise);
        return promise.future();
    }

    @Override public Future<Void> runBlocking(RunnerThatMightFail runner)
    {
        val promise = Promise.<Void>promise();
        getDefaultVertx().executeBlocking(future -> {
            try {
                runner.run();
                future.complete();
            } catch (Throwable cause) {
                future.fail(cause);
            }
        }, promise);
        return promise.future();
    }

    @Override
    public String toString() {
        return "MutableKosContext{" +
                "availablePayloadStrategies=" + availablePayloadStrategies +
                ", serializers=" + serializers +
                ", restClientSerializers=" + restClientSerializers +
                ", spi=" + spi +
                ", implementationLoader=" + implementationLoader +
                ", payloadSerializationStrategy=" + payloadSerializationStrategy +
                ", defaultStatusForEmptyResponses=" + defaultStatusForEmptyResponses +
                ", httpServerOptions=" + httpServerOptions +
                ", defaultSerializer=" + defaultSerializer +
                ", defaultRestClientSerializer=" + defaultRestClientSerializer +
                ", defaultVertx=" + defaultVertx +
                ", defaultVertxWebClient=" + defaultVertxWebClient +
                ", stringConverter=" + stringConverter +
                ", exceptionHandler=" + exceptionHandler +
                ", configRetriever=" + configRetriever +
                ", applicationConfig=" + applicationConfig +
                '}';
    }

    public class AvailablePayloadStrategies {

        /**
         * Creates an strategy based on the default serializer (usually Json).
         */
        public void useDefaultSerializer() {
            val strategy = new SingleSerializerStrategy(getDefaultSerializer());
            setPayloadSerializationStrategy(strategy);
        }

        /**
         * Creates an strategy based on the default serializer (usually Json).
         */
        public void useSerializerForContentType(String defaultContentType) {
            val computed = getSerializerForContentType(defaultContentType);
            val strategy = new SingleSerializerStrategy(computed);
            setPayloadSerializationStrategy(strategy);
        }

        /**
         * Creates an strategy that reads uses the defined response Content-Type
         * to pick an serializer and perform the serialization. If no Content-Type
         * was defined it will pick the one defined by {@code defaultContentType}.
         * The serialization strategy will throw {@link IllegalArgumentException}
         * if no serializer was found for the computed Content-Type.
         */
        public void inferSerializerFromHttpHeader(String defaultContentType) {
            val kosConfiguration = MutableKosContext.this;
            val strategy = new HeaderParserStrategy(kosConfiguration, HttpHeaders.CONTENT_TYPE, defaultContentType);
            setPayloadSerializationStrategy(strategy);
        }
    }
}
