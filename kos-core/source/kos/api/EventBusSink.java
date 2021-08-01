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
    Result tryInitialise(JsonObject applicationConfig, KosContext kosContext, String address);

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