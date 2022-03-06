package kos.api;

import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

/**
 * Bridges the communication between the local EventBus with a
 * remote broker (e.g. ApacheMQ, RabbitMQ, Apache Kafka, AWS SQS,
 * GCP PubSub, etc).
 *
 * Notice: do not implement this interface directly. Instead, it
 * is recommended to implement {@link EventPublisherSink} and {@link EventSubscriptionSink}.
 */
public interface EventBusSink {

    /**
     * Defines the resolution priority, where the bigger the returned
     * value higher the priority. Defaults to 0.
     */
    default int getPriority() {
        return 0;
    }

    /**
     * Initialises the Sink. In practical terms, this method is expected to
     * leverage all resources required to publish or receive messages from
     * the remote broker.
     */
    <T> Result initialize(EventBusSyncInitializationRequest<T> request);

    @Value
    class EventBusSyncInitializationRequest<T> {
        JsonObject applicationConfig;
        KosContext kosContext;
        String address;
        Class<T> expectedType;
    }

    /**
     * Represents the outcome of an attempt to initialise either {@link EventSubscriptionSink}
     * or {@link EventPublisherSink}.
     *
     * Developers are encouraged to not throw exceptions when initialising
     * Sinks. As a last-resort, one might store exceptions in {@code failureCause}.
     * If the Sink is not entitled to initialise a given {@code address}, one
     * can just return {@link Result#NOT_ATTEMPTED}.
     */
    @Value
    @RequiredArgsConstructor(access = PRIVATE)
    class Result {

        boolean initialised;
        Throwable failureCause;
        private String rewrittenAddress;

        public String getRewrittenAddressOr(String defaultValue) {
            if (rewrittenAddress != null)
                return rewrittenAddress;
            return defaultValue;
        }

        public static final Result NOT_ATTEMPTED = new Result(false, null, null);

        public static Result failure(Throwable failure) {
            return new Result(false, failure, null);
        }

        public static Result succeededAtAddress(String rewrittenAddress) {
            return new Result(true, null, rewrittenAddress);
        }
    }
}
