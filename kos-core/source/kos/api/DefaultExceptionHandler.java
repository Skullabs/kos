package kos.api;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

@Slf4j
class DefaultExceptionHandler implements ExceptionHandler
{
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
        log.error(msg, cause);

        val stackTrace = new StringWriter();
        cause.printStackTrace(new PrintWriter(stackTrace));

        val serialized = Buffer.buffer(stackTrace.toString());
        val headers = Collections.singletonMap(CONTENT_TYPE, "text/plain");
        return Response.wrap(serialized).statusCode(500).headers(headers);
    }
}