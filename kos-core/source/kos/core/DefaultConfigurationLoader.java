package kos.core;

import injector.Exposed;
import kos.api.WebServerEventListener;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Automatically configures Vert.x components managed by Kos.
 */
@Exposed
@Slf4j
public class DefaultConfigurationLoader
 implements WebServerEventListener {

    /**
     * Ensures the configuration will be applied prior to the user defined ones.
     */
    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void on(BeforeDeployWebServerEvent event) {
        val config = event.getApplicationConfig();
        val httpServerOptions = event.getKosContext().getHttpServerOptions();
        httpServerOptions.setPort( config.getInteger("web.server.port", 9000) );
    }
}
