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

import io.vertx.core.*;
import io.vertx.core.http.*;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Defines how Kos will handle the serialization and deserialization
 * for every received request.
 */
public interface PayloadSerializationStrategy {

    Serializer serializerFor(HttpServerResponse response);

    Serializer serializerFor(HttpServerRequest request);

}

@RequiredArgsConstructor
class SingleSerializerStrategy implements PayloadSerializationStrategy {

    final Serializer serializer;

    @Override
    public Serializer serializerFor(HttpServerResponse response) {
        return serializer;
    }

    @Override
    public Serializer serializerFor(HttpServerRequest request) {
        return serializer;
    }
}

@RequiredArgsConstructor
class HeaderParserStrategy implements PayloadSerializationStrategy {

    final KosContext kosContext;
    final CharSequence header;
    final String defaultContentType;

    @Override
    public Serializer serializerFor(HttpServerResponse response) {
        val contentType = parseContentTypeHeader(response.headers());
        return kosContext.getSerializerForContentType(contentType);
    }

    @Override
    public Serializer serializerFor(HttpServerRequest request) {
        val contentType = parseContentTypeHeader(request.headers());
        return kosContext.getSerializerForContentType(contentType);
    }

    String parseContentTypeHeader(MultiMap headers) {
        var value = headers.get(header);
        if (value == null)
            value = defaultContentType;
        else {
            var pos = value.indexOf(';');
            value = value.substring(0, pos > -1 ? pos : value.length());
        }
        return value;
    }
}
