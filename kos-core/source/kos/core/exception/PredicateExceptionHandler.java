package kos.core.exception;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import kos.api.ExceptionHandler;
import kos.api.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

/**
 * The default Exception Handler mechanism. It provides predicate-based
 * semantics for Exception Handler matching.
 */
@Slf4j
@RequiredArgsConstructor
public class PredicateExceptionHandler implements ExceptionHandler
{
    /**
     * This is a naive implementation, which the performance degrades
     * linearly as the number of predicates grows. It might not be a problem
     * as exceptions shouldn't be the preferred request path, thus
     * not impacting the user experience that much.
     */
    @NonNull
    final List<PredicateAndHandler> predicatesAndHandlers;

    /**
     * Constructs this class with an empty, but mutable, list of predicates.
     */
    public PredicateExceptionHandler(){
        this(new ArrayList<>());
    }

    public void add(Predicate<Throwable> predicate, ExceptionHandler handler) {
        this.predicatesAndHandlers.add(
            PredicateAndHandler.with(predicate, handler)
        );
    }

    /**
     * @see ExceptionHandler#handle(HttpServerRequest, Throwable)
     */
    @Override
    public Response handle(HttpServerRequest request, Throwable cause)
    {
        for (PredicateAndHandler predicateAndHandler : predicatesAndHandlers) {
            if (predicateAndHandler.predicate.test(cause))
                return predicateAndHandler.handler.handle(request, cause);
        }

        return handleUnknownError(request, cause);
    }

    private Response handleUnknownError(HttpServerRequest request, Throwable cause)
    {
        val msg = format("Failed to handle request: %s - %s", request.method(), request.uri());
        log.error(msg, cause);

        val stackTrace = new StringWriter();
        cause.printStackTrace(new PrintWriter(stackTrace));

        val serialized = Buffer.buffer(stackTrace.toString());
        val headers = Collections.singletonMap(CONTENT_TYPE, "text/plain");
        return Response.wrap(serialized).statusCode(500).headers(headers);
    }

    @RequiredArgsConstructor(staticName = "with")
    static class PredicateAndHandler {
        final Predicate<Throwable> predicate;
        final ExceptionHandler handler;
    }
}