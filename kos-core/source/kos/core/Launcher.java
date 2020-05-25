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

import io.vertx.core.*;
import io.vertx.core.json.*;
import io.vertx.core.logging.*;
import kos.api.Deployment;
import kos.api.ImplementationLoader;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Simplified launcher wrapping Vert.x deployment of {@link Verticle}s.
 *
 * @see ImplementationLoader Default implementation loader.
 */
public class Launcher {

    private final Logger log = Kos.logger(Launcher.class);
    private final Iterable<Verticle> verticles = Kos.implementationLoader.instancesExposedAs(Verticle.class);
    private final List<Verticle> customVerticles = new ArrayList<>();

    public static void main(String[] args) {
        new Launcher().run();
    }

    public void run(){
        log.debug("Launcher is initializing...");
        log.info("Loading configuration...");
        Kos.readConfig( conf -> {
            val deployment = new LauncherDeployment(conf);
            deployWebServer( deployment );
            deployVerticlesWithConfig( deployment );
            log.debug("Launcher has finished!");
        });
    }

    /**
     * Configure a custom Kos application. 
     */
    public void configureApp() {}

    private void deployWebServer(LauncherDeployment deployment) {
        if (deployment.config.getBoolean( "kos.auto", true )) {
            val server = Kos.implementationLoader.instanceOfOrFail(VertxWebServer.class);
            deployment.deploy(server);
        }
    }

    private void deployVerticlesWithConfig(LauncherDeployment deployment) {
        log.info("Looking for verticles...");
        deployment.deploy(verticles);

        if (!customVerticles.isEmpty())
            deployment.deploy(customVerticles);
    }

    @RequiredArgsConstructor
    private class LauncherDeployment implements Deployment {

        final Vertx vertx = Kos.defaultVertx.get();
        final JsonObject config;

        @Override
        public void deploy(Iterable<Verticle> verticles) {
            deploy(verticles, config);
        }

        @Override
        public void deploy(Verticle verticle) {
            deploy(verticle, config);
        }

        @Override
        public void deploy(Iterable<Verticle> verticles, JsonObject config) {
            for (val verticle : verticles)
                deploy(verticle, config);
        }

        @Override
        public void deploy(Verticle verticle, JsonObject config) {
            val options = new DeploymentOptions().setConfig(config);
            log.info("Deploying " + verticle.getClass().getCanonicalName() + "...");
            vertx.deployVerticle(verticle, options);
        }
    }
}
