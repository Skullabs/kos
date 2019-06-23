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
import io.vertx.core.logging.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.handler.*;
import lombok.*;
import lombok.experimental.*;

import java.util.*;

/**
 * Small layer over {@link Router} that aims to simplify a few common
 * aspects regarding route configuration, like intercepting requests
 * and the reading body requests in a POST, PUT and PATCH requests.
 */
@Accessors(fluent = true)
public class SimplifiedRouter implements Handler<HttpServerRequest> {

    private static final Logger log = Kos.logger(SimplifiedRouter.class);

    private final Router router;
    private final RequestInterceptorHandler interceptorHandler;
    private final Map<HttpMethod, Boolean> httpMethodsThatMayReadBody;

    public SimplifiedRouter(Router router, Map<HttpMethod, Boolean> httpMethodsThatMayReadBody) {
        this(router, new RequestInterceptorHandler(router), httpMethodsThatMayReadBody);
    }

    private SimplifiedRouter(Router router, RequestInterceptorHandler interceptor, Map<HttpMethod, Boolean> httpMethodsThatMayReadBody) {
        this.interceptorHandler = interceptor;
        this.router = router;
        this.httpMethodsThatMayReadBody = httpMethodsThatMayReadBody;
    }

    /**
     * Returns the wrapped Vert.x {@link Router}.
     * @return the wrapped Vert.x {@link Router}
     */
    public Router unwrap(){
        return router;
    }

    /**
     * Registers a {@link Handler} to intercept all requests.
     *
     * @param interceptor the interceptor handler.
     */
    public void intercept( RequestInterceptor interceptor ) {
        log.info("Registering interceptor " + interceptor.getClass().getCanonicalName() );
        interceptorHandler.register( interceptor );
    }

    /**
     * Routes requests made to {@code path} to a particular handler.
     *
     * @param method Http Method used by this endpoint
     * @param path relative URI used by this endpoint
     * @param handler request handler
     */
    public void route( HttpMethod method, String path, RequestHandler handler ){
        route( method, path, (Handler<RoutingContext>) handler );
    }

    /**
     * Routes requests made to {@code path} to a particular handler.
     *
     * @param method Http Method used by this endpoint
     * @param path relative URI used by this endpoint
     * @param handler request handler
     */
    public void route( HttpMethod method, String path, Handler<RoutingContext> handler ){
        log.info("Registering router "+method+" "+path );
        val isFirstTimeThisMethodIsUsed = httpMethodsThatMayReadBody.replace( method, false, true );

        if (isFirstTimeThisMethodIsUsed)
            router.route().method(method).handler( BodyHandler.create() );

        router.route( method, path )
            .handler( SafeRoutingContextHandler.wrap(handler) );

        log.debug( "Registered " + handler.getClass() );
    }

    /**
     * Creates an instance of {@link SimplifiedRouter} that will automatically read the
     * request body payload for PATCH, POST and PUT.
     *
     * @param router the router instance that will be wrapped
     * @return An instance of {@link SimplifiedRouter}
     */
    public static SimplifiedRouter wrapWithAutoBodyReader( Router router ){
        val methodsThatWillReadBodyBeforeExec =
            Lang.mapOf( HttpMethod.POST, false )
             .and( HttpMethod.PUT, false )
             .and( HttpMethod.PATCH, false )
                .build();

        return new SimplifiedRouter(
            router,
            methodsThatWillReadBodyBeforeExec
        );
    }

    /**
     * Creates a basic instance of {@link SimplifiedRouter}. Developers that use
     * routers created by this method should configure themselves how to read the
     * request body payload.
     *
     * @param router the router instance that will be wrapped
     * @return An instance of {@link SimplifiedRouter}
     */
    public static SimplifiedRouter wrapWithNoAuthBodyReader( Router router ) {
        return new SimplifiedRouter( router, Collections.emptyMap() );
    }

    @Override
    public void handle(HttpServerRequest event) {
        interceptorHandler.handle(event);
    }

    @RequiredArgsConstructor( staticName = "wrap" )
    private static class SafeRoutingContextHandler implements Handler<RoutingContext> {

        final Handler<RoutingContext> handler;

        @Override
        public void handle(RoutingContext event) {
            try {
                handler.handle(event);
            } catch ( Throwable cause ) {
                Response.sendError( event, cause );
            }
        }
    }
}
