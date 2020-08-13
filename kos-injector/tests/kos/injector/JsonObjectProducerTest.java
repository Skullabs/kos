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
import kos.api.MutableKosContext;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class JsonObjectProducerTest {

    MutableKosContext kosConfiguration = new MutableKosContext();
    @Mock JsonObject delegatedJsonObject;

    @BeforeEach
    void setupMocks(){
        doReturn("World").when(delegatedJsonObject).getString("Hello");
        kosConfiguration.setApplicationConfig(delegatedJsonObject);
    }

    @DisplayName("SHOULD delegate to the created Kos configuration")
    @Test void produce() {
        val producer = new JsonObjectProducer(kosConfiguration);
        val jsonObject = producer.produce();
        assertEquals("World", jsonObject.getString("Hello"));
    }
}