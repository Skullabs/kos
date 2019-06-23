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

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kos.core.Lang.await;
import static org.junit.jupiter.api.Assertions.*;

public class KosConfigLoaderTest {

    @DisplayName("CAN read the default configuration")
    @Test void readConfig()
    {
        val reference = Promise.<JsonObject>promise();
        Kos.readConfig(reference::complete);

        val jsonObject = await(reference.future());
        assertNotNull(jsonObject);
    }

    @DisplayName("Read configuration SHOULD be available in Kos.config")
    @Test void config()
    {
        val reference = Promise.<JsonObject>promise();
        Kos.readConfig(reference::complete);

        val jsonObject = await(reference.future());
        assertSame(jsonObject, Kos.config.get());
    }
}
