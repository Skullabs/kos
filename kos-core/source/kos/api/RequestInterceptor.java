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

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;

public interface RequestInterceptor {

    /**
     * Handles a request. If the {@code next} interceptor in the chain
     * were not called, the request will be interrupted. Please make sure
     * you have sent a response to the client if you choose to interrupt
     * the request chain.
     *
     * @param request Request object
     * @param next Next interceptor in the chain.
     */
    void handle(HttpServerRequest request, Handler<HttpServerRequest> next);
}
