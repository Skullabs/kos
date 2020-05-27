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
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import kos.api.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Simplified launcher wrapping Vert.x deployment of {@link Verticle}s.
 *
 * @see ImplementationLoader Default implementation loader.
 */
@RequiredArgsConstructor
public class Launcher {

    private final MutableKosConfiguration conf;
    private Logger log;

    public Launcher(){
        this(new MutableKosConfiguration());
    }

    public static void main(String[] args) {
        new Launcher().run();
    }

    public void run(){
        configureKos();
        loadLogger();
        readDeploymentConfig( deploymentConf -> {
            deployCustomApplication(deploymentConf);
            deployWebServer( deploymentConf );
            deployVerticles( deploymentConf );
        });
    }

    private void configureKos() {
        val plugins = conf.getSpi().instancesExposedAs(ConfigurationPlugin.class);

        for (val plugin : plugins)
            plugin.configure(conf);

        conf.getImplementationLoader().register(KosConfiguration.class, conf);
    }

    void loadLogger() {
        log = conf.createLoggerFor(getClass());
    }

    void readDeploymentConfig(Handler<DeploymentContext> handler) {
        log.info("Reading deployment configuration...");
        conf.getConfigRetriever().getConfig( res -> {
            if (res.succeeded()) {
                val deploymentConf = new DeploymentContext(conf, res.result());
                handler.handle(deploymentConf);
            } else
                throw new KosException("Failed to read configuration", res.cause());
        });
    }

    void deployCustomApplication(DeploymentContext deploymentContextConf) {
        val application = deploymentContextConf.instanceOf(Application.class);
        if (application.isPresent()) {
            val customApp = application.get();
            log.info("Configuring custom application " + customApp.getClass().getCanonicalName() + "...");
            customApp.configure(deploymentContextConf);
        }
    }

    void deployWebServer(DeploymentContext deploymentContext) {
        if (deploymentContext.getApplicationConfig().getBoolean( "auto-config", true )) {
            log.info("Deploying Vert.x WebServer...");
            val server = new VertxWebServer(deploymentContext.kosConfiguration);
            deploymentContext.deploy(server);
        }
    }

    void deployVerticles(DeploymentContext deploymentContext) {
        log.info("Looking for verticles...");

        val verticles = deploymentContext.instancesExposedAs(Verticle.class);
        deploymentContext.deploy(verticles);
    }

    @Getter
    static class DeploymentContext implements kos.api.DeploymentContext {

        final KosConfiguration kosConfiguration;
        final JsonObject applicationConfig;
        final Logger log;

        DeploymentContext(KosConfiguration kosConfiguration, JsonObject applicationConfig) {
            this.kosConfiguration = kosConfiguration;
            this.applicationConfig = applicationConfig;
            this.log = kosConfiguration.createLoggerFor(getClass());
        }

        <T> Iterable<T> instancesExposedAs(Class<T> targetClass) {
            return kosConfiguration.getImplementationLoader().instancesExposedAs(targetClass);
        }

        <T> ImplementationLoader.Result<T> instanceOf(Class<T> targetClass) {
            return kosConfiguration.getImplementationLoader().instanceOf(targetClass);
        }

        public void deploy(Iterable<Verticle> verticles) {
            for (val verticle : verticles)
                deploy(verticle);
        }

        public void deploy(Verticle verticle) {
            val options = new DeploymentOptions().setConfig(applicationConfig);
            log.info("Deploying " + verticle.getClass().getCanonicalName() + "...");
            kosConfiguration.getDefaultVertx().deployVerticle(verticle, options);
        }
    }
}
