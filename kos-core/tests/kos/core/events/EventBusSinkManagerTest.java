package kos.core.events;

import kos.api.EventBusSink;
import kos.core.exception.KosException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Arrays.asList;
import static kos.api.EventBusSink.Result.NOT_ATTEMPTED;
import static kos.api.EventBusSink.Result.SUCCEEDED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventBusSinkManagerTest {

    EventBusSink.SubscriptionRequest subscriptionRequest = new EventBusSink.SubscriptionRequest(
        null, null, "address", Object.class
    );

    @Mock EventBusSink eventBusSink;
    @Mock EventBusSink eventBusSink2;
    @Mock EventBusSink eventBusSink3;

    EventBusSinkManager manager;

    @BeforeEach
    void setupMock()
    {
        manager = new EventBusSinkManager(asList(
            eventBusSink, eventBusSink2, eventBusSink3
        ));
    }

    @Nested class WhenNoSinkAttemptedToInitialize {

        @BeforeEach
        void ensureMocksNoAttemptToInitialize() {
            doReturn(NOT_ATTEMPTED).when(eventBusSink).tryInitialise(any());
            doReturn(NOT_ATTEMPTED).when(eventBusSink2).tryInitialise(any());
            doReturn(NOT_ATTEMPTED).when(eventBusSink3).tryInitialise(any());
        }

        @DisplayName("Should return NOT_ATTEMPTED")
        @Test void tryInitialise()
        {
            val result = manager.tryInitialise(subscriptionRequest);
            assertEquals(NOT_ATTEMPTED, result);
        }
    }

    @Nested class WhenOneSinkIsSucceeded {

        @BeforeEach
        void ensureMocksNoAttemptToInitialize() {
            doReturn(NOT_ATTEMPTED).when(eventBusSink).tryInitialise(any());
            doReturn(SUCCEEDED).when(eventBusSink2).tryInitialise(any());
        }

        @DisplayName("Should return SUCCEEDED")
        @Test void tryInitialise()
        {
            val result = manager.tryInitialise(subscriptionRequest);
            assertEquals(SUCCEEDED, result);
        }

        @DisplayName("Should not invoke subsequent sinks after the successful ones")
        @Test void tryInitialise2()
        {
            manager.tryInitialise(subscriptionRequest);

            verify(eventBusSink3, never()).tryInitialise(any());
        }

        @DisplayName("Should invoke the ones before the successful attempt")
        @Test void tryInitialise3()
        {
            manager.tryInitialise(subscriptionRequest);

            verify(eventBusSink).tryInitialise(eq(subscriptionRequest));
        }

        @DisplayName("Should invoke the successful attempt")
        @Test void tryInitialise4()
        {
            manager.tryInitialise(subscriptionRequest);

            verify(eventBusSink2).tryInitialise(eq(subscriptionRequest));
        }
    }

    @Nested class WhenOneSinkFails {

        final Throwable FAILURE = new RuntimeException();

        @BeforeEach
        void ensureMocksNoAttemptToInitialize() {
            doReturn(NOT_ATTEMPTED).when(eventBusSink).tryInitialise(any());
            doThrow(FAILURE).when(eventBusSink2).tryInitialise(any());
        }

        @DisplayName("Should return Failure")
        @Test void tryInitialise()
        {
            assertThrows(KosException.class, () -> manager.tryInitialise(subscriptionRequest));
        }

        @DisplayName("Should not invoke subsequent sinks after the successful ones")
        @Test void tryInitialise2()
        {
            assertThrows(KosException.class, () -> manager.tryInitialise(subscriptionRequest));

            verify(eventBusSink3, never()).tryInitialise(any());
        }

        @DisplayName("Should invoke the ones before the successful attempt")
        @Test void tryInitialise3()
        {
            assertThrows(KosException.class, () -> manager.tryInitialise(subscriptionRequest));

            verify(eventBusSink).tryInitialise(eq(subscriptionRequest));
        }

        @DisplayName("Should invoke the successful attempt")
        @Test void tryInitialise4()
        {
            assertThrows(KosException.class, () -> manager.tryInitialise(subscriptionRequest));

            verify(eventBusSink2).tryInitialise(eq(subscriptionRequest));
        }
    }
}