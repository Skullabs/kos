package kos.rest.sample;

import kos.api.*;
import kos.core.*;
import kos.core.validation.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import kos.sample.rest.api.ApiWithNoPath;

/**
 * Auto generated server configuration for {@link ApiWithNoPath}.
 */
@SuppressWarnings("all")
@injector.Singleton
@javax.annotation.processing.Generated("kos.apt.rest.RestApiProcessor")
public class ApiWithNoPathRoutingContextHandler implements WebServerEventListener {

    public int priority() {
        return Integer.MIN_VALUE;
    }

    /**
     * Response Handler for GET requests received by {@link ApiWithNoPath.retrieveLocation}.
     */
    private static class ResponseTypeHandlerFor$RetrieveLocation$GET1518549639 implements Handler<AsyncResult<java.lang.String>> {

        private final KosContext kosContext;
        private final RoutingContext routingContext;

        ResponseTypeHandlerFor$RetrieveLocation$GET1518549639(KosContext kosContext, RoutingContext routingContext) {
            this.routingContext = routingContext;
            this.kosContext = kosContext;
        }

        public void handle( AsyncResult<java.lang.String> as ) {
            if (as.succeeded())
                Response.send(kosContext, routingContext, as.result());
            else
                Response.sendError(kosContext, routingContext, as.cause());
        }
    }


    public void on(WebServerEventListener.BeforeDeployWebServerEvent event) {
        final KosContext kosContext = event.getKosContext();
        final ImplementationLoader implementationLoader = kosContext.getImplementationLoader();
        final VertxFutures futures = implementationLoader.instanceOfOrFail(VertxFutures.class);
        final WebPointcutValidation webPointcutValidation = implementationLoader.instanceOfOrFail(WebPointcutValidation.class);

        // Fetches an instance of the web handler class
        final ApiWithNoPath handler = implementationLoader.instanceOfOrFail(ApiWithNoPath.class);

        /**
         * Handle incoming requests mapped for {@link ApiWithNoPath#retrieveLocation}.
         */
        Handler<RoutingContext> handlerForRetrieveLocation$GET1518549639 = new Handler<RoutingContext>() {

            public void handle(final RoutingContext routingContext) {
                try {
                    /* Call original handler */
                    final java.lang.String response =
                        handler.retrieveLocation(
                        );
                    /* Wraps response as Future */
                    futures.asFuture(response).onComplete(new ResponseTypeHandlerFor$RetrieveLocation$GET1518549639(kosContext, routingContext));
                } catch (Throwable cause){
                    Response.sendError(kosContext, routingContext, cause);
                }
            }
        };
        // Maps the method handler to an HTTP endpoint
        event.getRouter().route( HttpMethod.GET, "/location", handlerForRetrieveLocation$GET1518549639);

    }

}
