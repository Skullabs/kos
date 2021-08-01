package kos.api;

import io.vertx.core.json.JsonObject;
import lombok.Value;

/**
 * Either consumes from a Source or Manually produces messages which will
 * be sent through the {@link io.vertx.core.eventbus.EventBus}.
 */
public interface EventBusSink {

    /**
     * Attempts to initialise the Sink.
     */
    Result tryInitialise(SubscriptionRequest subscriptionRequest);

    /**
     * Represents a subscription request. Whenever EventBusSinkManager receives
     * a subscription request, it will notify all {@link EventBusSink}. As a result
     * of a successfully handled request, one or more producers will be available in
     * Vert.x's EventBus.
     */
    @Value
    class SubscriptionRequest {
        JsonObject applicationConfig;
        KosContext kosContext;
        String address;
        Class<?> expectedType;
    }

    /**
     * Represents the outcome of an initialisation attempt.
     */
    @Value
    class Result {
        boolean initialised;
        Throwable failure;

        public static final Result
            SUCCEEDED = new Result(true, null),
            NOT_ATTEMPTED = new Result(false, null)
        ;

        public static Result failure(Throwable failure) {
            return new Result(false, failure);
        }
    }
}