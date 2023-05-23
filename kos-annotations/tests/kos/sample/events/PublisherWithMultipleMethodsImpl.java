package kos.sample.events;

import injector.*;
import kos.api.*;
import kos.core.events.*;
import io.vertx.core.*;
import io.vertx.core.eventbus.*;

/**
 * Auto generated event bus publisher for {@link kos.sample.events.PublisherWithMultipleMethods}.
 */
@Singleton
@ExposedAs(ConfigurationLoadedEventListener.class)
@javax.annotation.processing.Generated("kos.apt.EventPublisherKosProcessor")
public class PublisherWithMultipleMethodsImpl implements ConfigurationLoadedEventListener, PublisherWithMultipleMethods {

    /**
     * Message producer for (java.util.UUID).
     */
    MessageProducer<java.util.UUID> userDeletedProducer0;

    @Override
    public io.vertx.core.Future<java.lang.Void> userDeleted(java.util.UUID value) {
        return userDeletedProducer0.write(value);
    }

    @Override
    public void on(ConfigurationLoadedEvent configurationLoadedEvent) {
        final ImplementationLoader implementationLoader = configurationLoadedEvent.getKosContext().getImplementationLoader();
        final EventBusSinkManager eventPublisherManager = implementationLoader.instanceOfOrFail(EventBusSinkManager.class);

        userDeletedProducer0 = eventPublisherManager.createProducer("gcp::pubsub::users::deleted", java.util.UUID.class);
    }

    @Producer
    public PublisherWithMultipleMethods producePublisherWithMultipleMethods() {
        return this;
    }
}