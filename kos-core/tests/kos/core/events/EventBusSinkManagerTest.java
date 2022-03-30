package kos.core.events;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.json.JsonObject;
import kos.api.EventBusSink;
import kos.api.EventSubscriptionSink;
import kos.api.KosContext;
import kos.api.MutableKosContext;
import kos.core.exception.KosException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static kos.api.EventBusSink.Result.NOT_ATTEMPTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventBusSinkManagerTest {

    @Mock Vertx vertx;
    @Mock EventBus eventBus;
    @Mock MessageConsumer messageConsumer;
    @Mock MessageProducer messageProducer;

    @Mock EventSubscriptionSink eventSubscriptionSink;
    @Mock EventSubscriptionSink eventSubscriptionSink2;
    @Mock EventSubscriptionSink eventSubscriptionSink3;

    KosContext kosContext;
    EventBusSinkManager manager;
    List<EventSubscriptionSink> allSinks;

    @BeforeEach
    void setupMock()
    {
        kosContext = new MutableKosContext()
                .setDefaultVertx(vertx)
                .setApplicationConfig(new JsonObject());
        manager = new EventBusSinkManager(kosContext, Collections.emptyList(), Collections.emptyList());
        allSinks = asList(
            eventSubscriptionSink, eventSubscriptionSink2, eventSubscriptionSink3
        );
    }

    @BeforeEach
    void setupVertx(){
        doReturn(eventBus).when(vertx).eventBus();
        doReturn(messageConsumer).when(eventBus).consumer(any(), any());
        doReturn(messageProducer).when(eventBus).publisher(any());
    }

    @Nested class WhenNoSinkAttemptedToInitialize {

        @BeforeEach
        void ensureMocksNoAttemptToInitialize() {
            doReturn(NOT_ATTEMPTED).when(eventSubscriptionSink).initialize(any());
            doReturn(NOT_ATTEMPTED).when(eventSubscriptionSink2).initialize(any());
            doReturn(NOT_ATTEMPTED).when(eventSubscriptionSink3).initialize(any());
        }

        @DisplayName("Should do nothing")
        @Test void tryInitializeSink()
        {
            manager.tryInitializeSink("address", Object.class, allSinks);
        }
    }

    @Nested class WhenOneSinkIsSucceeded {

        @BeforeEach
        void ensureMocksNoAttemptToInitialize() {
            doReturn(NOT_ATTEMPTED).when(eventSubscriptionSink).initialize(any());
            doReturn(EventBusSink.Result.succeededAtAddress("rewritten")).when(eventSubscriptionSink2).initialize(any());
        }

        @DisplayName("Should return the rewritten address")
        @Test void tryInitializeSink()
        {
            val result = manager.tryInitializeSink("address", Object.class, allSinks);
            assertEquals("rewritten", result.getRewrittenAddress());
        }

        @DisplayName("Should not invoke subsequent sinks after the successful ones")
        @Test void tryInitializeSink2()
        {
            manager.tryInitializeSink("address", Object.class, allSinks);

            verify(eventSubscriptionSink3, never()).initialize(any());
        }

        @DisplayName("Should invoke the ones before the successful attempt")
        @Test void tryInitializeSink3()
        {
            manager.tryInitializeSink("address", Object.class, allSinks);

            verify(eventSubscriptionSink).initialize(any());
        }

        @DisplayName("Should invoke the successful attempt")
        @Test void tryInitializeSink4()
        {
            manager.tryInitializeSink("address", Object.class, allSinks);

            verify(eventSubscriptionSink2).initialize(any());
        }
    }

    @Nested class WhenOneSinkFails {

        final Throwable FAILURE = new RuntimeException();

        @BeforeEach
        void ensureMocksNoAttemptToInitialize() {
            doReturn(NOT_ATTEMPTED).when(eventSubscriptionSink).initialize(any());
            doThrow(FAILURE).when(eventSubscriptionSink2).initialize(any());
        }

        @DisplayName("Should return Failure")
        @Test void tryInitializeSink()
        {
            assertThrows(KosException.class, () -> manager.tryInitializeSink("address", Object.class, allSinks));
        }

        @DisplayName("Should not invoke subsequent sinks after the successful ones")
        @Test void tryInitializeSink2()
        {
            assertThrows(KosException.class, () -> manager.tryInitializeSink("address", Object.class, allSinks));

            verify(eventSubscriptionSink3, never()).initialize(any());
        }

        @DisplayName("Should invoke the ones before the successful attempt")
        @Test void tryInitializeSink3()
        {
            assertThrows(KosException.class, () -> manager.tryInitializeSink("address", Object.class, allSinks));

            verify(eventSubscriptionSink).initialize(any());
        }

        @DisplayName("Should invoke the successful attempt")
        @Test void tryInitializeSink4()
        {
            assertThrows(KosException.class, () -> manager.tryInitializeSink("address", Object.class, allSinks));

            verify(eventSubscriptionSink2).initialize(any());
        }
    }
}