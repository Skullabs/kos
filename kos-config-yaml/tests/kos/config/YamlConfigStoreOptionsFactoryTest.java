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

import io.vertx.config.*;
import io.vertx.core.json.*;
import kos.core.*;
import lombok.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlConfigStoreOptionsFactoryTest {

    @DisplayName("Can read prod yml file")
    @Test void canReadFile(){
        val options = new ConfigRetrieverOptions();
        options.addStore( new YamlConfigStoreOptionsFactory().create() );

        val configRef = new AtomicReference<JsonObject>();
        ConfigRetriever.create( Kos.defaultVertx.get(), options )
                .getConfig( res -> configRef.set( res.result() ));

        val config = awaitFor( configRef );
        val expected = new JsonObject().put("http",
            new JsonObject().put("port", 9000).put("host", "0.0.0.0"));
        assertEquals( expected, config );
    }

    @DisplayName("Can read test yml file")
    @Test void canReadTestFile(){
        val options = new ConfigRetrieverOptions();
        options.addStore( new YamlTestConfigStoreOptionsFactory().create() );

        val configRef = new AtomicReference<JsonObject>();
        ConfigRetriever.create( Kos.defaultVertx.get(), options )
                .getConfig( res -> configRef.set( res.result() ));

        val config = awaitFor( configRef );
        val expected = new JsonObject()
            .put("http",new JsonObject().put("port", 9999))
            .put("https",new JsonObject().put("port", 8443));
        assertEquals( expected, config );
    }

    @DisplayName("Can merge yml files correctly")
    @Test void canMergeYmlCorrectly(){
        val configRef = new AtomicReference<JsonObject>();
        Kos.readConfig(configRef::set);

        val config = awaitFor( configRef );
        val expected = new JsonObject()
            .put("https",new JsonObject().put("port", 8443))
            .put("http",new JsonObject().put("port", 9999).put("host","0.0.0.0"))
        ;
        assertEquals( expected, config );
    }

    <T> T awaitFor(AtomicReference<T> reference ) {
        T result = null;
        while ( (result = reference.get()) == null )
            LockSupport.parkNanos(1L);
        return result;
    }
}