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
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.logging.LogDelegateFactory;
import kos.core.Lang.Result;
import kos.core.client.RestClientSerializer;
import lombok.val;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@DisplayName("Unit: KosConfiguration")
class KosConfigurationTest {

    final ImplementationLoader spi = mock( ImplementationLoader.class );
    final KosConfiguration conf = new KosConfiguration(spi);

    @BeforeEach void setUpMocks(){
        doReturn(Result.empty()).when(spi).anyInstanceOf( eq(ImplementationLoader.class) );
    }
    
    @DisplayName("Scenario: Serializers")
    @Nested class SerializersScenario {

        @DisplayName("Should discover and load all serializers from SPI")
        @Test void scenario1(){
            var immutable = conf.build();
            assertEquals(2, immutable.getSerializers().size());
            assertTrue(immutable.getSerializers().get("application/json") instanceof Serializer.JsonSerializer);
            assertTrue(immutable.getSerializers().get("text/plain") instanceof Serializer.PlainTextSerializer);
        }

        @DisplayName("Should return JsonSerializer as default serializer WHEN none was defined")
        @Test void scenario2(){
            var immutable = conf.build();
            assertTrue(immutable.getDefaultSerializer() instanceof Serializer.JsonSerializer);
        }

        @DisplayName("Should return custom Serializer WHEN defined via setter")
        @Test void scenario3(){
            var defined = mock(Serializer.class);
            conf.setDefaultSerializer(defined);

            var immutable = conf.build();
            assertEquals(defined, immutable.getDefaultSerializer());
        }
    }

    @DisplayName("Scenario: RestClientSerializers")
    @Nested class RestClientSerializerScenario {

        @DisplayName("Should discover and load all Rest Client Serializers from SPI")
        @Test void scenario1() {
            var immutable = conf.build();
            assertEquals(1, immutable.getRestClientSerializers().size());
            assertTrue(immutable.getRestClientSerializers().get("application/json") instanceof RestClientSerializer.JsonRestClientSerializer);
        }

        @DisplayName("Should return JsonRestClientSerializer as default serializer WHEN none was defined")
        @Test void scenario2() {
            var immutable = conf.build();
            assertTrue(immutable.getDefaultRestClientSerializer() instanceof RestClientSerializer.JsonRestClientSerializer);
        }

        @DisplayName("Should return custom RestClientSerializer WHEN defined via setter")
        @Test void scenario3() {
            var defined = mock(RestClientSerializer.class);
            conf.setDefaultRestClientSerializer(defined);

            var immutable = conf.build();
            assertEquals(defined, immutable.getDefaultRestClientSerializer());
        }
    }

    @DisplayName("Scenario: PayloadSerializationStrategy")
    @Nested class PayloadSerializationStrategyScenario {

        @DisplayName("Should return default serialization strategy WHEN no object was defined via setter")
        @Test void scenario1() {
            val result = conf.build();
            assertTrue(result.getPayloadSerializationStrategy() instanceof SingleSerializerStrategy);
        }

        @DisplayName("Should the object that was defined via setter")
        @Test void scenario2(){
            val defined = mock(PayloadSerializationStrategy.class);
            conf.setPayloadSerializationStrategy(defined);

            val result = conf.build();
            assertEquals(defined, result.getPayloadSerializationStrategy());
        }
    }

    @DisplayName("Scenario: LogDelegateFactory")
    @Nested class LogDelegateFactoryScenario {

        @DisplayName("Should discover a LogDelegateFactory from SPI WHEN no object was defined via setter")
        @Test void scenario1() {
            val discovered = mock(LogDelegateFactory.class);
            doReturn(Result.of(discovered)).when(spi).anyInstanceOf(eq(LogDelegateFactory.class));
            
            val result = conf.build();
            assertEquals(discovered, result.getLogDelegateFactory());
        }

        @DisplayName("Should the object that was defined via setter")
        @Test void scenario2(){
            val defined = mock(LogDelegateFactory.class);
            conf.setLogDelegateFactory(defined);

            val result = conf.build();
            assertEquals(defined, result.getLogDelegateFactory());
        }
    }

    @DisplayName("Scenario: Default Vertx")
    @Nested class VertxScenario {

        @DisplayName("Should create new Vertx instance WHEN no object was defined via setter")
        @Test void scenario1() {
            doReturn(Result.empty()).when(spi).anyInstanceOf(eq(VertxOptions.class));
            
            val result = conf.build();
            assertNotNull(result.getDefaultVertx());
        }

        @DisplayName("Should the object that was defined via setter")
        @Test void scenario2(){
            val defined = mock(Vertx.class);
            conf.setDefaultVertx(defined);

            val result = conf.build();
            assertEquals(defined, result.getDefaultVertx());
        }
    }

    @DisplayName("Scenario: StringConverter")
    @Nested class StringConverterScenario {

        @DisplayName("Should discover a StringConverter from SPI WHEN no object was defined via setter")
        @Test void scenario1() {
            val discovered = mock(StringConverter.class);
            doReturn(Result.of(discovered)).when(spi).anyInstanceOf(eq(StringConverter.class));
            
            val result = conf.build();
            assertEquals(discovered, result.getStringConverter());
        }

        @DisplayName("Should the object that was defined via setter")
        @Test void scenario2(){
            val defined = mock(StringConverter.class);
            conf.setStringConverter(defined);

            val result = conf.build();
            assertEquals(defined, result.getStringConverter());
        }
    }

    @DisplayName("Scenario: ExceptionHandler")
    @Nested class ExceptionHandlerScenario {

        @DisplayName("Should discover a ExceptionHandler from SPI WHEN no object was defined via setter")
        @Test void scenario1() {
            val discovered = mock(ExceptionHandler.class);
            doReturn(Result.of(discovered)).when(spi).anyInstanceOf(eq(ExceptionHandler.class));
            
            val result = conf.build();
            assertEquals(discovered, result.getExceptionHandler());
        }

        @DisplayName("Should the object that was defined via setter")
        @Test void scenario2(){
            val defined = mock(ExceptionHandler.class);
            conf.setExceptionHandler(defined);

            val result = conf.build();
            assertEquals(defined, result.getExceptionHandler());
        }
    }

    @DisplayName("Scenario: ConfigRetriever")
    @Nested class ConfigRetrieverScenario {

        @DisplayName("Should discover a ConfigRetriever from SPI WHEN no object was defined via setter")
        @Test void scenario1() {
            val discovered = mock(ConfigRetriever.class);
            doReturn(Result.of(discovered)).when(spi).anyInstanceOf(eq(ConfigRetriever.class));
            
            val result = conf.build();
            assertEquals(discovered, result.getConfigRetriever());
        }

        @DisplayName("Should the object that was defined via setter")
        @Test void scenario2(){
            val defined = mock(ConfigRetriever.class);
            conf.setConfigRetriever(defined);

            val result = conf.build();
            assertEquals(defined, result.getConfigRetriever());
        }
    }
}
