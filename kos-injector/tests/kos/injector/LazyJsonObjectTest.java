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
import kos.core.KosException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LazyJsonObjectTest {
    
    @Mock JsonObject delegatedJsonObject;
    
    @BeforeEach
    void setupMocks(){
    }

    @DisplayName("SHOULD delegate to a previously loaded config")
    @Test void canDelegate()
    {
        doReturn("World").when(delegatedJsonObject).getString("Hello");
        Kos.config.set(delegatedJsonObject);
        
        val lazy = new LazyJsonObject(Kos.config::get);
        assertEquals("World", lazy.getString("Hello"));
        verify(delegatedJsonObject).getString(eq("Hello"));
    }

    @DisplayName("SHOULD delegate to a lazy loaded config that will be set after the LazyJsonObject creation")
    @Test void canDelegate1()
    {
        doReturn("World").when(delegatedJsonObject).getString("Hello");
        val lazy = new LazyJsonObject(Kos.config::get);
        Kos.config.set(delegatedJsonObject);

        assertEquals("World", lazy.getString("Hello"));
        verify(delegatedJsonObject).getString(eq("Hello"));
    }

    @DisplayName("SHOULD FAIL to delegate to a lazy loaded config that was never defined")
    @Test void cannotDelegate()
    {
        val lazy = new LazyJsonObject(Kos.config::get);
        Kos.config.set(null);

        assertThrows(KosException.class, () -> lazy.getString("Hello"));
    }
}