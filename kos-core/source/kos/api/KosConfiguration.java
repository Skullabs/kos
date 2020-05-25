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
import io.vertx.core.VertxOptions;
import io.vertx.core.logging.JULLogDelegateFactory;
import io.vertx.core.spi.logging.LogDelegateFactory;
import kos.core.Lang;
import kos.core.client.RestClientSerializer;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME;

@RequiredArgsConstructor
@Getter @Setter
@Accessors(chain = true)
public class KosConfiguration {

    private final ImplementationLoader spi;
    private ImplementationLoader implementationLoader;

    private PayloadSerializationStrategy payloadSerializationStrategy;
    private int defaultStatusForEmptyResponses = 204;
    private LogDelegateFactory logDelegateFactory;

    private Serializer defaultSerializer;
    private RestClientSerializer defaultRestClientSerializer;
    private Vertx defaultVertx;
    private StringConverter stringConverter;
    private ExceptionHandler exceptionHandler;
    private ConfigRetriever configRetriever;

    public KosConfiguration(){
        this(new ImplementationLoader.SPIImplementationLoader());
    }

    public Immutable build(){
        return new Immutable();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class Immutable {

        private final Map<String, RestClientSerializer> restClientSerializers = loadRestClientSerializers();
        private final Map<String, Serializer> serializers = loadSerializers();

        public ImplementationLoader getImplementationLoader(){
            if (implementationLoader == null)
                implementationLoader = spi.anyInstanceOf(ImplementationLoader.class).orElse(spi);
            return implementationLoader;
        }

        public PayloadSerializationStrategy getPayloadSerializationStrategy() {
            if (payloadSerializationStrategy == null)
                setPayloadSerializationStrategy(new SingleSerializerStrategy(getDefaultSerializer()));
            return payloadSerializationStrategy;
        }

        public Serializer getDefaultSerializer() {
            if (defaultSerializer == null)
                setDefaultSerializer(getSerializers().get("application/json"));
            return defaultSerializer;
        }

        public RestClientSerializer getDefaultRestClientSerializer() {
            if (defaultRestClientSerializer == null)
                setDefaultRestClientSerializer(getRestClientSerializers().get("application/json"));
            return defaultRestClientSerializer;
        }

        public LogDelegateFactory getLogDelegateFactory() {
            if (logDelegateFactory == null)
                setLogDelegateFactory(loadLogDelegateFactory());
            return logDelegateFactory;
        }

        public Vertx getDefaultVertx() {
            if (defaultVertx == null) {
                val vertxOptions = getImplementationLoader().anyInstanceOf(VertxOptions.class);
                defaultVertx = vertxOptions.isPresent() ? Vertx.vertx(vertxOptions.get()) : Vertx.vertx();
            }
            return defaultVertx;
        }

        public StringConverter getStringConverter() {
            if (stringConverter == null)
                stringConverter = getImplementationLoader().anyInstanceOf(StringConverter.class)
                        .orElseGet(StringConverter.DefaultStringConverter::new);
            return stringConverter;
        }

        public ExceptionHandler getExceptionHandler() {
            if (exceptionHandler == null)
                exceptionHandler = getImplementationLoader()
                        .anyInstanceOf(ExceptionHandler.class)
                        .orElseGet(ExceptionHandler.DefaultExceptionHandler::new);
            return exceptionHandler;
        }

        public ConfigRetriever getConfigRetriever() {
            if (configRetriever == null)
                configRetriever = loadConfigRetriever();
            return configRetriever;
        }

        private Map<String, Serializer> loadSerializers() {
            val serializers = new HashMap<String, Serializer>();

            val json = new Serializer.JsonSerializer();
            serializers.put(json.contentType(), json);

            val plainText = new Serializer.PlainTextSerializer();
            serializers.put(plainText.contentType(), plainText);

            getImplementationLoader().instancesExposedAs(Serializer.class)
                    .forEach(s -> serializers.put(s.contentType(), s));
            return serializers;
        }

        private Map<String, RestClientSerializer> loadRestClientSerializers() {
            val serializers = new HashMap<String, RestClientSerializer>();

            val json = new RestClientSerializer.JsonRestClientSerializer();
            serializers.put(json.contentType(), json);

            getImplementationLoader().instancesExposedAs(RestClientSerializer.class)
                    .forEach(s -> serializers.put(s.contentType(), s));
            return serializers;
        }

        private LogDelegateFactory loadLogDelegateFactory() {
            val commandLineLogger = System.getProperty(LOGGER_DELEGATE_FACTORY_CLASS_NAME);
            if (commandLineLogger != null)
                return loadLogDelegateFactory(commandLineLogger);

            val factory = getImplementationLoader()
                    .anyInstanceOf(LogDelegateFactory.class)
                    .orElseGet(JULLogDelegateFactory::new);

            System.setProperty(LOGGER_DELEGATE_FACTORY_CLASS_NAME, factory.getClass().getCanonicalName());

            return factory;
        }

        private LogDelegateFactory loadLogDelegateFactory(String canonicalName) {
            val loader = Thread.currentThread().getContextClassLoader();
            try {
                val clz = loader.loadClass(canonicalName);
                return (LogDelegateFactory) clz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Error instantiating transformer class \"" + canonicalName + "\"", e);
            }
        }

        private ConfigRetriever loadConfigRetriever() {
            val factories = Lang.sorted(
                getImplementationLoader().instancesExposedAs(ConfigStoreOptionsFactory.class),
                Comparator.comparingInt(ConfigStoreOptionsFactory::priority)
            );

            val retrieverOptions = new ConfigRetrieverOptions();
            factories.forEach(factory -> retrieverOptions.addStore(factory.create()));

            return getImplementationLoader()
                .anyInstanceOf(ConfigRetriever.class)
                .orElseGet(() -> ConfigRetriever.create(getDefaultVertx(), retrieverOptions));
        }
    }
}
