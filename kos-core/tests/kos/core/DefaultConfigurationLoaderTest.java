package kos.core;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import kos.api.KosContext;
import kos.api.MutableKosContext;
import kos.api.WebServerEventListener.BeforeDeployWebServerEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class DefaultConfigurationLoaderTest {

    KosContext context = new MutableKosContext();

    @Mock Vertx vertx;
    @Mock SimplifiedRouter simplifiedRouter;
    @Mock JsonObject configObject;
    BeforeDeployWebServerEvent event;

    DefaultConfigurationLoader confLoader = new DefaultConfigurationLoader();

    @BeforeEach
    void createDefaultEvent() {
        event = new BeforeDeployWebServerEvent(
            vertx, simplifiedRouter, configObject, context
        );
    }

    @Test void shouldLoadWebServerPortFromConfiguration() {
        doReturn(8080).when(configObject).getInteger(eq("web.server.port"), eq(9000));

        confLoader.on(event);

        assertEquals(8080, context.getHttpServerOptions().getPort());
    }
}