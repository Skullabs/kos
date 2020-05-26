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

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import kos.api.MutableKosConfiguration;
import kos.api.RequestHandler;
import kos.api.RequestInterceptor;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static io.vertx.core.http.HttpMethod.GET;
import static org.mockito.Mockito.*;

class SimplifiedRouterTest {

    @Mock RequestHandler requestHandler;
    @Mock HttpServerRequest request;
    @Mock HttpServerResponse response;

    SimplifiedRouter simplifiedRouter;

    @BeforeEach void setup(){
        MockitoAnnotations.initMocks(this);
        doReturn("/").when(request).path();
        doReturn(GET).when(request).method();
        doReturn(response).when(request).response();

        val router = Router.router(Kos.defaultVertx.get());
        simplifiedRouter = SimplifiedRouter.wrapWithAutoBodyReader(new MutableKosConfiguration(), router);
    }

    @DisplayName("Interceptors SHOULD be able to interrupt executions avoiding handlers for being called")
    @Test void intercept()
    {
        val interceptor = mock(RequestInterceptor.class);
        simplifiedRouter.route(GET, "/", requestHandler);
        simplifiedRouter.intercept(interceptor);

        simplifiedRouter.handle(request);
        verify(interceptor).handle(eq(request), any());
        verifyZeroInteractions(requestHandler);
    }

    @DisplayName("Interceptors SHOULD be able to allow the request chain to be called")
    @Test void intercept1()
    {
        simplifiedRouter.route(GET, "/", requestHandler);
        simplifiedRouter.intercept(new ByPassInterceptor());

        simplifiedRouter.handle(request);
        verify(requestHandler).handle(any());
    }
}

class ByPassInterceptor implements RequestInterceptor {

    @Override
    public void handle(HttpServerRequest request, Handler<HttpServerRequest> next) {
        next.handle(request);
    }
}