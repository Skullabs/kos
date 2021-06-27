/*
 * Copyright 2019-2021 Skullabs Contributors (https://github.com/skullabs)
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
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

/**
 * Represents a (mutable object) response to be sent to the http client.
 */
public interface Response {

    int statusCode();
    Response statusCode(int value);

    Map<? extends CharSequence, ? extends CharSequence> headers();
    Response headers(Map<? extends CharSequence, ? extends CharSequence> value);

    void send(KosContext kosContext, HttpServerResponse response );

    /**
     * Creates an empty response.
     */
    static Response empty(){
        return new EmptyResponse();
    }

    /**
     * Creates an empty response for a given {@code statusCode}.
     */
    static Response empty(int statusCode){
        return new EmptyResponse().statusCode(statusCode);
    }

    /**
     * Wraps a Buffer into a response.
     */
    static Response wrap(Buffer serialized) {
        return new RawResponse(serialized);
    }

    /**
     * Wraps an object as a response.
     */
    static <T> Response of( T payload ) {
        return new SerializableResponse<>(payload);
    }

    /**
     * Wraps a nullable object as a response.
     */
    static <T> Response ofNullable( T payload ) {
        if ( payload == null )
            return empty();
        else
            return new SerializableResponse<>(payload);
    }

    // Common responses with no payload
    Response
        CREATED = Response.empty(201),
        ACCEPTED = Response.empty(202),
        NO_CONTENT = Response.empty(204),
        BAD_REQUEST = Response.empty(400),
        UNAUTHORIZED = Response.empty(401),
        FORBIDDEN = Response.empty(403),
        NOT_FOUND = Response.empty(404),
        METHOD_NOT_ALLOWED = Response.empty(405),
        NOT_ACCEPTABLE = Response.empty(406),
        CONFLICT = Response.empty(409),
        GONE = Response.empty(410),
        TOO_MANY_REQUESTS = Response.empty(429),
        NOT_IMPLEMENTED = Response.empty(501),
        BAD_GATEWAY = Response.empty(502),
        SERVICE_UNAVAILABLE = Response.empty(503),
        GATEWAY_TIMEOUT = Response.empty(504);

    /**
     * Sends a response to the client. Internally it serializes the {@code payload}
     * object using a previously defined {@link PayloadSerializationStrategy}. Although this method
     * is a semantically-equivalent to {@link Response#send(KosContext,HttpServerResponse)}
     * it was designed to be called directly by the generated routes.
     */
    static void send(KosContext kosContext, RoutingContext context, Object payload) {
        val httpResponse = context.response();
        send(kosContext, httpResponse, payload);
    }

    /**
     * Sends a response to the client. Internally it serializes the {@code payload}
     * object using a previously defined {@link PayloadSerializationStrategy}. Although this method
     * is a semantically-equivalent to {@link Response#send(KosContext,HttpServerResponse)}
     * it was designed to be called directly by the generated routes.
     */
    static void send(KosContext kosContext, HttpServerResponse httpResponse, Object payload) {
        val serializer = kosContext.getPayloadSerializationStrategy().serializerFor(httpResponse);
        val buffer = serializer.serialize(payload);
        httpResponse.setStatusCode(200);
        httpResponse.putHeader(CONTENT_TYPE, serializer.contentType());
        httpResponse.end(buffer);
    }

    /**
     * Sends a response to the client. It will delegate to
     * {@link Response#send(KosContext, HttpServerResponse)}
     * to perform the serialization, allowing developers to design custom and powerful
     * serialization mechanisms.
     */
    static void send(KosContext kosContext, RoutingContext context, Response response) {
        val serverResponse = context.response();
        response.send(kosContext, serverResponse);
    }

    /**
     * Serializes a {@link Throwable} and sends as a response to the client.
     * Internally it will use the appropriate {@link ExceptionHandler} to
     * handle the failure and generate the response object.
     */
    static void sendError(KosContext kosContext, RoutingContext context, Throwable cause) {
        sendError(kosContext, context.request(), cause);
    }

    /**
     * Serializes a {@link Throwable} and sends as a response to the client.
     * Internally it will use the appropriate {@link ExceptionHandler} to
     * handle the failure and generate the response object.
     */
    static void sendError(KosContext kosContext, HttpServerRequest request, Throwable cause) {
        val httpResponse = request.response();
        val handledResponse = kosContext.getExceptionHandler()
            .handle(request, cause);
        handledResponse.send(kosContext, httpResponse);
    }

    /**
     * Sends an empty response to the client.
     *
     * @see KosContext#getExceptionHandler()
     */
    static void sendDefaultNoContent(KosContext kosContext, RoutingContext context) {
        val serverResponse = context.response();
        serverResponse.setStatusCode(kosContext.getDefaultStatusForEmptyResponses());
        serverResponse.end();
    }
}

@Data @Accessors(fluent = true)
class EmptyResponse implements Response {

    int statusCode = 200;
    Map<? extends CharSequence, ? extends CharSequence> headers = new HashMap<>();

    @Override
    public void send(KosContext kosContext, HttpServerResponse response) {
        this.sendStatusAndHeader(response);
        response.end();
    }

    protected void sendStatusAndHeader(HttpServerResponse response) {
        response.setStatusCode(this.statusCode);
        for ( val header : headers().entrySet() )
            response.putHeader(header.getKey(), header.getValue());
    }
}

@RequiredArgsConstructor
class RawResponse extends EmptyResponse {
    
    final Buffer buffer;

    @Override
    public void send(KosContext kosContext, HttpServerResponse response) {
        this.sendStatusAndHeader(response);
        response.end(this.buffer);
    }
}

@RequiredArgsConstructor
class SerializableResponse<T> extends EmptyResponse {
    
    final T payload;

    @Override
    public void send(KosContext kosContext, HttpServerResponse response) {
        val serializer = kosContext.getPayloadSerializationStrategy().serializerFor(response);
        val buffer = serializer.serialize(payload);
        this.sendStatusAndHeader(response);
        response.putHeader(CONTENT_TYPE, serializer.contentType());
        response.end(buffer);
    }
}