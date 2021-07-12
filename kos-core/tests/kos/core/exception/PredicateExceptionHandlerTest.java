package kos.core.exception;

import io.vertx.core.http.HttpServerRequest;
import kos.api.ExceptionHandler;
import kos.api.Response;
import kos.core.exception.PredicateExceptionHandler.PredicateAndHandler;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("Default Exception Handler mechanism")
class PredicateExceptionHandlerTest {

    ExceptionHandler matchedExceptionHandler = mock(ExceptionHandler.class);
    HttpServerRequest request = mock(HttpServerRequest.class);
    Throwable cause = new NullPointerException();
    Response expectedHandledResponse = mock(Response.class);

    @BeforeEach
    void setupMocks(){
        doReturn(expectedHandledResponse).when(matchedExceptionHandler).handle(eq(request), eq(cause));
    }

    @DisplayName("when predicate matches the thrown exception")
    @Nested class ShouldHandleExceptions {

        ExceptionHandler defaultExceptionHandler = new PredicateExceptionHandler(List.of(
            PredicateAndHandler.with(cause -> true, matchedExceptionHandler)
        ));

        @DisplayName("should delegate exception handling to handler")
        @Test void handle() {
            defaultExceptionHandler.handle(request, cause);
            verify(matchedExceptionHandler).handle(eq(request), eq(cause));
        }

        @DisplayName("should return the created response based on the caught exception")
        @Test void handle2() {
            val response = defaultExceptionHandler.handle(request, cause);
            assertEquals(expectedHandledResponse, response);
        }
    }

    @DisplayName("when predicate not matches the thrown exception")
    @Nested class ShouldNotHandleExceptions {

        ExceptionHandler defaultExceptionHandler = new PredicateExceptionHandler(List.of(
            PredicateAndHandler.with(cause -> false, matchedExceptionHandler)
        ));

        @DisplayName("should not delegate exception handling to handler")
        @Test void handle() {
            defaultExceptionHandler.handle(request, cause);
            verify(matchedExceptionHandler, times(0)).handle(eq(request), eq(cause));
        }

        @DisplayName("should not return the created response based on the caught exception")
        @Test void handle2() {
            val response = defaultExceptionHandler.handle(request, cause);
            assertNotEquals(expectedHandledResponse, response);
        }

        @DisplayName("should return a generic 500 response")
        @Test void handle3() {
            val response = defaultExceptionHandler.handle(request, cause);
            assertEquals(500, response.statusCode());
        }
    }
}