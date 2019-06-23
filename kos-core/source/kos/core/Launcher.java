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
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
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
        configureApp();

        log.info("Loading configuration...");
        Kos.readConfig( res -> {
            deployVerticlesWithConfig( res );
        });
    }

    /**
     * Configure a Kos application. This method will be called before sensible
     * default configuration has been loaded. It is the ideal place for developers
     * to overwrite {@link Kos} defaults that would be applied widely in the
     * application.
     */
    public void configureApp() {}

    /**
     * Manually deploy the following verticles using the discovered configuration
     * loaded by {@link Kos}.
     *
     * @param verticles verticles to be deployed.
     */
    public void deploy(Verticle...verticles) {
        Collections.addAll(customVerticles, verticles);
    }

    private void deployVerticlesWithConfig(JsonObject config) {
        log.info("Looking for verticles to be deployed...");
        deployVerticlesWithConfig(config, verticles);

        if (!customVerticles.isEmpty())
            deployVerticlesWithConfig(config, customVerticles);

        log.debug("Deployment finished");
    }

    private void deployVerticlesWithConfig(JsonObject config, Iterable<Verticle> verticles) {
        val vertx = Kos.defaultVertx.get();

        for (val verticle : verticles) {
            val options = new DeploymentOptions().setConfig(config);
            log.info("Deploying " + verticle.getClass().getCanonicalName() + "...");
            vertx.deployVerticle(verticle, options);
        }
    }
}
