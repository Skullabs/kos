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
import io.vertx.config.impl.ConfigRetrieverImpl;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.logging.JULLogDelegateFactory;
import io.vertx.core.spi.logging.LogDelegateFactory;
import kos.core.client.RestClientSerializer;
import lombok.val;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@DisplayName("Unit: KosConfiguration")
class KosConfigurationTest {

    final ImplementationLoader spi = mock( ImplementationLoader.class );
    final MutableKosConfiguration conf = new MutableKosConfiguration(spi);

    @BeforeEach void setUpMocks(){
        doReturn(ImplementationLoader.Result.empty()).when(spi).anyInstanceOf( eq(ImplementationLoader.class) );
    }

    @DisplayName("Scenario: Serializers")
    @Nested class SerializersScenario {

        @DisplayName("Should discover and load all serializers from SPI")
        @Test void scenario1(){
            
            assertEquals(2, conf.getSerializers().size());
            assertTrue(conf.getSerializers().get("application/json") instanceof Serializer.JsonSerializer);
            assertTrue(conf.getSerializers().get("text/plain") instanceof Serializer.PlainTextSerializer);
        }

        @DisplayName("Should return JsonSerializer as default serializer WHEN none was defined")
        @Test void scenario2(){
            
            assertTrue(conf.getDefaultSerializer() instanceof Serializer.JsonSerializer);
        }

        @DisplayName("Should return custom Serializer WHEN defined via setter")
        @Test void scenario3(){
            var defined = mock(Serializer.class);
            conf.setDefaultSerializer(defined);

            
            assertEquals(defined, conf.getDefaultSerializer());
        }
    }

    @DisplayName("Scenario: RestClientSerializers")
    @Nested class RestClientSerializerScenario {

        @DisplayName("Should discover and load all Rest Client Serializers from SPI")
        @Test void scenario1() {
            
            assertEquals(1, conf.getRestClientSerializers().size());
            assertTrue(conf.getRestClientSerializers().get("application/json") instanceof RestClientSerializer.JsonRestClientSerializer);
        }

        @DisplayName("Should return JsonRestClientSerializer as default serializer WHEN none was defined")
        @Test void scenario2() {
            
            assertTrue(conf.getDefaultRestClientSerializer() instanceof RestClientSerializer.JsonRestClientSerializer);
        }

        @DisplayName("Should return custom RestClientSerializer WHEN defined via setter")
        @Test void scenario3() {
            var defined = mock(RestClientSerializer.class);
            conf.setDefaultRestClientSerializer(defined);

            
            assertEquals(defined, conf.getDefaultRestClientSerializer());
        }
    }

    @DisplayName("Scenario: PayloadSerializationStrategy")
    @Nested class PayloadSerializationStrategyScenario {

        @DisplayName("Should return default serialization strategy WHEN no object was defined via setter")
        @Test void scenario1() {
            assertTrue(conf.getPayloadSerializationStrategy() instanceof SingleSerializerStrategy);
        }

        @DisplayName("Should the object that was defined via setter")
        @Test void scenario2(){
            val defined = mock(PayloadSerializationStrategy.class);
            conf.setPayloadSerializationStrategy(defined);

            assertEquals(defined, conf.getPayloadSerializationStrategy());
        }
    }

    @DisplayName("Scenario: LogDelegateFactory")
    @Nested class LogDelegateFactoryScenario {

        @DisplayName("Should return default value WHEN no object was defined via setter")
        @Test void scenario1() {
            assertTrue(conf.getLogDelegateFactory() instanceof JULLogDelegateFactory);
        }

        @DisplayName("Should the object that was defined via setter")
        @Test void scenario2(){
            val defined = mock(LogDelegateFactory.class);
            conf.setLogDelegateFactory(defined);

            assertEquals(defined, conf.getLogDelegateFactory());
        }
    }

    @DisplayName("Scenario: Default Vertx")
    @Nested class VertxScenario {

        @DisplayName("Should create new Vertx instance WHEN no object was defined via setter")
        @Test void scenario1() {
            doReturn(ImplementationLoader.Result.empty()).when(spi).anyInstanceOf(eq(VertxOptions.class));
            assertNotNull(conf.getDefaultVertx());
        }

        @DisplayName("Should the object that was defined via setter")
        @Test void scenario2(){
            val defined = mock(Vertx.class);
            conf.setDefaultVertx(defined);
            assertEquals(defined, conf.getDefaultVertx());
        }
    }

    @DisplayName("Scenario: StringConverter")
    @Nested class StringConverterScenario {

        @DisplayName("Should return default value WHEN no object was defined via setter")
        @Test void scenario1() {
            assertTrue(conf.getStringConverter() instanceof StringConverter.DefaultStringConverter);
        }

        @DisplayName("Should the object that was defined via setter")
        @Test void scenario2(){
            val defined = mock(StringConverter.class);
            conf.setStringConverter(defined);
            assertEquals(defined, conf.getStringConverter());
        }
    }

    @DisplayName("Scenario: ExceptionHandler")
    @Nested class ExceptionHandlerScenario {

        @DisplayName("Should return default value WHEN no object was defined via setter")
        @Test void scenario1() {
            assertTrue(conf.getExceptionHandler() instanceof ExceptionHandler.DefaultExceptionHandler);
        }

        @DisplayName("Should the object that was defined via setter")
        @Test void scenario2(){
            val defined = mock(ExceptionHandler.class);
            conf.setExceptionHandler(defined);
            assertEquals(defined, conf.getExceptionHandler());
        }
    }

    @DisplayName("Scenario: ConfigRetriever")
    @Nested class ConfigRetrieverScenario {

        @DisplayName("Should return default value WHEN no object was defined via setter")
        @Test void scenario1() {
            assertTrue(conf.getConfigRetriever() instanceof ConfigRetrieverImpl);
        }

        @DisplayName("Should the object that was defined via setter")
        @Test void scenario2(){
            val defined = mock(ConfigRetriever.class);
            conf.setConfigRetriever(defined);
            assertEquals(defined, conf.getConfigRetriever());
        }
    }
}
