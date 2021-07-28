package kos.core.events;

import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("ALL")
@ExtendWith(MockitoExtension.class)
class EventHandlerTest {

    @Mock Message<Object> message;

    @Nested class TryingToHandleEventsSynchronously {

        @Mock Consumer<Message<Object>> messageHandler;

        @InjectMocks
        @Spy SyncEventHandler<Object> syncEventHandler;

        @DisplayName("Should invoke message handler")
        @Test void handle()
        {
            syncEventHandler.tryHandle(message);
            verify(messageHandler).accept(eq(message));
        }

        @Nested class WhenHandlerSucceeds {

            @BeforeEach
            void assume(){
                doNothing().when(messageHandler).accept(eq(message));
            }

            @DisplayName("Should return a successful future")
            @Test void tryHandle()
            {
                val result = syncEventHandler.tryHandle(message);
                assertTrue(result.succeeded());
            }
        }

        @Nested class WhenHandlerFails {

            Throwable cause = new RuntimeException();

            @BeforeEach
            void assume(){
                doThrow(cause).when(messageHandler).accept(eq(message));
            }

            @DisplayName("Should return a failure future")
            @Test void tryHandle()
            {
                val result = syncEventHandler.tryHandle(message);
                assertTrue(result.failed());
            }

            @DisplayName("Should wrap the failure cause in the result")
            @Test void tryHandle2()
            {
                val result = syncEventHandler.tryHandle(message);
                assertSame(cause, result.cause());
            }
        }


    }

    @Nested class TryingToHandleEventsAsynchronously {

        @Mock Function<Message<Object>, Future<Void>> messageHandler;

        @InjectMocks
        @Spy AsyncEventHandler<Object> asyncEventHandler;

        @DisplayName("Should invoke message handler")
        @Test void handle()
        {
            asyncEventHandler.tryHandle(message);
            verify(messageHandler).apply(eq(message));
        }

        @Nested class WhenHandlerReturnsASuccessfulResult {

            Future<Void> futureResponse = Future.succeededFuture();

            @BeforeEach
            void assume(){
                doReturn(futureResponse).when(messageHandler).apply(eq(message));
            }

            @DisplayName("Should return the successful promise")
            @Test void handle2()
            {
                val result = asyncEventHandler.tryHandle(message);
                assertSame(futureResponse, result);
            }
        }

        @Nested class WhenHandlerReturnsAFailureResult {

            Future<Void> futureResponse = Future.failedFuture("Failed");

            @BeforeEach
            void assume(){
                doReturn(futureResponse).when(messageHandler).apply(eq(message));
            }

            @DisplayName("Should return the successful promise")
            @Test void handle2()
            {
                val result = asyncEventHandler.tryHandle(message);
                assertSame(futureResponse, result);
            }
        }

        @Nested class WhenHandlerThrowsException {

            Throwable cause = new RuntimeException();

            @BeforeEach
            void assume(){
                doThrow(cause).when(messageHandler).apply(eq(message));
            }

            @DisplayName("Should return a failure future")
            @Test void handle()
            {
                val result = asyncEventHandler.tryHandle(message);
                assertTrue(result.failed());
            }

            @DisplayName("Returned future should wrap the failure cause")
            @Test void handle2()
            {
                val result = asyncEventHandler.tryHandle(message);
                assertSame(cause, result.cause());
            }
        }
    }

    @Nested class HandlingEvents {

        @Mock Future<Void> futureResponse;
        @Mock EventHandler<Object> asyncEventHandler;

        @BeforeEach
        void assume(){
            doReturn(futureResponse).when(asyncEventHandler).tryHandle(eq(message));
            doCallRealMethod().when(asyncEventHandler).handle(eq(message));
        }

        @DisplayName("Should try to handle message")
        @Test void handle()
        {
            asyncEventHandler.handle(message);
            verify(asyncEventHandler).tryHandle(eq(message));
        }

        @DisplayName("Should attach completition handler into the future response")
        @Test void handle2()
        {
            asyncEventHandler.handle(message);
            verify(futureResponse).onComplete(any(DefaultAsyncEventReplier.class));
        }
    }
}