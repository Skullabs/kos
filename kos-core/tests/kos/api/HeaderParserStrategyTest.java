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

import io.vertx.core.http.*;
import io.vertx.core.http.impl.headers.*;
import lombok.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class HeaderParserStrategyTest {

    KosContext kosContext = new MutableKosContext();
    HeaderParserStrategy strategy = new HeaderParserStrategy(kosContext, HttpHeaders.CONTENT_TYPE, "application/json");

    @Test void basedOnContentType() {
        val headers = new HeadersMultiMap();
        headers.set(HttpHeaders.CONTENT_TYPE, "text/plain; charset=us-ascii");

        val parsed = strategy.parseContentTypeHeader(headers);
        assertEquals("text/plain", parsed);
    }

    @Test void basedOnContentType1() {
        val headers = new HeadersMultiMap();
        headers.set(HttpHeaders.CONTENT_TYPE, "text/plain");

        val parsed = strategy.parseContentTypeHeader(headers);
        assertEquals("text/plain", parsed);
    }
}