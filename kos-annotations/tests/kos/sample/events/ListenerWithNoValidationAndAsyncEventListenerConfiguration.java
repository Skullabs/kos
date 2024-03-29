package kos.sample.events;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import kos.api.*;
import kos.core.events.*;

/**
 * Auto generated event bus listener configuration for {@link ListenerWithNoValidationAndAsync }.
 */
@SuppressWarnings("all")
@injector.Singleton
@injector.ExposedAs(ConfigurationLoadedEventListener.class)
@javax.annotation.processing.Generated("kos.apt.EventListenerKosProcessor")
public class ListenerWithNoValidationAndAsyncEventListenerConfiguration implements ConfigurationLoadedEventListener {

    @Override
    public void on(ConfigurationLoadedEvent event) {
        final ImplementationLoader implementationLoader = event.getKosContext().getImplementationLoader();

        // Auto-configure a message producer, if found in the classpath
        final EventBusSinkManager subscriptionManager = implementationLoader.instanceOfOrFail(EventBusSinkManager.class);

        final Validation validation = event.getKosContext().getDefaultValidation();
        final Vertx vertx = event.getKosContext().getDefaultVertx();
        final ListenerWithNoValidationAndAsync listener = implementationLoader.instanceOfOrFail(ListenerWithNoValidationAndAsync.class);

        /*
         * Configuring listener for
         *  - eventBus address: "gcp::pubsub::users::deleted"
         *  - handled by: ListenerWithNoValidationAndAsync#on
         *  - handler is async: true
         *  - requires validation: false
         */
        subscriptionManager.subscribe("gcp::pubsub::users::deleted", java.lang.String.class, EventHandler.async((Message<java.lang.String> message) -> {
            java.lang.String body = message.body();
            return listener.on(body);
        }));
    }
}