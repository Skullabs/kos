package kos.api;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import kos.core.Lang;
import lombok.val;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

class DefaultExceptionHandler implements ExceptionHandler
{
    private final Lang.Lazy<Logger> log;

    public DefaultExceptionHandler(KosConfiguration kosConfiguration) {
        this.log = Lang.Lazy.by(() -> kosConfiguration.createLoggerFor(DefaultExceptionHandler.class));
    }

    @Override
    public Response handle(HttpServerRequest request, HttpServerResponse response, Throwable cause)
    {
        if (cause instanceof HandledResponseException) {
            return ((HandledResponseException) cause).response;
        } else {
            return handleUnknownError(request, cause);
        }
    }

    private Response handleUnknownError(HttpServerRequest request, Throwable cause)
    {
        val msg = format("Failed to handle request: %s - %s", request.method(), request.uri());
        log.get().error(msg, cause);

        val stackTrace = new StringWriter();
        cause.printStackTrace(new PrintWriter(stackTrace));

        val serialized = Buffer.buffer(stackTrace.toString());
        val headers = Collections.singletonMap(CONTENT_TYPE, "text/plain");
        return Response.wrap(serialized).statusCode(500).headers(headers);
    }
}