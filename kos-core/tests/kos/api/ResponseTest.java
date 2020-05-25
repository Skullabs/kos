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

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import kos.core.Lang;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static io.vertx.core.http.HttpMethod.GET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ResponseTest {

    @Mock HttpServerRequest serverRequest;
    @Mock HttpServerResponse serverResponse;
    @Mock RoutingContext routingContext;

    @BeforeEach void setupMocks(){
        MockitoAnnotations.initMocks(this);

        doReturn(serverResponse).when(routingContext).response();
        doReturn(serverRequest).when(routingContext).request();

        doReturn(GET).when(serverRequest).method();
        doReturn("/").when(serverRequest).uri();
    }

    @DisplayName("empty() SHOULD create an empty Response object")
    @Test void empty(){
        val response = Response.empty();
        assertTrue(response instanceof EmptyResponse);
        assertEquals(200, response.statusCode());

        response.send(serverResponse);
        verify(serverResponse).end();
        verifyNoMoreInteractions(serverResponse);
    }

    @DisplayName("empty(status) SHOULD create empty message with a pre-defined Http Status")
    @Test void emptyStatus(){
        val response = Response.empty(256);
        assertTrue(response instanceof EmptyResponse);
        assertEquals(256, response.statusCode());

        response.send(serverResponse);
        verify(serverResponse).end();
        verifyNoMoreInteractions(serverResponse);
    }

    @DisplayName("wrap(Buffer) SHOULD create empty message with a pre-defined body")
    @Test void wrapBuffer(){
        val buffer = Buffer.buffer("Hello World");
        val response = Response.wrap(buffer);
        assertTrue(response instanceof RawResponse);
        assertEquals(200, response.statusCode());

        response.send(serverResponse);
        verify(serverResponse).end(eq(buffer));
        verifyNoMoreInteractions(serverResponse);
    }

    @DisplayName("of(T) SHOULD create empty response with a serializable object")
    @Test void of(){
        val response = Response.of(Boolean.TRUE);
        assertTrue(response instanceof SerializableResponse);
        assertEquals(200, response.statusCode());

        response.send(serverResponse);
        verify(serverResponse).putHeader(eq(CONTENT_TYPE), eq((CharSequence)"application/json"));
        verify(serverResponse).end(eq(Buffer.buffer("true")));
        verifyNoMoreInteractions(serverResponse);
    }

    @DisplayName(
        "Response.send(RoutingContext, Response) " +
        "SHOULD send the response to the client including status, headers " +
        "and serializing the response payload"
    )
    @Test void sendRoutingContextResponse(){
        val response = Response.of("Hello World")
            .headers( Lang.mapOf("X-Token", "123456").build())
            .statusCode(201);

        Response.send(routingContext, response);
        verify(serverResponse).setStatusCode(eq(201));
        verify(serverResponse).putHeader(eq((CharSequence)"X-Token"), eq((CharSequence)"123456"));
        verify(serverResponse).putHeader(eq(CONTENT_TYPE), eq((CharSequence)"application/json"));
        verify(serverResponse).end(eq(Buffer.buffer("\"Hello World\"")));
        verifyNoMoreInteractions(serverResponse);
    }

    @DisplayName(
        "Response.send(RoutingContext, Object) " +
        "SHOULD send the response to the client including 200 status " +
        "and serializing the response payload"
    )
    @Test void sendRoutingContextObject(){
        Response.send(routingContext, "Hello World");
        verify(serverResponse).setStatusCode(eq(200));
        verify(serverResponse).putHeader(eq(CONTENT_TYPE), eq((CharSequence)"application/json"));
        verify(serverResponse).end(eq(Buffer.buffer("\"Hello World\"")));
        verifyNoMoreInteractions(serverResponse);
    }

    @DisplayName(
        "Response.sendError(RoutingContext, Throwable) " +
        "SHOULD send the response to the client including HTTP status " +
        "and serializing the exception as response payload"
    )
    @Test void sendRoutingContextThrowable(){
        Response.sendError(routingContext, new UnhandledException());
        verify(serverResponse).setStatusCode(eq(500));
        verify(serverResponse).putHeader(eq(CONTENT_TYPE), eq((CharSequence)"text/plain"));
        verify(serverResponse).end(eq(Buffer.buffer("kos.core.UnhandledException\n")));
        verifyNoMoreInteractions(serverResponse);
    }
}

class UnhandledException extends RuntimeException {

    UnhandledException(){
        super(null, null, false, false);
    }
}