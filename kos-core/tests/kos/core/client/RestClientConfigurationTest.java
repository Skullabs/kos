package kos.core.client;

import io.vertx.core.MultiMap;
import io.vertx.ext.web.client.WebClient;
import kos.api.KosConfiguration;
import kos.api.MutableKosConfiguration;
import kos.api.StringConverter;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URL;

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
        
        KosConfiguration kosConfiguration = new MutableKosConfiguration();
        
        @Test void scenario1(){
            val conf = RestClientConfiguration.withUrl(baseUrl).build();
            val withDefaults = conf.useDefaultsForNullProperties(kosConfiguration);
            assertEquals(withDefaults.getClient(), kosConfiguration.getDefaultVertxWebClient());
            assertEquals(withDefaults.getRestClientSerializer(), kosConfiguration.getDefaultRestClientSerializer());
            assertEquals(withDefaults.getStringConverter(), kosConfiguration.getStringConverter());
        }
        
        @Test void scenario2(){
            val conf = RestClientConfiguration.withUrl(baseUrl)
                    .client(mock(WebClient.class))
                    .build();

            val withDefaults = conf.useDefaultsForNullProperties(kosConfiguration);
            assertNotEquals(withDefaults.getClient(), kosConfiguration.getDefaultVertxWebClient());
            assertEquals(withDefaults.getRestClientSerializer(), kosConfiguration.getDefaultRestClientSerializer());
            assertEquals(withDefaults.getStringConverter(), kosConfiguration.getStringConverter());
        }
        
        @Test void scenario3(){
            val conf = RestClientConfiguration.withUrl(baseUrl)
                    .client(mock(WebClient.class))
                    .restClientSerializer(mock(RestClientSerializer.class))
                    .build();

            val withDefaults = conf.useDefaultsForNullProperties(kosConfiguration);
            assertNotEquals(withDefaults.getClient(), kosConfiguration.getDefaultVertxWebClient());
            assertNotEquals(withDefaults.getRestClientSerializer(), kosConfiguration.getDefaultRestClientSerializer());
            assertEquals(withDefaults.getStringConverter(), kosConfiguration.getStringConverter());
        }
        
        @Test void scenario4(){
            val conf = RestClientConfiguration.withUrl(baseUrl)
                    .client(mock(WebClient.class))
                    .restClientSerializer(mock(RestClientSerializer.class))
                    .stringConverter(mock(StringConverter.class))
                    .build();

            val withDefaults = conf.useDefaultsForNullProperties(kosConfiguration);
            assertNotEquals(withDefaults.getClient(), kosConfiguration.getDefaultVertxWebClient());
            assertNotEquals(withDefaults.getRestClientSerializer(), kosConfiguration.getDefaultRestClientSerializer());
            assertNotEquals(withDefaults.getStringConverter(), kosConfiguration.getStringConverter());
        }
    }
}