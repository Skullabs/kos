/*
 * Copyright 2019 Skullabs Contributors (https://github.com/skullabs)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kos.core;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import kos.api.*;
import kos.api.ConfigurationLoadedEventListener.ConfigurationLoadedEvent;
import kos.core.exception.KosException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Simplified launcher wrapping Vert.x deployment of {@link Verticle}s.
 *
 * @see ImplementationLoader Default implementation loader.
 */
@Slf4j
@RequiredArgsConstructor
public class Launcher {

    private final MutableKosContext conf;

    public Launcher(){
        this(new MutableKosContext());
    }

    public static void main(String[] args) {
        new Launcher().run();
    }

    public void run(){
        runPluginsAndConfigureKos();
        readDeploymentConfig( loadedConfiguration -> {
            trigger( loadedConfiguration );
            deployWebServer( loadedConfiguration );
            deployVerticles( loadedConfiguration );
        });
    }

    private void runPluginsAndConfigureKos() {
        log.info("Initializing plugins...");
        val plugins = Lang.sorted(
            conf.getSpi().instancesExposedAs(Plugin.class),
            (p1, p2) -> Integer.compare(p2.priority(), p1.priority())
        );

        for (val plugin : plugins) {
            log.debug(" -> " + plugin.getClass().getCanonicalName());
            plugin.configure(conf);
        }

        conf.getImplementationLoader().register(KosContext.class, conf);
    }

    void readDeploymentConfig(Handler<ConfigurationLoadedEvent> handler) {
        log.info("Reading deployment configuration...");
        conf.getConfigRetriever().getConfig( res -> {
            if (res.succeeded()) {
                conf.setApplicationConfig(res.result());
                val deploymentConf = new ConfigurationLoadedEvent(conf, res.result());
                handler.handle(deploymentConf);
            } else
                throw new KosException("Failed to read configuration", res.cause());
        });
    }

    void trigger(ConfigurationLoadedEvent event) {
        val listeners = event.getKosContext().getImplementationLoader().instancesExposedAs(ConfigurationLoadedEventListener.class);

        log.info("Configuration loaded.");
        for (ConfigurationLoadedEventListener listener : listeners) {
            log.debug("Notifying " + listener.getClass().getCanonicalName() + "...");
            listener.on(event);
        }
    }

    void deployWebServer(ConfigurationLoadedEvent event) {
        if (event.getApplicationConfig().getBoolean( "auto-config", true )) {
            log.info("Deploying Vert.x WebServer...");
            val server = new VertxWebServer(event.getKosContext());
            deploy(event, server);
        }
    }

    void deployVerticles(ConfigurationLoadedEvent event) {
        log.info("Looking for verticles...");

        val verticles = event.getKosContext().getImplementationLoader().instancesExposedAs(Verticle.class);
        for (val verticle : verticles)
            deploy(event, verticle);
    }

    public void deploy(ConfigurationLoadedEvent event, Verticle verticle) {
        val options = new DeploymentOptions().setConfig(event.getApplicationConfig());
        log.debug("Deploying " + verticle.getClass().getCanonicalName() + "...");
        event.getKosContext().getDefaultVertx().deployVerticle(verticle, options);
    }
}
