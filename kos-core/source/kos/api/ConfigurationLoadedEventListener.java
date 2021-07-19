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

import io.vertx.core.json.JsonObject;
import lombok.Value;

/**
 * A listener for "Configuration Loaded" event. It can be used to read the configuration
 * file, interact with the configured Vert.x components or deploy tailor made Vert.x Verticles.
 */
public interface ConfigurationLoadedEventListener {

    void on(ConfigurationLoadedEvent configurationLoadedEvent);

    @Value class ConfigurationLoadedEvent {
        KosContext kosContext;
        JsonObject applicationConfig;
    }
}
