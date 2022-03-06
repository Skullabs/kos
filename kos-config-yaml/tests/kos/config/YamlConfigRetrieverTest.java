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
import kos.api.MutableKosContext;
import lombok.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

import static kos.core.Lang.waitFor;
import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlConfigRetrieverTest {
    
    MutableKosContext kosConfiguration = new MutableKosContext();
    YamlConfigRetriever retriever = new YamlConfigRetriever(kosConfiguration);
    
    @BeforeEach
    void setup(){
        kosConfiguration.setConfigRetriever(retriever);
    }

    @DisplayName("Can read prod yml file")
    @Test void canReadFile(){
        val options = new ConfigRetrieverOptions();
        options.addStore( retriever.createStoreForProduction() );

        val configRef = new AtomicReference<JsonObject>();
        ConfigRetriever.create( kosConfiguration.getDefaultVertx(), options )
                .getConfig( res -> configRef.set( res.result() ));

        val config = waitFor( configRef );
        val expected = new JsonObject().put("http",
            new JsonObject().put("port", 9000).put("host", "0.0.0.0"));
        assertEquals( expected, config );
    }

    @DisplayName("Can read test yml file")
    @Test void canReadTestFile(){
        val options = new ConfigRetrieverOptions();
        options.addStore( retriever.createStoreForTest() );

        val configRef = new AtomicReference<JsonObject>();
        ConfigRetriever.create( kosConfiguration.getDefaultVertx(), options )
                .getConfig( res -> configRef.set( res.result() ));

        val config = waitFor( configRef );
        val expected = new JsonObject()
            .put("http",new JsonObject().put("port", 9999))
            .put("https",new JsonObject().put("port", 8443));
        assertEquals( expected, config );
    }

    @DisplayName("Can merge yml files correctly")
    @Test void canMergeYmlCorrectly(){
        val config = kosConfiguration.getApplicationConfig();
        val expected = new JsonObject()
            .put("https",new JsonObject().put("port", 8443))
            .put("http",new JsonObject().put("port", 9999).put("host","0.0.0.0"))
        ;
        assertEquals( expected, config );
    }
}
