package kos.core.events;

import injector.AllOf;
import injector.Singleton;
import io.vertx.core.json.JsonObject;
import kos.api.EventBusSink;
import kos.api.KosContext;
import kos.core.exception.KosException;

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
    public Result tryInitialise(JsonObject applicationConfig, KosContext kosContext, String address) {
        if (syncsAttemptedToBeInitialised.contains(address))
            return Result.NOT_ATTEMPTED;

        return attemptToInitialise(applicationConfig, kosContext, address);
    }

    private Result attemptToInitialise(JsonObject applicationConfig, KosContext kosContext, String address) {
        Result result = Result.NOT_ATTEMPTED;

        for (EventBusSink sink : sinks) {
            result = tryInitialise(sink, applicationConfig, kosContext, address);
            if (Result.SUCCEEDED.equals(result)) break;
            if (result.getFailure() != null) {
                throw new KosException("Failed to initialize Sync for " + address + ".", result.getFailure());
            }
        }

        syncsAttemptedToBeInitialised.add(address);
        return result;
    }

    private Result tryInitialise(
        EventBusSink sync, JsonObject applicationConfig,
        KosContext kosContext, String address
    ) {
        try {
            return sync.tryInitialise(applicationConfig, kosContext, address);
        } catch (Throwable cause) {
            return Result.failure(cause);
        }
    }
}