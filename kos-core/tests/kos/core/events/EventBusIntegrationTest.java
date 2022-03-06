package kos.core.events;

import injector.Injector;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static kos.core.Lang.waitFor;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventBusIntegrationTest {

    @Mock Handler<Message<String>> handler;

    EventBusSinkManager eventBusSinkManager;

    @BeforeEach
    void setup(){
        val injector = Injector.create();
        eventBusSinkManager = injector.instanceOf(EventBusSinkManager.class);
    }

    @Test
    void shouldCreateAChannelAndPerformFireAndForgetCommunication() throws InterruptedException {
        eventBusSinkManager.subscribe("test", String.class, handler);

        val producer = eventBusSinkManager.createProducer("test", String.class);
        waitFor( producer.write("Hello World") );

        Thread.sleep(200L);
        verify(handler).handle(argThat( ctx -> ctx.body().equals("Hello World")));
    }
}
