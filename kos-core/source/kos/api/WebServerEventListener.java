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

package kos.api;

import io.vertx.core.*;
import io.vertx.core.json.*;
import kos.core.SimplifiedRouter;
import kos.core.VertxWebServer;
import lombok.*;
import lombok.experimental.*;

/**
 * Called by {@link VertxWebServer}, implementations of
 * this interface will have the opportunity to enhance or change
 * the web server configuration before it is started.
 */
public interface WebServerEventListener {

    /**
     * Called before deploy {@link VertxWebServer} verticle.
     * @param event
     */
    void on( BeforeDeployWebServerEvent event );

    /**
     * Data available before deploy the web server.
     */
    @Accessors(chain = true)
    @Value class BeforeDeployWebServerEvent {
        Vertx vertx;
        SimplifiedRouter router;
        JsonObject applicationConfig;
        KosConfiguration kosConfiguration;
    }
}
