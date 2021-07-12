package kos.core.validation;

import io.vertx.core.http.HttpServerRequest;
import kos.api.ExceptionHandler;
import kos.api.Response;
import lombok.val;

public class ValidationExceptionHandler implements ExceptionHandler {

    @Override
    public Response handle(HttpServerRequest request, Throwable cause) {
        ValidationException validationException = (ValidationException) cause;
        val response = Response.of(validationException.getMessage());

        switch (validationException.getFailureType()) {
            case UNPROCESSABLE_ENTITY: return response.statusCode(422);
            case MALFORMED_ENTITY: return response.statusCode(400);
            default: return response.statusCode(500);
        }
    }
}
