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

import io.vertx.core.http.*;
import io.vertx.ext.web.*;
import kos.api.RequestHandler;
import lombok.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisabledOnJre(JRE.JAVA_8)
class RequestHandlerTest {

    @Mock RoutingContext context;
    @Mock HttpServerRequest request;
    @Mock HttpServerResponse response;

    @BeforeEach void setupMocks(){
        MockitoAnnotations.initMocks(this);
        doReturn( request ).when(context).request();
        doReturn( response ).when(context).response();
    }

    @DisplayName("SHOULD delegate execution to RequestHandler.handler(req, resp)")
    @Test void handle(){
        val handler = mock(RequestHandler.class);
        val wrapped = (RequestHandler) handler::handle;
        wrapped.handle( context );
        verify( handler ).handle( eq(request), eq(response) );
    }
}
