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
import io.vertx.ext.web.*;
import io.vertx.ext.web.handler.*;
import kos.api.KosContext;
import kos.api.RequestHandler;
import kos.api.RequestInterceptor;
import kos.api.Response;
import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static kos.core.Lang.mapOf;

/**
 * Small layer that wraps {@link Router} aiming to simplify a few common
 * aspects regarding route configuration, like intercepting requests
 * and the reading body requests in a POST, PUT and PATCH requests.
 */
@Slf4j
@Accessors(fluent = true)
public class SimplifiedRouter implements Handler<HttpServerRequest> {

    private final Router router;
    private final RequestInterceptorHandler interceptorHandler;
    private final Map<HttpMethod, Boolean> httpMethodsThatMayReadBody;
    private final KosContext kosContext;

    public SimplifiedRouter(KosContext kosContext, Router router, Map<HttpMethod, Boolean> httpMethodsThatMayReadBody) {
        this(kosContext, router, new RequestInterceptorHandler(router), httpMethodsThatMayReadBody);
    }

    private SimplifiedRouter(KosContext kosContext, Router router, RequestInterceptorHandler interceptor, Map<HttpMethod, Boolean> httpMethodsThatMayReadBody) {
        this.interceptorHandler = interceptor;
        this.router = router;
        this.httpMethodsThatMayReadBody = httpMethodsThatMayReadBody;
        this.kosContext = kosContext;
    }

    /**
     * Returns the wrapped Vert.x {@link Router}.
     * @return the wrapped Vert.x {@link Router}
     */
    public Router unwrap(){
        return router;
    }

    /**
     * Registers a {@link RequestInterceptor} to intercept all requests.
     *
     * @param interceptor the interceptor handler.
     */
    public void intercept( RequestInterceptor interceptor ) {
        intercept(interceptor, true);
    }

    /**
     * Registers a {@link RequestInterceptor} to intercept all requests.
     *
     * @param tryHandleExceptions if set true, it will wrap the interceptor and try to handle possible
     *                            exceptions that might be thrown. It won't be capable to handle
     *                            those that might happen on a different thread though.
     * @param interceptor the interceptor handler.
     */
    public void intercept( RequestInterceptor interceptor, boolean tryHandleExceptions ) {
        log.info("Registering router interceptor " + interceptor.getClass().getCanonicalName() );

        if (tryHandleExceptions)
            interceptor = new SafeRequestInterceptor(interceptor, kosContext);

        interceptorHandler.register( interceptor );
    }

    /**
     * Registers a {@link Handler} to intercept all received requests.
     *
     * @param interceptor The interceptor handler
     */
    public void intercept( Handler<RoutingContext> interceptor ) {
        log.info("Registering router interceptor "+ interceptor.getClass().getCanonicalName() );
        val safe = new SafeRoutingContextHandler(interceptor, kosContext);
        router.route().handler(safe);
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
            .handler( new SafeRoutingContextHandler(handler, kosContext) );

        log.debug( "Registered " + handler.getClass() );
    }

    /**
     * Creates an instance of {@link SimplifiedRouter} that will automatically read the
     * request body payload for PATCH, POST and PUT.
     *
     * @param router the router instance that will be wrapped
     * @return An instance of {@link SimplifiedRouter}
     */
    public static SimplifiedRouter wrapWithAutoBodyReader(KosContext kosContext, Router router ){
        val methodsThatWillReadBodyBeforeExec =
            mapOf( HttpMethod.POST, false )
             .and( HttpMethod.PUT, false )
             .and( HttpMethod.PATCH, false )
                .build();

        return new SimplifiedRouter(
            kosContext, router,
            methodsThatWillReadBodyBeforeExec
        );
    }

    /**
     * Creates a basic instance of {@link SimplifiedRouter}. Developers that use
     * instances created by this method should configure themselves how to read the
     * request body payload.
     *
     * @param router the router instance that will be wrapped
     * @return An instance of {@link SimplifiedRouter}
     */
    public static SimplifiedRouter wrapWithNoAutoBodyReader(KosContext kosContext, Router router ) {
        return new SimplifiedRouter(kosContext, router, Collections.emptyMap() );
    }

    @Override
    public void handle(HttpServerRequest event) {
        interceptorHandler.handle(event);
    }

    @RequiredArgsConstructor
    private class SafeRoutingContextHandler implements Handler<RoutingContext> {

        final Handler<RoutingContext> handler;
        final KosContext kosContext;

        @Override
        public void handle(RoutingContext event) {
            try {
                handler.handle(event);
            } catch ( Throwable cause ) {
                log.error("Could not execute handler", cause);
                Response.sendError(kosContext, event, cause);
            }
        }
    }

    @RequiredArgsConstructor
    private class SafeRequestInterceptor implements RequestInterceptor {

        final RequestInterceptor interceptor;
        final KosContext kosContext;

        @Override
        public void handle(HttpServerRequest request, Handler<HttpServerRequest> next) {
            try {
                interceptor.handle(request, next);
            } catch (Throwable cause) {
                log.error("Could not execute interceptor " + interceptor.getClass().getCanonicalName(), cause);
                Response.sendError(kosContext, request, cause);
            }
        }
    }
}
