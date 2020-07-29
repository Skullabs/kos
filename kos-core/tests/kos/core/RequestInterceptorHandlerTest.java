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
import io.vertx.core.http.*;
import kos.api.RequestInterceptor;
import lombok.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@DisabledOnJre(JRE.JAVA_8)
class RequestInterceptorHandlerTest {

    RequestInterceptorHandler interceptorHandler;

    @Mock Handler<HttpServerRequest> wrappedHandler;
    @Mock HttpServerRequest request;
    @Mock HttpServerResponse response;

    @BeforeEach void setupMocks(){
        MockitoAnnotations.initMocks(this);
        doReturn(response).when(request).response();

        interceptorHandler = new RequestInterceptorHandler(wrappedHandler);
    }

    @DisplayName("SHOULD be able to intercept requests and avoid the chain to be executed")
    @Test void handle(){
        val interceptor = mock(RequestInterceptor.class);
        interceptorHandler.register(interceptor);
        interceptorHandler.handle(request);

        verify( interceptor ).handle( eq(request), eq(wrappedHandler) );
        verifyZeroInteractions(wrappedHandler);
    }

    @DisplayName("SHOULD call the wrapped handler WHEN no interceptor is registered")
    @Test void handle1(){
        interceptorHandler.handle(request);
        verify(wrappedHandler).handle(eq(request));
    }
}