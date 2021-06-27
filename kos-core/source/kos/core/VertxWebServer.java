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
import io.vertx.core.http.*;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.*;
import kos.api.KosContext;
import kos.api.RequestInterceptor;
import kos.api.WebServerEventListener;
import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;

/**
 * Abstracts the creation of a Vert.x web server, automating a few
 * repetitive tasks, automatically loading routes if available,
 * and avoiding common mistakes made during the application bootstrap.
 * This class was either designed to be compatible with Vert.x Verticle
 * mechanism or to be run as standalone Java application.
 */
@Slf4j
@Setter @Getter
@Accessors(fluent = true)
public class VertxWebServer extends AbstractVerticle {

    private final KosContext kosContext;

    @NonNull
    private SimplifiedRouter router;

    /**
     * Constructs a VertxWebServer. This constructor will automatically
     * run and configure all option flags.
     *
     * @param kosContext Kos Configuration
     */
    public VertxWebServer(KosContext kosContext){
        this(kosContext, true);
    }

    /**
     * Constructs VertxWebServer.
     * 
     * @param kosContext - Kos Configuration
     * @param autoConfigOptionals - true if should automatically config all optionals
     */
    public VertxWebServer(KosContext kosContext, boolean autoConfigOptionals) {
        this(kosContext, loadDefaultRouter(kosContext, autoConfigOptionals));
    }

    private static SimplifiedRouter loadDefaultRouter(
        KosContext kosContext, boolean autoConfigOptionals)
    {
        val defaultRouter = Router.router(kosContext.getDefaultVertx());
        val simplified = SimplifiedRouter.wrapWithAutoBodyReader(kosContext, defaultRouter);

        if (autoConfigOptionals) {
            defaultRouter.route().handler(new DefaultContextAttributesMemorizer());

            kosContext.getImplementationLoader()
                .instancesExposedAs(RequestInterceptor.class)
                .forEach(simplified::intercept);
        }

        return simplified;
    }

    /**
     * Constructs a VertxWebServer.
     *
     * @param kosContext a valid configuration
     * @param router a pre-configured Simplified Router.
     */
    public VertxWebServer(KosContext kosContext, SimplifiedRouter router) {
        this.kosContext = kosContext;
        this.router = router;
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void start(Promise<Void> startFuture) {
        try {
            beforeStart();
            tryToStartServer( startFuture );
        } catch ( Throwable cause ) {
            log.error( "Could not start server", cause );
            startFuture.fail(cause);
        }
    }

    private void tryToStartServer(Promise<Void> startFuture ) {
        vertx.createHttpServer( kosContext.getHttpServerOptions() )
            .requestHandler( router() )
                .listen( as -> {
                    if ( as.failed() )
                        startFuture.fail(as.cause());
                    else {
                        val server = as.result();
                        Runtime.getRuntime().addShutdownHook(new Thread(server::close));
                        afterStart(server);
                        startFuture.complete();
                    }
                });
    }

    private void notifyWebServerDeploymentListeners() {
        val deploymentListenerContext = new WebServerEventListener.BeforeDeployWebServerEvent(
            vertx, router(), this.config(), kosContext
        );

        val listeners = kosContext.getImplementationLoader()
            .instancesExposedAs( WebServerEventListener.class );

        Lang.sorted(listeners, Comparator.comparingInt(WebServerEventListener::priority).reversed())
            .forEach( cnf -> cnf.on(deploymentListenerContext) );
    }

    /**
     * Executes before the server is started.
     */
    protected void beforeStart() {
        notifyWebServerDeploymentListeners();
        log.debug("Initializing Web Service verticle with the following configurations: " + kosContext);

        DatabindCodec.mapper().findAndRegisterModules();
        DatabindCodec.prettyMapper().findAndRegisterModules();
    }

    /**
     * Executes as soon as the server is started.
     *
     * @param server reference to instantiated (and started) server.
     */
    protected void afterStart(HttpServer server) {
        log.info("Server started at " + server.actualPort() + " port");
    }
}
