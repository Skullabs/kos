package kos.core.validation;

import io.vertx.core.http.HttpServerRequest;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kos.core.validation.FailureType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ValidationExceptionHandlerTest {

    ValidationExceptionHandler handler = new ValidationExceptionHandler();
    HttpServerRequest request = mock(HttpServerRequest.class);

    @DisplayName("Should return a valid Response when handling ValidationException")
    @Nested class ValidResponse {

        @DisplayName("when MALFORMED_ENTITY")
        @Nested class MalformedEntity {

            ValidationException cause = new ValidationException(MALFORMED_ENTITY, "missing field 'name'");

            @DisplayName("should return 400")
            @Test void handle() {
                val response = handler.handle(request, cause);
                assertEquals(400, response.statusCode());
            }
        }

        @DisplayName("when UNPROCESSABLE_ENTITY")
        @Nested class UnprocessableEntity {

            ValidationException cause = new ValidationException(UNPROCESSABLE_ENTITY, "invalid 'name' attribute");

            @DisplayName("should return 422")
            @Test void handle() {
                val response = handler.handle(request, cause);
                assertEquals(422, response.statusCode());
            }
        }

        @DisplayName("when OTHER")
        @Nested class Other {

            ValidationException cause = new ValidationException(OTHER, "failed to fetch extra data");

            @DisplayName("should return 500")
            @Test void handle() {
                val response = handler.handle(request, cause);
                assertEquals(500, response.statusCode());
            }
        }
    }
}