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

package kos.injector;

import io.vertx.core.json.JsonObject;
import kos.core.Kos;
import kos.core.Lang;
import lombok.experimental.Delegate;

import java.util.function.Supplier;

public class LazyJsonObject extends JsonObject {

    private final Lang.Lazy<JsonObject> jsonObject;

    LazyJsonObject(Supplier<JsonObject> supplier) {
        jsonObject = Lang.Lazy.by(supplier);
    }

    @Delegate private JsonObject readJsonObject(){
        return jsonObject.get();
    }
}
