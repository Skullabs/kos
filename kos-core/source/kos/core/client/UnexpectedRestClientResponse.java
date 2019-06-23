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

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import kos.core.KosException;
import lombok.Getter;

/**
 * Exception thrown whenever the client faces an unexpected
 * response received from the web service.
 */
public final class UnexpectedRestClientResponse extends KosException {

    @Getter
    private final HttpResponse<Buffer> response;

    UnexpectedRestClientResponse(String expectedCondition, HttpResponse<Buffer> response) {
        super("Received response didn't match expected precondition: " + expectedCondition);
        this.response = response;
    }
}