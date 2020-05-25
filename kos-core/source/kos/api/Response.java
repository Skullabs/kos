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

import io.vertx.core.buffer.*;
import io.vertx.core.http.*;
import io.vertx.ext.web.*;
import kos.core.Kos;
import lombok.*;
import lombok.experimental.*;

import java.util.*;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

/**
 * Represents a (mutable object) response to be sent to the http client.
 */
public interface Response {

    int statusCode();
    Response statusCode(int value);

    Map<? extends CharSequence, ? extends CharSequence> headers();
    Response headers(Map<? extends CharSequence, ? extends CharSequence> value);

    void send( HttpServerResponse response );

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
     * object using a previously defined {@link PayloadSerializationStrategy}. This method
     * though is a semantically-equivalent to {@link Response#send(HttpServerResponse)},
     * but with different syntax.
     *
     * @param context
     * @param payload
     */
    static void send(RoutingContext context, Object payload) {
        val httpResponse = context.response();
        val serializer = Kos.payloadSerializationStrategy.serializerFor(httpResponse);
        val buffer = serializer.serialize(payload);
        httpResponse.setStatusCode(200);
        httpResponse.putHeader(CONTENT_TYPE, serializer.contentType());
        httpResponse.end(buffer);
    }

    /**
     * Sends a response to the client. It will ensure that the defined statusCode
     * and headers are send to the client. It also uses {@link Response#send(HttpServerResponse)}
     * to perform the serialization, allowing developers to design custom and powerful
     * serialization mechanisms.
     */
    static void send(RoutingContext context, Response response) {
        val serverResponse = context.response();
        serverResponse.setStatusCode(response.statusCode());
        for ( val header : response.headers().entrySet() )
            serverResponse.putHeader(header.getKey(), header.getValue());
        response.send(serverResponse);
    }

    /**
     * Serializes a {@link Throwable} and sends as a response to the client.
     * Internally it will use the appropriate {@link ExceptionHandler} to
     * handle the failure and generate the response object.
     */
    static void sendError(RoutingContext context, Throwable cause) {
        val handledResponse = Kos.exceptionHandler.get().handle(context, cause);
        send(context, handledResponse);
    }

    /**
     * Sends an empty response to the client.
     *
     * @see Kos#defaultStatusForEmptyResponses
     */
    static void sendDefaultNoContent(RoutingContext context) {
        val serverResponse = context.response();
        serverResponse.setStatusCode(Kos.defaultStatusForEmptyResponses);
        serverResponse.end();
    }
}

@Data @Accessors(fluent = true)
class EmptyResponse implements Response {

    int statusCode = 200;
    Map<? extends CharSequence, ? extends CharSequence> headers =
        Collections.emptyMap();

    @Override
    public void send(HttpServerResponse response) {
        response.end();
    }
}

@RequiredArgsConstructor
class RawResponse extends EmptyResponse {
    
    final Buffer buffer;

    @Override
    public void send(HttpServerResponse response) {
        response.end(buffer);
    }
}

@RequiredArgsConstructor
class SerializableResponse<T> extends EmptyResponse {
    
    final T payload;

    @Override
    public void send(HttpServerResponse response) {
        val serializer = Kos.payloadSerializationStrategy.serializerFor(response);
        val buffer = serializer.serialize(payload);
        response.putHeader(CONTENT_TYPE, serializer.contentType());
        response.end(buffer);
    }
}