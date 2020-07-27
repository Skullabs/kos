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

package kos.core;

import io.vertx.core.*;
import io.vertx.core.http.HttpServerRequest;
import kos.api.RequestInterceptor;
import lombok.*;

/**
 *
 */
@AllArgsConstructor
public class RequestInterceptorHandler implements Handler<HttpServerRequest> {

    private Handler<HttpServerRequest> currentHandler;

    public void register( RequestInterceptor next ) {
        currentHandler = new InterceptorWrapper(next, currentHandler);
    }

    @Override
    public void handle(HttpServerRequest request) {
        currentHandler.handle(request);
    }

    @RequiredArgsConstructor
    private static class InterceptorWrapper implements Handler<HttpServerRequest> {

        final RequestInterceptor interceptor;
        final Handler<HttpServerRequest> next;

        @Override
        public void handle(HttpServerRequest request) {
            this.interceptor.handle(request, this.next);
        }
    }
}