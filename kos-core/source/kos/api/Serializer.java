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
import io.vertx.core.json.*;
import kos.core.Kos;
import lombok.*;

public interface Serializer {

    Buffer serialize(Object target);

    <T> T deserialize( Buffer buffer, Class<T> type );

    String contentType();

    class JsonSerializer implements Serializer {

        @Override
        public String contentType() {
            return "application/json";
        }

        @Override
        public Buffer serialize(Object target) {
            return Json.encodeToBuffer(target);
        }

        @Override
        public <T> T deserialize(Buffer buffer, Class<T> type) {
            return Json.decodeValue( buffer, type );
        }
    }

    class PlainTextSerializer implements Serializer {

        @Override
        public Buffer serialize(@NonNull Object target) {
            return Buffer.buffer(target.toString());
        }

        @Override
        public <T> T deserialize(Buffer buffer, Class<T> type) {
            val string = buffer.toString();
            return Kos.stringConverter.get()
                  .convertTo( type, string );
        }

        @Override
        public String contentType() {
            return "text/plain";
        }
    }
}
