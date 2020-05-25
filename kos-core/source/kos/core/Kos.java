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

package kos.core;

import io.vertx.config.*;
import io.vertx.config.spi.*;
import io.vertx.core.*;
import io.vertx.core.json.*;
import io.vertx.core.logging.*;
import io.vertx.core.spi.logging.*;
import io.vertx.ext.web.client.WebClient;
import kos.api.*;
import kos.core.Lang.*;
import kos.core.client.RestClientSerializer;
import lombok.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import static io.vertx.core.logging.LoggerFactory.*;

/**
 * Singleton class that holds <i>kos</i> context. This class was designed
 * to store important configurable pieces from Vert.x, and a few internal
 * kos configuration. Developers are encouraged to use this class
 * only before the server is started, as changes made after that will not
 * be automatically propagated.
 */
public final class Kos {

    private static final ImplementationLoader spi = new ImplementationLoader.SPIImplementationLoader();

    /**
     * Found serializers for Http Requests.
     * @see Serializer
     * @see Kos#defaultSerializer
     */
    public static final Map<String, Serializer> serializers = loadSerializers();

    /**
     * Default {@link Serializer} for http requests. If not otherwise configured,
     * this will be used to deserialize payloads from incoming requests or to
     * automatically serialize response objects.
     */
    public static final Serializer defaultSerializer = serializers.get("application/json");

    private static Map<String, Serializer> loadSerializers() {
        val serializers = new HashMap<String, Serializer>();

        val json = new Serializer.JsonSerializer();
        serializers.put(json.contentType(), json);

        val plainText = new Serializer.PlainTextSerializer();
        serializers.put(plainText.contentType(), plainText);

        spi.instancesExposedAs(Serializer.class)
            .forEach(s -> serializers.put(s.contentType(), s));
        return serializers;
    }

    /**
     * Found serializers for Rest Clients.
     * @see RestClientSerializer
     * @see Kos#defaultRestClientSerializer
     */
    public static final Map<String, RestClientSerializer> restClientSerializers = loadRestClientSerializers();

    /**
     * Default {@link Serializer} for Rest Clients. If not otherwise configured,
     * this will be used to deserialize payloads received as a request response or to
     * automatically serialize objects used as request payload.
     */
    public static RestClientSerializer defaultRestClientSerializer =
        restClientSerializers.get("application/json");

    private static Map<String, RestClientSerializer> loadRestClientSerializers() {
        val serializers = new HashMap<String, RestClientSerializer>();

        val json = new RestClientSerializer.JsonRestClientSerializer();
        serializers.put(json.contentType(), json);

        spi.instancesExposedAs(RestClientSerializer.class)
            .forEach(s -> serializers.put(s.contentType(), s));
        return serializers;
    }

    /**
     * Serialization strategy used to serialize/deserialize objects.
     */
    public static PayloadSerializationStrategy payloadSerializationStrategy =
        PayloadSerializationStrategy.useDefaultSerializer();

    /**
     * Defines the default status code to sent when either no response body
     * is defined or the response body is empty. Defaults to 204, following
     * the semantics suggested by HTTP RFC.
     * <p>
     * See: https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/204
     */
    public static final int defaultStatusForEmptyResponses = 204;

    private static final LogDelegateFactory logFactory = loadLogDelegateFactory();

    private static final ConcurrentMap<String, Logger> loggers = new ConcurrentHashMap<>();

    /**
     * Alternative log loader that uses SPI to load the log configuration but will otherwise
     * honour the property defined by {@link LoggerFactory#LOGGER_DELEGATE_FACTORY_CLASS_NAME}.
     * Despite of its new mechanism, it tries its best to propagate the found {@link LogDelegateFactory}
     * and make it available for other logs that relies on {@link LoggerFactory#getLogger(Class)}.
     *
     * @return the found {@link LogDelegateFactory}.
     */
    private static LogDelegateFactory loadLogDelegateFactory() {
        val commandLineLogger = System.getProperty(LOGGER_DELEGATE_FACTORY_CLASS_NAME);
        if (commandLineLogger != null)
            return loadLogDelegateFactory(commandLineLogger);

        val factory = spi
                .anyInstanceOf(LogDelegateFactory.class)
                .orElseGet(JULLogDelegateFactory::new);

        System.setProperty(LOGGER_DELEGATE_FACTORY_CLASS_NAME, factory.getClass().getCanonicalName());

        return factory;
    }

    /**
     * Reads the LogDelegateFactory by reflection. Originally copied from
     * {@link LoggerFactory#getLogger(String)}.
     *
     * @see io.vertx.core.logging.LoggerFactory
     */
    private static LogDelegateFactory loadLogDelegateFactory(String canonicalName) {
        val loader = Thread.currentThread().getContextClassLoader();
        try {
            val clz = loader.loadClass(canonicalName);
            return (LogDelegateFactory) clz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error instantiating transformer class \"" + canonicalName + "\"", e);
        }
    }

    /**
     * Returns a Logger for a given class.
     */
    public static Logger logger(Class type) {
        return loggers.computeIfAbsent(
            type.getCanonicalName(),
            name -> new Logger(new LazyLogDelegate(() ->
                logFactory.createDelegate(name)
            ))
        );
    }

    /**
     * The configured {@link ImplementationLoader}.
     */
    public static final ImplementationLoader implementationLoader =
        spi.anyInstanceOf(ImplementationLoader.class).orElse(spi);

    /**
     * Default reference to a {@link Vertx} instance.
     */
    public static final Supplier<Vertx> defaultVertx = Lazy.by(Kos::loadDefaultVertX);

    private static Vertx loadDefaultVertX() {
        val vertxOptions = implementationLoader.anyInstanceOf(VertxOptions.class);
        return vertxOptions.isPresent() ? Vertx.vertx(vertxOptions.get()) : Vertx.vertx();
    }

    /**
     * Default {@link StringConverter}.
     */
    public static final Lazy<StringConverter> stringConverter = Lazy.by(
            () -> implementationLoader
                    .anyInstanceOf(StringConverter.class)
                    .orElseGet(StringConverter.DefaultStringConverter::new)
    );

    /**
     * Default {@link ExceptionHandler}.
     */
    public static final Lazy<ExceptionHandler> exceptionHandler = Lazy.by(
        () -> implementationLoader
            .anyInstanceOf(ExceptionHandler.class)
            .orElseGet(ExceptionHandler.DefaultExceptionHandler::new)
    );

    /**
     * Default configuration. It is lazily loaded by the {@link ConfigRetriever}
     * found in the classpath.
     */
    public static final Lazy<JsonObject> config = Lazy.by(
        () -> { throw new KosException("Configuration not read yet"); } );

    private static final Lazy<ConfigRetriever> configRetriever = Lazy.by(Kos::loadConfigRetriever);

    private static ConfigRetriever loadConfigRetriever() {
        val factories = Lang.sorted(
            spi.instancesExposedAs(ConfigStoreOptionsFactory.class),
            Comparator.comparingInt(ConfigStoreOptionsFactory::priority)
        );

        val retrieverOptions = new ConfigRetrieverOptions();
        factories.forEach(factory -> retrieverOptions.addStore(factory.create()));

        return spi
            .anyInstanceOf(ConfigRetriever.class)
            .orElseGet(() -> ConfigRetriever.create(defaultVertx.get(), retrieverOptions));
    }

    /**
     * Reads the configuration from the different {@link ConfigStore}
     * and computes the final configuration.
     *
     * @param handler handler receiving the computed configuration
     * @throws KosException if couldn't read the configuration
     */
    public static void readConfig(Handler<JsonObject> handler) {
        configRetriever.get().getConfig( res -> {
            if (res.succeeded()) {
                config.set(res.result());
                handler.handle(res.result());
            } else {
                throw new KosException("Failed to read configuration", res.cause());
            }
        });
    }

    /**
     * Default WebClient.
     */
    public static final Lazy<WebClient> webClient = Lazy.by(
        () -> implementationLoader.anyInstanceOf( WebClient.class )
                .orElseGet(() -> WebClient.create(defaultVertx.get()))
    );
}
