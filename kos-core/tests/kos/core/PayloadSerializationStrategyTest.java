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

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import kos.api.KosConfiguration;
import kos.api.MutableKosConfiguration;
import kos.api.PayloadSerializationStrategy;
import kos.api.Serializer;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@DisplayName("Behavior: Payload Serialization Strategy")
class PayloadSerializationStrategyTest {

    final static String PLAIN_TEXT = "text/plain";
    final static MultiMap HEADERS = MultiMap.caseInsensitiveMultiMap().add("Content-Type", PLAIN_TEXT);
    
    final MutableKosConfiguration kosConfiguration = new MutableKosConfiguration();

    @Mock HttpServerRequest request;
    @Mock HttpServerResponse response;

    @BeforeEach void setupMocks(){
        MockitoAnnotations.initMocks(this);
        doReturn(HEADERS).when(request).headers();
        doReturn(HEADERS).when(response).headers();
    }

    @DisplayName("useSerializer SHOULD throw exception WHEN loading Serializer for unknown Content-Type")
    @Test void useSerializerFailsUnknown(){
        assertThrows(KosException.class,
            () -> kosConfiguration.getAvailablePayloadStrategies().useSerializerForContentType("image/png") );
    }

    @DisplayName(
        "useSerializerForContentType SHOULD load Serializers only based on pre-defined Content-Type " +
        "WHEN asking for a Serializer for a given HttpServerRequest"
    )
    @Test void useSerializerPlainTextHttpServerRequest(){
        kosConfiguration.getAvailablePayloadStrategies().useSerializerForContentType(PLAIN_TEXT);

        val serializer = kosConfiguration.getPayloadSerializationStrategy().serializerFor(request);
        assertTrue(serializer instanceof Serializer.PlainTextSerializer);
    }

    @DisplayName(
        "useSerializer SHOULD load Serializers only based on pre-defined Content-Type " +
        "WHEN asking for a Serializer for a given HttpServerResponse"
    )
    @Test void useSerializerPlainTextHttpServerResponse(){
        kosConfiguration.getAvailablePayloadStrategies().useSerializerForContentType(PLAIN_TEXT);

        val serializer = kosConfiguration.getPayloadSerializationStrategy().serializerFor(response);
        assertTrue(serializer instanceof Serializer.PlainTextSerializer);
    }

    @DisplayName(
        "basedOnHttpHeader SHOULD load Serializers only based on HTTP Header defined in HttpServerRequest"
    )
    @Test void basedOnHttpHeaderPlainTextHttpServerRequest(){
        kosConfiguration.getAvailablePayloadStrategies().inferSerializerFromHttpHeader(PLAIN_TEXT);

        val serializer = kosConfiguration.getPayloadSerializationStrategy().serializerFor(request);
        assertTrue(serializer instanceof Serializer.PlainTextSerializer);
    }

    @DisplayName(
        "basedOnHttpHeader SHOULD load Serializers only based on HTTP Header defined in HttpServerResponse"
    )
    @Test void basedOnHttpHeaderPlainTextHttpServerResponse(){
        kosConfiguration.getAvailablePayloadStrategies().inferSerializerFromHttpHeader(PLAIN_TEXT);

        val serializer = kosConfiguration.getPayloadSerializationStrategy().serializerFor(response);
        assertTrue(serializer instanceof Serializer.PlainTextSerializer);
    }
}