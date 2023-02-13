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
    private static class ResponseTypeHandlerFor$PatchUser$a44fb2910d498802191f53879ab6ffd166eaf5e8a28a8fb6149bb171237f53c3 implements Handler<AsyncResult<kos.api.Response>> {

        private final KosContext kosContext;
        private final RoutingContext routingContext;

        ResponseTypeHandlerFor$PatchUser$a44fb2910d498802191f53879ab6ffd166eaf5e8a28a8fb6149bb171237f53c3(KosContext kosContext, RoutingContext routingContext) {
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
    private static class ResponseTypeHandlerFor$RetrieveUser$503ad392d03d2f606bae554cab497033540ab30b069b679547715448c2c2ba2d implements Handler<AsyncResult<kos.sample.rest.api.User>> {

        private final KosContext kosContext;
        private final RoutingContext routingContext;

        ResponseTypeHandlerFor$RetrieveUser$503ad392d03d2f606bae554cab497033540ab30b069b679547715448c2c2ba2d(KosContext kosContext, RoutingContext routingContext) {
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
        Handler<RoutingContext> handlerForDeleteUser$2cd2bbd6306e060993e03665fc2a2a97f6b081cf725906313d80cb90ce5aaeb4 = new Handler<RoutingContext>() {

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
        event.getRouter().route( HttpMethod.DELETE, "/api/simple/:user_id", handlerForDeleteUser$2cd2bbd6306e060993e03665fc2a2a97f6b081cf725906313d80cb90ce5aaeb4);

        /**
         * Handle incoming requests mapped for {@link kos.sample.rest.api.SimpleApi#patchUser}.
         */
        Handler<RoutingContext> handlerForPatchUser$a44fb2910d498802191f53879ab6ffd166eaf5e8a28a8fb6149bb171237f53c3 = new Handler<RoutingContext>() {

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
                    futures.asFuture(response).onComplete(new ResponseTypeHandlerFor$PatchUser$a44fb2910d498802191f53879ab6ffd166eaf5e8a28a8fb6149bb171237f53c3(kosContext, routingContext));
                } catch (Throwable cause){
                    Response.sendError(kosContext, routingContext, cause);
                }
            }
        };
        // Maps the method handler to an HTTP endpoint
        event.getRouter().route( HttpMethod.PATCH, "/api/simple/:user_id", handlerForPatchUser$a44fb2910d498802191f53879ab6ffd166eaf5e8a28a8fb6149bb171237f53c3);

        /**
         * Handle incoming requests mapped for {@link kos.sample.rest.api.SimpleApi#retrieveUser}.
         */
        Handler<RoutingContext> handlerForRetrieveUser$503ad392d03d2f606bae554cab497033540ab30b069b679547715448c2c2ba2d = new Handler<RoutingContext>() {

            public void handle(final RoutingContext routingContext) {
                try {
                    /* Call original handler */
                    final io.vertx.core.Future<kos.sample.rest.api.User> response =
                        handler.retrieveUser(
                        );
                    /* Wraps response as Future */
                    futures.asFuture(response).onComplete(new ResponseTypeHandlerFor$RetrieveUser$503ad392d03d2f606bae554cab497033540ab30b069b679547715448c2c2ba2d(kosContext, routingContext));
                } catch (Throwable cause){
                    Response.sendError(kosContext, routingContext, cause);
                }
            }
        };
        // Maps the method handler to an HTTP endpoint
        event.getRouter().route( HttpMethod.GET, "/api/simple", handlerForRetrieveUser$503ad392d03d2f606bae554cab497033540ab30b069b679547715448c2c2ba2d);

        /**
         * Handle incoming requests mapped for {@link kos.sample.rest.api.SimpleApi#retrieveUser}.
         */
        Handler<RoutingContext> handlerForRetrieveUser$503ad392d03d2f606bae554cab497033540ab30b069b679547715448c2c2ba2d = new Handler<RoutingContext>() {

            public void handle(final RoutingContext routingContext) {
                try {
                    /* Call original handler */
                    final io.vertx.core.Future<kos.sample.rest.api.User> response =
                        handler.retrieveUser(
                        );
                    /* Wraps response as Future */
                    futures.asFuture(response).onComplete(new ResponseTypeHandlerFor$RetrieveUser$503ad392d03d2f606bae554cab497033540ab30b069b679547715448c2c2ba2d(kosContext, routingContext));
                } catch (Throwable cause){
                    Response.sendError(kosContext, routingContext, cause);
                }
            }
        };
        // Maps the method handler to an HTTP endpoint
        event.getRouter().route( HttpMethod.GET, "/api/simple/all", handlerForRetrieveUser$503ad392d03d2f606bae554cab497033540ab30b069b679547715448c2c2ba2d);

        /**
         * Handle incoming requests mapped for {@link kos.sample.rest.api.SimpleApi#saveUser}.
         */
        Handler<RoutingContext> handlerForSaveUser$b62e5df7194d0669c603c07a70e7b13755eb3cece61c742d558db28704d86523 = new Handler<RoutingContext>() {

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
        event.getRouter().route( HttpMethod.POST, "/api/simple", handlerForSaveUser$b62e5df7194d0669c603c07a70e7b13755eb3cece61c742d558db28704d86523);

        /**
         * Handle incoming requests mapped for {@link kos.sample.rest.api.SimpleApi#updateUser}.
         */
        Handler<RoutingContext> handlerForUpdateUser$752e7933bc45972696bf537d05b533987c3bfc153424ea502a5974e2ca4e6641 = new Handler<RoutingContext>() {

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
        event.getRouter().route( HttpMethod.PUT, "/api/simple/:id", handlerForUpdateUser$752e7933bc45972696bf537d05b533987c3bfc153424ea502a5974e2ca4e6641);

    }

}
