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
import io.vertx.core.logging.*;
import io.vertx.ext.web.*;
import kos.core.Lang;
import lombok.*;

import java.io.*;
import java.util.*;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

/**
 * Versatile exception handler abstraction.
 */
public interface ExceptionHandler {

    /**
     * Handles exceptions by returning a {@link Response} object that
     * will be send as http response to the client.
     *
     * @param request current request context
     * @param cause error cause
     * @return object that will be send as http response to the client
     */
    Response handle( RoutingContext request, Throwable cause );

    class DefaultExceptionHandler implements ExceptionHandler {

        private final Lang.Lazy<Logger> log;
        
        public DefaultExceptionHandler(KosConfiguration kosConfiguration) {
            log = Lang.Lazy.by( () -> kosConfiguration.createLoggerFor(DefaultExceptionHandler.class) );
        }

        @Override
        public Response handle( RoutingContext request, Throwable cause ) {
            val msg = String.format( "Failed to handle request: %s - %s",
                request.request().method(), request.request().uri() );
            log.get().error( msg, cause );

            val stackTrace = new StringWriter();
            cause.printStackTrace(new PrintWriter(stackTrace));

            val serialized = Buffer.buffer(stackTrace.toString());
            val headers = Collections.singletonMap(CONTENT_TYPE, "text/plain");
            return Response.wrap(serialized).statusCode(500).headers(headers);
        }
    }
}
