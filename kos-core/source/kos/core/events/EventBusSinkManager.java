package kos.core.events;

import injector.AllOf;
import injector.Singleton;
import kos.api.EventBusSink;
import kos.core.exception.KosException;
import lombok.val;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class EventBusSinkManager implements EventBusSink {

    private final Iterable<EventBusSink> sinks;
    private final Set<String> syncsAttemptedToBeInitialised = new HashSet<>();

    EventBusSinkManager(
       @AllOf(EventBusSink.class) Iterable<EventBusSink> sinks)
    {
        this.sinks = sinks;
    }

    @Override
    public Result tryInitialise(SubscriptionRequest subscriptionRequest) {
        if (syncsAttemptedToBeInitialised.contains(subscriptionRequest.getAddress()))
            return Result.NOT_ATTEMPTED;

        return performInitialization(subscriptionRequest);
    }

    private Result performInitialization(SubscriptionRequest subscriptionRequest) {
        val address = subscriptionRequest.getAddress();
        Result result = Result.NOT_ATTEMPTED;

        for (val sink : sinks) {
            result = tryInitialiseSink(sink, subscriptionRequest);
            if (Result.SUCCEEDED.equals(result)) break;
            if (result.getFailure() != null) {
                throw new KosException("Failed to initialize Sync for " + address + ".", result.getFailure());
            }
        }

        syncsAttemptedToBeInitialised.add(address);
        return result;
    }

    private Result tryInitialiseSink(
        EventBusSink sync, SubscriptionRequest subscriptionRequest
    ) {
        try {
            return sync.tryInitialise(subscriptionRequest);
        } catch (Throwable cause) {
            return Result.failure(cause);
        }
    }
}