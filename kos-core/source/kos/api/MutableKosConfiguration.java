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
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.core.logging.JULLogDelegateFactory;
import io.vertx.core.logging.Logger;
import io.vertx.core.spi.logging.LogDelegateFactory;
import kos.core.client.RestClientSerializer;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
@Accessors(chain = true)
public class MutableKosConfiguration implements KosConfiguration {

    private final Map<String, Serializer> serializers;
    private final Map<String, RestClientSerializer> restClientSerializers;
    private final ImplementationLoader spi;
    private ImplementationLoader implementationLoader;

    private PayloadSerializationStrategy payloadSerializationStrategy;
    private int defaultStatusForEmptyResponses = 204;
    private LogDelegateFactory logDelegateFactory;
    private HttpServerOptions httpServerOptions;

    private Serializer defaultSerializer;
    private RestClientSerializer defaultRestClientSerializer;
    private Vertx defaultVertx;
    private StringConverter stringConverter;
    private ExceptionHandler exceptionHandler;
    private ConfigRetriever configRetriever;

    public MutableKosConfiguration(){
        this(new ImplementationLoader.SPIImplementationLoader());
    }

    public MutableKosConfiguration(ImplementationLoader spi) {
        this.spi = spi;
        this.restClientSerializers = loadRestClientSerializers();
        this.serializers = loadSerializers();
        this.implementationLoader = spi;
        this.payloadSerializationStrategy = new SingleSerializerStrategy(getDefaultSerializer());
        this.httpServerOptions = new HttpServerOptions().setPort(9000);
        this.logDelegateFactory = new JULLogDelegateFactory();
        this.exceptionHandler = new ExceptionHandler.DefaultExceptionHandler();
        this.stringConverter = new StringConverter.DefaultStringConverter();
        this.defaultSerializer = getSerializers().get("application/json");
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

    private Map<String, Serializer> loadSerializers() {
        val serializers = new HashMap<String, Serializer>();

        val json = new Serializer.JsonSerializer();
        serializers.put(json.contentType(), json);

        val plainText = new Serializer.PlainTextSerializer();
        serializers.put(plainText.contentType(), plainText);

        return serializers;
    }

    private Map<String, RestClientSerializer> loadRestClientSerializers() {
        val serializers = new HashMap<String, RestClientSerializer>();

        val json = new RestClientSerializer.JsonRestClientSerializer();
        serializers.put(json.contentType(), json);
        return serializers;
    }

    public Logger createLoggerFor(Class type) {
        return new Logger(
            getLogDelegateFactory().createDelegate(type.getCanonicalName())
        );
    }
}
