package kos.sample.events;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import kos.api.*;
import kos.core.events.*;

/**
 * Auto generated event bus listener configuration for {@link ListenerWithNoValidationAndSync }.
 */
@SuppressWarnings("all")
@injector.Singleton
@javax.annotation.processing.Generated("kos.apt.EventListenerKosProcessor")
public class ListenerWithNoValidationAndSyncEventListenerConfiguration implements ConfigurationLoadedEventListener {

    @Override
    public void on(ConfigurationLoadedEvent event) {
        final ImplementationLoader implementationLoader = event.getKosContext().getImplementationLoader();

        // Auto-configure a message producer, if found in the classpath
        final EventBusSinkManager messageProducerSyncManager = implementationLoader.instanceOfOrFail(EventBusSinkManager.class);

        final Validation validation = event.getKosContext().getDefaultValidation();
        final Vertx vertx = event.getKosContext().getDefaultVertx();
        final ListenerWithNoValidationAndSync listener = implementationLoader.instanceOfOrFail(ListenerWithNoValidationAndSync.class);

        /*
         * Configuring listener for
         *  - eventBus address: "gcp::pubsub::users::deleted"
         *  - handled by: ListenerWithNoValidationAndSync#on
         *  - handler is async: false
         *  - requires validation: false
         */
        EventBusSink.SubscriptionRequest subscriptionRequest1 = new EventBusSink.SubscriptionRequest(
            event.getApplicationConfig(), event.getKosContext(), "gcp::pubsub::users::deleted", java.lang.String.class
        );
        messageProducerSyncManager.tryInitialise(subscriptionRequest1);
        vertx.eventBus().consumer("gcp::pubsub::users::deleted", EventHandler.async((Message<java.lang.String> message) -> {
            java.lang.String body = message.body();
            listener.on(body);
            return Future.succeededFuture();
        }));
    }
}