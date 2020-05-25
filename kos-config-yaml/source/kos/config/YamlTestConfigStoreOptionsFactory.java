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

package kos.config;

import injector.*;
import io.vertx.config.*;
import io.vertx.core.json.*;
import kos.api.ConfigStoreOptionsFactory;

@Singleton @ExposedAs(ConfigStoreOptionsFactory.class)
public class YamlTestConfigStoreOptionsFactory implements ConfigStoreOptionsFactory {

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public ConfigStoreOptions create() {
        return new ConfigStoreOptions()
            .setOptional(true)
            .setType("file")
            .setFormat("yaml")
            .setConfig(new JsonObject().put("path", "conf/application-test.yml"));
    }
}
