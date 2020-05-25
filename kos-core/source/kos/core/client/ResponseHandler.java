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

package kos.core.client;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Generic response handler for auto-generated web clients. It
 * wraps all complex logic required to handle responses properly
 * letting the auto-generated stubs only in charge of retrieving
 * parameters and sending down to this class.
 *
 * @param <T> expected response type
 */
@RequiredArgsConstructor
public abstract class ResponseHandler<T>
        implements Handler<AsyncResult<HttpResponse<Buffer>>> {

    private static final String MSG_UNEXPECTED_RESPONSE = "Expected status between 200 and 299";

    private final Promise<T> promise = Promise.promise();
    protected final RestClientSerializer restClientSerializer;

    @Override
    public void handle(AsyncResult<HttpResponse<Buffer>> event) {
        try {
            if (event.succeeded()) tryHandle(event);
            else promise.fail(event.cause());
        } catch (Throwable cause) {
            promise.fail(cause);
        }
    }

    private void tryHandle(AsyncResult<HttpResponse<Buffer>> event){
        val response = event.result();
        if (response.statusCode() > 299) {
            val msg = MSG_UNEXPECTED_RESPONSE + ", received " + response.statusCode();
            promise.fail(new UnexpectedRestClientResponse(msg, response));
        } else {
            val decoded = deserialize(response);
            if (decoded == null)
                promise.complete();
            else
                promise.complete(decoded);
        }
    }

    protected abstract T deserialize(HttpResponse<Buffer> response);

    public Future<T> future(){
        return promise.future();
    }

    public static <T> ResponseHandler<T> create(RestClientSerializer restClientSerializer, Class<T> targetClass){
        return new SimpleTypeResponseHandler<>(restClientSerializer, targetClass);
    }

    public static <T> ResponseHandler<T> create(RestClientSerializer restClientSerializer, TypeReference<T> targetClass){
        return new TypeReferenceResponseHandler<>(restClientSerializer, targetClass);
    }
}

class SimpleTypeResponseHandler<T> extends ResponseHandler<T> {

    private final Class<T> targetClass;

    SimpleTypeResponseHandler(RestClientSerializer restClientSerializer, Class<T> targetClass) {
        super(restClientSerializer);
        this.targetClass = targetClass;
    }

    @Override
    protected T deserialize(HttpResponse<Buffer> response) {
        return restClientSerializer.deserialize(response, targetClass);
    }
}

class TypeReferenceResponseHandler<T> extends ResponseHandler<T> {

    private final TypeReference<T> typeReference;

    TypeReferenceResponseHandler(RestClientSerializer restClientSerializer, TypeReference<T> typeReference) {
        super(restClientSerializer);
        this.typeReference = typeReference;
    }

    @Override
    protected T deserialize(HttpResponse<Buffer> response) {
        return restClientSerializer.deserialize(response, typeReference);
    }
}