package kos.core.client;

import io.vertx.ext.web.client.WebClient;
import kos.api.KosContext;
import kos.api.MutableKosContext;
import kos.api.StringConverter;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RestClientConfigurationTest {
    
    String baseUrl = "https://example.me";

    @DisplayName("Scenario: Configuration has not been fully defined")
    @Nested class ConfigurationNotFinished {

        @Test void scenario1() {
            assertThrows(NullPointerException.class, () -> RestClientConfiguration.defaults().build());
        }

        @Test void scenario2() {
            val conf = RestClientConfiguration.withUrl(baseUrl).build();
            assertTrue(conf.isEmpty());
        }

        @Test void scenario3() {
            val conf = RestClientConfiguration
                    .withUrl(baseUrl)
                    .client(mock(WebClient.class))
                    .build();
            assertTrue(conf.isEmpty());
        }

        @Test void scenario4() {
            val conf = RestClientConfiguration
                    .withUrl(baseUrl)
                    .client(mock(WebClient.class))
                    .restClientSerializer(mock(RestClientSerializer.class))
                    .build();
            assertTrue(conf.isEmpty());
        }

        @Test void scenario5() {
            val conf = RestClientConfiguration
                    .withUrl(baseUrl)
                    .client(mock(WebClient.class))
                    .restClientSerializer(mock(RestClientSerializer.class))
                    .stringConverter(mock(StringConverter.class))
                    .build();
            assertFalse(conf.isEmpty());
        }
    }

    @DisplayName("Scenario: Using default values from KosConfiguration for those undefined")
    @Nested class UsingDefaultValues {
        
        KosContext kosContext = new MutableKosContext();
        
        @Test void scenario1(){
            val conf = RestClientConfiguration.withUrl(baseUrl).build();
            val withDefaults = conf.useDefaultsForNullProperties(kosContext);
            assertEquals(withDefaults.getClient(), kosContext.getDefaultVertxWebClient());
            assertEquals(withDefaults.getRestClientSerializer(), kosContext.getDefaultRestClientSerializer());
            assertEquals(withDefaults.getStringConverter(), kosContext.getStringConverter());
        }
        
        @Test void scenario2(){
            val conf = RestClientConfiguration.withUrl(baseUrl)
                    .client(mock(WebClient.class))
                    .build();

            val withDefaults = conf.useDefaultsForNullProperties(kosContext);
            assertNotEquals(withDefaults.getClient(), kosContext.getDefaultVertxWebClient());
            assertEquals(withDefaults.getRestClientSerializer(), kosContext.getDefaultRestClientSerializer());
            assertEquals(withDefaults.getStringConverter(), kosContext.getStringConverter());
        }
        
        @Test void scenario3(){
            val conf = RestClientConfiguration.withUrl(baseUrl)
                    .client(mock(WebClient.class))
                    .restClientSerializer(mock(RestClientSerializer.class))
                    .build();

            val withDefaults = conf.useDefaultsForNullProperties(kosContext);
            assertNotEquals(withDefaults.getClient(), kosContext.getDefaultVertxWebClient());
            assertNotEquals(withDefaults.getRestClientSerializer(), kosContext.getDefaultRestClientSerializer());
            assertEquals(withDefaults.getStringConverter(), kosContext.getStringConverter());
        }
        
        @Test void scenario4(){
            val conf = RestClientConfiguration.withUrl(baseUrl)
                    .client(mock(WebClient.class))
                    .restClientSerializer(mock(RestClientSerializer.class))
                    .stringConverter(mock(StringConverter.class))
                    .build();

            val withDefaults = conf.useDefaultsForNullProperties(kosContext);
            assertNotEquals(withDefaults.getClient(), kosContext.getDefaultVertxWebClient());
            assertNotEquals(withDefaults.getRestClientSerializer(), kosContext.getDefaultRestClientSerializer());
            assertNotEquals(withDefaults.getStringConverter(), kosContext.getStringConverter());
        }
    }
}