package kos.core.events;

import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("ThrowableNotThrown")
class DefaultAsyncEventReplierTest {

    @Mock Message<Object> message;
    @Mock AsyncResult<Void> result;
    DefaultAsyncEventReplier<Object> replier;

    @BeforeEach
    void setup(){
        replier = new DefaultAsyncEventReplier<>(message);
    }

    @Nested class WhenResultHoldsFailure {

        Throwable FAILURE = new RuntimeException("Failure");

        @BeforeEach
        void assume() {
            doReturn(false).when(result).succeeded();
            doReturn(FAILURE).when(result).cause();
        }

        @DisplayName("Should send a failure reply to sender")
        @Test void handle()
        {
            replier.handle(result);
            verify(message).fail(eq(1), eq("Failure"));
        }
    }

    @Nested class WhenResultHoldsSuccess {

        @BeforeEach
        void assume() {
            doReturn(true).when(result).succeeded();
        }

        @DisplayName("Should send a failure reply to sender")
        @Test void handle()
        {
            replier.handle(result);
            verify(message).replyAddress();
        }
    }
}