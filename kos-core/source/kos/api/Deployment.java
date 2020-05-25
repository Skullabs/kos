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

import io.vertx.core.Verticle;
import io.vertx.core.json.JsonObject;

/**
 * A Kos deployment. Nothing special here, it just wraps Vert.x mechanisms
 * and provides a normalized deployment API.
 */
public interface Deployment {

    /**
     * Deploys a verticle.
     * 
     * @param verticle
     */
    void deploy(Verticle verticle);

    /**
     * Deploys a verticle with a custom configuration.
     * @param verticle
     * @param config
     */
    void deploy(Verticle verticle, JsonObject config);

    /**
     * Deploys a verticles.
     *
     * @param verticles
     */
    void deploy(Iterable<Verticle> verticles);

    /**
     * Deploys verticles with a custom configuration.
     * @param verticles
     * @param config
     */
    void deploy(Iterable<Verticle> verticles, JsonObject config);
}
