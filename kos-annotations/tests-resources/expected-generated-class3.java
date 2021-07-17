package kos.rest.sample;

import kos.api.*;
import kos.core.*;
import kos.core.validation.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.RoutingContext;

/**
 * Auto generated server configuration for {@link kos.rest.sample.ApiWithValidation}.
 */
@SuppressWarnings("all")
@injector.Singleton
@javax.annotation.processing.Generated("kos.apt.RestApiProcessor")
public class ApiWithValidationRoutingContextHandler implements WebServerEventListener {

    public int priority() {
        return Integer.MIN_VALUE;
    }


    public void on(WebServerEventListener.BeforeDeployWebServerEvent event) {
        final KosContext kosContext = event.getKosContext();
        final ImplementationLoader implementationLoader = kosContext.getImplementationLoader();
        final VertxFutures futures = implementationLoader.instanceOfOrFail(VertxFutures.class);
        final WebPointcutValidation webPointcutValidation = implementationLoader.instanceOfOrFail(WebPointcutValidation.class);

        // Fetches an instance of the web handler class
        final ApiWithValidation handler = implementationLoader.instanceOfOrFail(ApiWithValidation.class);

        /**
         * Handle incoming requests mapped for {@link kos.rest.sample.ApiWithValidation#updateEvent}.
         */
        Handler<RoutingContext> handlerForUpdateEvent$PUT1812602333 = new Handler<RoutingContext>() {

            public void handle(final RoutingContext routingContext) {
                try {
                    /* Call original handler */
                        handler.updateEvent(
                            /* Read Param parameter identified by "id" */
                            Request.readParam( kosContext, routingContext, "id", java.lang.String.class ),
                            /* Read attached (and pre-validated) Body parameter identified by "receivedEvent" */
                            webPointcutValidation.unwrapForBody(routingContext, "receivedEvent")
                        );
                    /* Sends the default response for "no content". */
                    Response.sendDefaultNoContent( kosContext, routingContext );
                } catch (Throwable cause){
                    Response.sendError(kosContext, routingContext, cause);
                }
            }
        };

        // Validates the parameter identified as receivedEvent
        handlerForUpdateEvent$PUT1812602333 = webPointcutValidation.wrapForBody( kos.rest.sample.Event.class, "receivedEvent", handlerForUpdateEvent$PUT1812602333 );
        // Maps the method handler to an HTTP endpoint
        event.getRouter().route( HttpMethod.PUT, "/events/:id", handlerForUpdateEvent$PUT1812602333);

    }

}
