package kos.sample.rest.api;

import kos.api.*;
import kos.core.*;
import kos.core.validation.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.RoutingContext;

/**
 * Auto generated server configuration for {@link kos.sample.rest.api.SimpleApi}.
 */
@SuppressWarnings("all")
@injector.Singleton
@javax.annotation.processing.Generated("kos.apt.rest.RestApiProcessor")
public class SimpleApiRoutingContextHandler implements WebServerEventListener {

    public int priority() {
        return Integer.MIN_VALUE;
    }

    /**
     * Response Handler for PATCH requests received by {@link SimpleApi.patchUser}.
     */
    private static class ResponseTypeHandlerFor$PatchUser$PATCH453745825 implements Handler<AsyncResult<kos.api.Response>> {

        private final KosContext kosContext;
        private final RoutingContext routingContext;

        ResponseTypeHandlerFor$PatchUser$PATCH453745825(KosContext kosContext, RoutingContext routingContext) {
            this.routingContext = routingContext;
            this.kosContext = kosContext;
        }

        public void handle( AsyncResult<kos.api.Response> as ) {
            if (as.succeeded())
                Response.send(kosContext, routingContext, as.result());
            else
                Response.sendError(kosContext, routingContext, as.cause());
        }
    }

    /**
     * Response Handler for GET requests received by {@link SimpleApi.retrieveUser}.
     */
    private static class ResponseTypeHandlerFor$RetrieveUser$GET860966350 implements Handler<AsyncResult<kos.sample.rest.api.User>> {

        private final KosContext kosContext;
        private final RoutingContext routingContext;

        ResponseTypeHandlerFor$RetrieveUser$GET860966350(KosContext kosContext, RoutingContext routingContext) {
            this.routingContext = routingContext;
            this.kosContext = kosContext;
        }

        public void handle( AsyncResult<kos.sample.rest.api.User> as ) {
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
        final SimpleApi handler = implementationLoader.instanceOfOrFail(SimpleApi.class);

        /**
         * Handle incoming requests mapped for {@link kos.sample.rest.api.SimpleApi#deleteUser}.
         */
        Handler<RoutingContext> handlerForDeleteUser$DELETE1849506963 = new Handler<RoutingContext>() {

            public void handle(final RoutingContext routingContext) {
                try {
                    /* Call original handler */
                        handler.deleteUser(
                            /* Read Param parameter identified by "user_id" */
                            Request.readParam( kosContext, routingContext, "user_id", java.lang.String.class ),
                            /* Read Context parameter identified by "httpServerRequest" */
                            Request.readContext( kosContext, routingContext, "httpServerRequest", io.vertx.core.http.HttpServerRequest.class )
                        );
                    /* Sends the default response for "no content". */
                    Response.sendDefaultNoContent( kosContext, routingContext );
                } catch (Throwable cause){
                    Response.sendError(kosContext, routingContext, cause);
                }
            }
        };
        // Maps the method handler to an HTTP endpoint
        event.getRouter().route( HttpMethod.DELETE, "/api/simple/:user_id", handlerForDeleteUser$DELETE1849506963);

        /**
         * Handle incoming requests mapped for {@link kos.sample.rest.api.SimpleApi#patchUser}.
         */
        Handler<RoutingContext> handlerForPatchUser$PATCH453745825 = new Handler<RoutingContext>() {

            public void handle(final RoutingContext routingContext) {
                try {
                    /* Call original handler */
                    final kos.api.Response response =
                        handler.patchUser(
                            /* Read Param parameter identified by "user_id" */
                            Request.readParam( kosContext, routingContext, "user_id", java.lang.String.class ),
                            /* Read Header parameter identified by "Authorization" */
                            Request.readHeader( kosContext, routingContext, "Authorization", java.lang.String.class ),
                            /* Read Body parameter identified by "user" */
                            Request.readBody( kosContext, routingContext, "user", kos.sample.rest.api.User.class )
                        );
                    /* Wraps response as Future */
                    futures.asFuture(response).onComplete(new ResponseTypeHandlerFor$PatchUser$PATCH453745825(kosContext, routingContext));
                } catch (Throwable cause){
                    Response.sendError(kosContext, routingContext, cause);
                }
            }
        };
        // Maps the method handler to an HTTP endpoint
        event.getRouter().route( HttpMethod.PATCH, "/api/simple/:user_id", handlerForPatchUser$PATCH453745825);

        /**
         * Handle incoming requests mapped for {@link kos.sample.rest.api.SimpleApi#retrieveUser}.
         */
        Handler<RoutingContext> handlerForRetrieveUser$GET860966350 = new Handler<RoutingContext>() {

            public void handle(final RoutingContext routingContext) {
                try {
                    /* Call original handler */
                    final io.vertx.core.Future<kos.sample.rest.api.User> response =
                        handler.retrieveUser(
                        );
                    /* Wraps response as Future */
                    futures.asFuture(response).onComplete(new ResponseTypeHandlerFor$RetrieveUser$GET860966350(kosContext, routingContext));
                } catch (Throwable cause){
                    Response.sendError(kosContext, routingContext, cause);
                }
            }
        };
        // Maps the method handler to an HTTP endpoint
        event.getRouter().route( HttpMethod.GET, "/api/simple", handlerForRetrieveUser$GET860966350);

        /**
         * Handle incoming requests mapped for {@link kos.sample.rest.api.SimpleApi#retrieveUser}.
         */
        Handler<RoutingContext> handlerForRetrieveUser$GET860966350 = new Handler<RoutingContext>() {

            public void handle(final RoutingContext routingContext) {
                try {
                    /* Call original handler */
                    final io.vertx.core.Future<kos.sample.rest.api.User> response =
                        handler.retrieveUser(
                        );
                    /* Wraps response as Future */
                    futures.asFuture(response).onComplete(new ResponseTypeHandlerFor$RetrieveUser$GET860966350(kosContext, routingContext));
                } catch (Throwable cause){
                    Response.sendError(kosContext, routingContext, cause);
                }
            }
        };
        // Maps the method handler to an HTTP endpoint
        event.getRouter().route( HttpMethod.GET, "/api/simple/all", handlerForRetrieveUser$GET860966350);

        /**
         * Handle incoming requests mapped for {@link kos.sample.rest.api.SimpleApi#saveUser}.
         */
        Handler<RoutingContext> handlerForSaveUser$POST2072131618 = new Handler<RoutingContext>() {

            public void handle(final RoutingContext routingContext) {
                try {
                    /* Call original handler */
                        handler.saveUser(
                            /* Read Body parameter identified by "user" */
                            Request.readBody( kosContext, routingContext, "user", kos.sample.rest.api.User.class )
                        );
                    /* Sends the default response for "no content". */
                    Response.sendDefaultNoContent( kosContext, routingContext );
                } catch (Throwable cause){
                    Response.sendError(kosContext, routingContext, cause);
                }
            }
        };
        // Maps the method handler to an HTTP endpoint
        event.getRouter().route( HttpMethod.POST, "/api/simple", handlerForSaveUser$POST2072131618);

        /**
         * Handle incoming requests mapped for {@link kos.sample.rest.api.SimpleApi#updateUser}.
         */
        Handler<RoutingContext> handlerForUpdateUser$PUT1562071373 = new Handler<RoutingContext>() {

            public void handle(final RoutingContext routingContext) {
                try {
                    /* Call original handler */
                        handler.updateUser(
                            /* Read Param parameter identified by "id" */
                            Request.readParam( kosContext, routingContext, "id", java.lang.String.class )
                        );
                    /* Sends the default response for "no content". */
                    Response.sendDefaultNoContent( kosContext, routingContext );
                } catch (Throwable cause){
                    Response.sendError(kosContext, routingContext, cause);
                }
            }
        };
        // Maps the method handler to an HTTP endpoint
        event.getRouter().route( HttpMethod.PUT, "/api/simple/:id", handlerForUpdateUser$PUT1562071373);

    }

}
