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
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.HttpResponse;
import lombok.Getter;
import lombok.experimental.Accessors;

public interface Serializer {

    String contentType();

    Buffer serialize(Object target);

    <T> T deserialize(HttpResponse<Buffer> response, Class<T> type);
    <T> T deserialize(HttpResponse<Buffer> response, TypeReference<T> type);

    @Accessors(fluent = true)
    class JsonSerializer implements Serializer {

        @Getter
        final String contentType = "application/json";

        @Override
        public Buffer serialize(Object target) {
            return Json.encodeToBuffer(target);
        }

        @Override
        public <T> T deserialize(HttpResponse<Buffer> response, Class<T> type) {
            return Json.decodeValue(response.body(), type);
        }

        @Override
        public <T> T deserialize(HttpResponse<Buffer> response, TypeReference<T> type) {
            return Json.decodeValue(response.body(), type);
        }
    }
}
