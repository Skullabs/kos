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
import kos.api.*;
import kos.core.exception.KosException;
import lombok.Getter;
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
        configureKos();
        readDeploymentConfig( deploymentConf -> {
            deployCustomApplication(deploymentConf);
            deployWebServer( deploymentConf );
            deployVerticles( deploymentConf );
        });
    }

    private void configureKos() {
        val plugins = Lang.sorted(
            conf.getSpi().instancesExposedAs(ConfigurationPlugin.class),
            (p1, p2) -> Integer.compare(p2.priority(), p1.priority())
        );

        for (val plugin : plugins)
            plugin.configure(conf);

        conf.getImplementationLoader().register(KosContext.class, conf);
    }

    void readDeploymentConfig(Handler<DeploymentContext> handler) {
        log.info("Reading deployment configuration...");
        conf.getConfigRetriever().getConfig( res -> {
            if (res.succeeded()) {
                conf.setApplicationConfig(res.result());
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
            val server = new VertxWebServer(deploymentContext.kosContext);
            deploymentContext.deploy(server);
        }
    }

    void deployVerticles(DeploymentContext deploymentContext) {
        log.info("Looking for verticles...");

        val verticles = deploymentContext.instancesExposedAs(Verticle.class);
        deploymentContext.deploy(verticles);
    }

    @Slf4j
    @Getter
    static class DeploymentContext implements kos.api.DeploymentContext {

        final KosContext kosContext;
        final JsonObject applicationConfig;

        DeploymentContext(KosContext kosContext, JsonObject applicationConfig) {
            this.kosContext = kosContext;
            this.applicationConfig = applicationConfig;
        }

        <T> Iterable<T> instancesExposedAs(Class<T> targetClass) {
            return kosContext.getImplementationLoader().instancesExposedAs(targetClass);
        }

        <T> ImplementationLoader.Result<T> instanceOf(Class<T> targetClass) {
            return kosContext.getImplementationLoader().instanceOf(targetClass);
        }

        public void deploy(Iterable<Verticle> verticles) {
            for (val verticle : verticles)
                deploy(verticle);
        }

        public void deploy(Verticle verticle) {
            val options = new DeploymentOptions().setConfig(applicationConfig);
            log.debug("Deploying " + verticle.getClass().getCanonicalName() + "...");
            kosContext.getDefaultVertx().deployVerticle(verticle, options);
        }
    }
}
