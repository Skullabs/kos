package kos.core.events;

import injector.AllOf;
import injector.Singleton;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import kos.api.EventBusSink;
import kos.api.EventPublisherSink;
import kos.api.EventSubscriptionSink;
import kos.api.KosContext;
import kos.core.exception.KosException;
import lombok.NonNull;
import lombok.val;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import static kos.core.Lang.sorted;

@Singleton
@SuppressWarnings("all")
public class EventBusSinkManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Set<Class> classesWhichCodedHaveBeenRegistered = new HashSet<>();

    protected final KosContext kosContext;
    private final Iterable<EventSubscriptionSink> eventSubscriberSinks;
    private final Iterable<EventPublisherSink> eventPublisherSinks;

    public EventBusSinkManager(
        KosContext kosContext,
        @AllOf(EventSubscriptionSink.class) Iterable<EventSubscriptionSink> eventSubscriberSinks,
        @AllOf(EventPublisherSink.class) Iterable<EventPublisherSink> eventPublisherSinks) {
        this.kosContext = kosContext;
        this.eventSubscriberSinks = sorted(eventSubscriberSinks, Comparator.comparing(EventBusSink::getPriority));
        this.eventPublisherSinks = sorted(eventPublisherSinks, Comparator.comparing(EventBusSink::getPriority));
    }

    /**
     * Registers a message consumer {@code messageHandler} that will receive messages of
     * the type {@link T} published into the given {@code address}.
     *
     * @param address a free-format String. {@link EventPublisherSink} implementations might have to
     *                parse this address to communicate with external services.
     * @param expectedType the expected contract used when serialising messages.
     * @throws KosException whenever it reaches a terminal, but unrecoverable state.
     */
    public <T> void subscribe(String address, Class<T> expectedType, Handler<Message<T>> messageHandler) {
        val rewrittenAddress = tryInitializeSink(address, expectedType, eventSubscriberSinks);
        kosContext.getDefaultVertx().eventBus().consumer(rewrittenAddress, messageHandler);
    }

    /**
     * Creates a {@link MessageProducer} that expects messages of type {@link T}.
     *
     * @param address a free-format String. {@link EventPublisherSink} implementations might have to
     *                parse this address to communicate with external services.
     * @param expectedType the expected contract used when serialising messages.
     * @throws KosException whenever it reaches a terminal, but unrecoverable state.
     */
    public <T> MessageProducer<T> createProducer(String address, Class<T> expectedType) {
        val rewrittenAddress = tryInitializeSink(address, expectedType, eventPublisherSinks);
        return kosContext.getDefaultVertx().eventBus().publisher(rewrittenAddress);
    }

    /**
     * Initializes the Sink responsible for bridging the communication
     * between the local {@link io.vertx.core.eventbus.EventBus} with a remote broker, if any.
     * Sinks must be able to successfully finish its initialization. Any Exception thrown during
     * this process will be considered a terminal, non-recoverable state - leading to an abrupt
     * interruption of the whole application.
     */
    final <T> String tryInitializeSink(@NonNull String address, Class<T> expectedType, Iterable<? extends EventBusSink> eventBusSinks) {
        try {
            ensureEventBusCanSerializeType(expectedType);
            return performInitialization(address, expectedType, eventBusSinks);
        } catch (KosException cause){
            throw cause;
        } catch (Throwable cause) {
            throw new KosException(cause, "Failed to initialize Sync for " + address + ".");
        }
    }

    private <T> String performInitialization(@NonNull String address, Class<T> expectedType, Iterable<? extends EventBusSink> eventBusSinks) {
        val request = constructPublishingRequestFor(address, expectedType);
        EventBusSink.Result result = EventBusSink.Result.NOT_ATTEMPTED;

        for (val sink : eventBusSinks) {
            result = sink.initialize(request);
            if (result.isInitialised()) break;
            else if (result.getFailureCause() != null)
                throw new KosException("Failed to initialize Sync for " + address + ".", result.getFailureCause());
        }

        if (!result.isInitialised()) {
            logger.debug("No custom EventBusSink was initialized for address '" + address + ".");
        }

        return result.getRewrittenAddressOr(address);
    }

    private <T> EventBusSink.EventBusSyncInitializationRequest<T> constructPublishingRequestFor(String address, Class<T> expectedType) {
        return new EventBusSink.EventBusSyncInitializationRequest<T>(
            kosContext.getApplicationConfig(),
            kosContext,
            address,
            expectedType
        );
    }

    public <T> void ensureEventBusCanSerializeType(Class<T> targetType) {
        if (!classesWhichCodedHaveBeenRegistered.contains(targetType)) {
            EventBus eventBus = kosContext.getDefaultVertx().eventBus();
            MessageCodec<T, T> codec = kosContext.getDefaultEventBusCodecFactory().constructCodecFor(targetType);
            eventBus.registerDefaultCodec(targetType, codec);
            classesWhichCodedHaveBeenRegistered.add(targetType);
        }
    }
}
