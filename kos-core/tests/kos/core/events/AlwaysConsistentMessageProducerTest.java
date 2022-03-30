package kos.core.events;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageProducer;
import kos.core.Lang;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AlwaysConsistentMessageProducerTest {

    static final String LOCAL_ADDRESS = "local::address";

    Vertx vertx;
    EventBus eventBus;
    MessageProducer<String> producer;

    @BeforeEach
    void setupVertx(){
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        producer = new AlwaysConsistentMessageProducer<>(LOCAL_ADDRESS, eventBus);
    }

    @AfterEach
    void shutdownVertx(){
        Lang.waitFor(vertx.close());
    }

    @Test
    void shouldSucceedWhenSubscriberSucceeds(){
        val expectedSuccessMessage = "Succeeded";

        eventBus.consumer(LOCAL_ADDRESS, res -> {
            res.reply(expectedSuccessMessage);
        });

        val future = producer.write("Something");
        Lang.waitFor(future);

        assertTrue(future.succeeded());
    }

    @Test
    void shouldSucceedWhenSubscriberReceivedTheExpectedMessage(){
        val expectedSuccessMessage = "Succeeded";

        eventBus.consumer(LOCAL_ADDRESS, EventHandler.async( msg -> {
            assertEquals(msg.body(), expectedSuccessMessage);
            return Future.succeededFuture();
        }));

        val future = producer.write(expectedSuccessMessage);
        Lang.waitFor(future);
        assertTrue(future.succeeded());
    }

    @Test
    void shouldFailWhenSubscriberFails(){
        val expectedFailureMsg = "It's failed, mate";

        eventBus.consumer(LOCAL_ADDRESS, res -> {
            res.fail(1, expectedFailureMsg);
        });

        val future = producer.write("Something");
        assertThrows(RuntimeException.class, () -> Lang.waitFor(future), expectedFailureMsg);
        assertTrue(future.failed());
    }

    @Test
    void shouldSucceedWhenSubscriberDidNotReceiveTheExpectedMessage(){
        val expectedSuccessMessage = "Succeeded";

        eventBus.consumer(LOCAL_ADDRESS, res -> {
            if (res.body().equals(expectedSuccessMessage))
                res.replyAddress();
            else
                res.fail(1, "Nope");
        });

        val future = producer.write("Something Else");
        assertThrows(RuntimeException.class, () -> Lang.waitFor(future));
        assertTrue(future.failed());
    }
}