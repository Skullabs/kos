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

        /**
         * Tells whether the Sink was initialised or not.
         */
        boolean initialised;

        /**
         * Holds the cause of a possible failure. It is null in case the
         * initialisation succeeds.
         */
        Throwable failureCause;

        /**
         * The local address which Kos will use to bridge the communication
         * between Producers and Consumers.
         */
        @Getter(PRIVATE)
        String rewrittenAddress;

        /**
         *
         */
        boolean eventuallyConsistent;

        public String getRewrittenAddressOr(String defaultValue) {
            if (rewrittenAddress != null)
                return rewrittenAddress;
            return defaultValue;
        }

        /**
         * Identifies that a given sink haven't tried to be initialised.
         * Sink developers are encouraged to return {@code Result.NOT_ATTEMPTED}
         * whenever the Sink cannot handle the informed {@code address}.
         */
        public static final Result NOT_ATTEMPTED = new Result(false, null, null, true);

        /**
         * Generates a result indicating a failure. Failure results are terminal
         * and might immediately stop the application.
         */
        public static Result failure(Throwable failure) {
            return new Result(false, failure, null, false);
        }

        /**
         * Returns a Result object indicating that the Sink managed to
         * start the communication with the remote broker. As a result,
         * all communication made through EventBus on the informed
         * {@code rewrittenAddress} will be bridged by the remote broker.
         *
         * The communication semantics will always be consistent, yielding
         * a <i>failure Future</i> object whenever the communication with
         * the remote broker fails - succeeding otherwise. This model does
         * not allow one channel to have more than one consumer. However,
         * given that this method was designed to bridge the communication with
         * a remote broker, rather than providing broadcast semantics locally,
         * this implications might actually be the desirable behaviour.
         */
        public static Result succeededAtAddress(String rewrittenAddress) {
            return new Result(true, null, rewrittenAddress, false);
        }

        /**
         * Returns a Result object indicating that the Sink managed to
         * start the communication with the remote broker. As a result,
         * all communication made through EventBus on the informed
         * {@code rewrittenAddress} will be bridged by the remote broker.
         *
         * @param eventuallyConsistent if set to true, it will implement a fire-and-forget semantic,
         *    succeeding the operation whenever the message reaches the EventBus. If set to false it
         *    will behave exactly like {@link Result#succeededAtAddress(String, Boolean)}.
         */
        public static Result succeededAtAddress(String rewrittenAddress, Boolean eventuallyConsistent) {
            return new Result(true, null, rewrittenAddress, eventuallyConsistent);
        }
    }
}
