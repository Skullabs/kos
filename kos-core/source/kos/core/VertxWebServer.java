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

import injector.*;
import io.vertx.core.*;
import io.vertx.core.http.*;
import io.vertx.core.json.*;
import io.vertx.core.logging.*;
import io.vertx.ext.web.*;
import lombok.*;
import lombok.experimental.*;

/**
 * Abstracts the creation of a Vert.x web server, automating a few
 * repetitive tasks, automatically loading routes if available,
 * and avoiding common mistakes made during the application bootstrap.
 * This class was either designed to be compatible with Vert.x Verticle
 * mechanism or to be run as standalone Java application.
 */
@Singleton @ExposedAs(Verticle.class)
@Setter @Getter
@Accessors(fluent = true)
public class VertxWebServer extends AbstractVerticle {

    static private final Logger log = Kos.logger(VertxWebServer.class);

    @NonNull
    private SimplifiedRouter router;

    @Override
    public void start() {
        start(Promise.promise());
    }

    @Override
    public void start(Promise<Void> startFuture) {
        try {
            beforeStart();
            startServer( startFuture );
            startFuture.complete();
        } catch ( Throwable cause ) {
            log.error( "Could not start server", cause );
            startFuture.fail(cause);
        }
    }

    private void startServer( Promise<Void> startFuture ) {
        val httpServerOptions = loadServerOptions();

        notifyWebServerDeploymentListeners(httpServerOptions);

        vertx.createHttpServer( httpServerOptions )
            .requestHandler( router() )
                .listen( as -> {
                    if ( as.failed() )
                        startFuture.fail(as.cause());
                    else {
                        val server = as.result();
                        Runtime.getRuntime().addShutdownHook(new Thread(server::close));
                        afterStart(server);
                    }
                });
    }

    /**
     * Loads the {@link HttpServerOptions} from the Class Path. Developers
     * are encouraged to override this method whenever needed.
     *
     * @return the found or created HttpServerOptions object.
     */
    protected HttpServerOptions loadServerOptions() {
        return Kos.implementationLoader
            .instanceOf( HttpServerOptions.class )
            .orElseGet( this::createServerOptions );
    }

    /**
     * The default server configuration. It will be triggered
     * when the auto-discovery mechanism failed to find one in the classpath.
     */
    private HttpServerOptions createServerOptions(){
        val options = new HttpServerOptions( this.config() );
        log.debug( "Loading default HttpServerOptions: " + Json.encodePrettily(options) );
        return options;
    }

    private void notifyWebServerDeploymentListeners(HttpServerOptions httpServerOptions) {
        val deploymentListenerContext = new WebServerEventListener.BeforeDeployEvent(
            vertx, httpServerOptions, router(), this.config()
        );

        Kos.implementationLoader
            .instancesExposedAs( WebServerEventListener.class )
            .forEach( cnf -> cnf.on(deploymentListenerContext) );
    }

    /**
     * Executes before the server is started.
     */
    protected void beforeStart() {
        Json.mapper.findAndRegisterModules();
        Json.prettyMapper.findAndRegisterModules();
    }

    /**
     * Executes as soon as the server is started.
     *
     * @param server reference to instantiated (and started) server.
     */
    protected void afterStart(HttpServer server) {
        log.info("Server started at " + server.actualPort() + " port");
    }

    /**
     * Retrieves the current configured router. If none is defined, it
     * will lazily instantiate the default {@link SimplifiedRouter} configuration.
     */
    public SimplifiedRouter router(){
        if ( router == null ) {
            val defaultRouter = Router.router(vertx);
            defaultRouter.route().handler(new DefaultContextAttributesMemorizer());
            router = SimplifiedRouter.wrapWithAutoBodyReader(defaultRouter);
        }
        return router;
    }
}
