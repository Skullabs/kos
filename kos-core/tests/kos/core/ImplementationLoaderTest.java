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

import kos.core.sample.ExposedService;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ImplementationLoaderTest {

    final ImplementationLoader spiLoader = new ImplementationLoader.SPIImplementationLoader();

    @DisplayName("anyInstanceOf SHOULD return the first instance WHEN loading known type")
    @Test void anyInstanceOfSPIService(){
        val service = spiLoader.anyInstanceOf(ExposedService.class);
        assertTrue(service.isPresent());
        assertNotNull(service.get());
        assertEquals("Hello World", service.get().sayHello());
    }

    @DisplayName("instanceOf SHOULD return one WHEN loading known type")
    @Test void instanceOfSPIService(){
        val service = spiLoader.instanceOf(ExposedService.class);
        assertFalse(service.isEmpty());
        assertNotNull(service.get());
        assertEquals("Hello World", service.get().sayHello());
    }

    @DisplayName("instanceOf SHOULD return new instance WHEN loading type with default constructor")
    @Test void instanceOfDefaultConstructor(){
        val map = spiLoader.instanceOf(HashMap.class);
        assertFalse(map.isEmpty());
        assertNotNull(map.get());
    }

    @DisplayName("instanceOf SHOULD return empty WHEN loading unknown type")
    @Test void instanceOfIsEmpty(){
        val map = spiLoader.instanceOf(Map.class);
        assertTrue(map.isEmpty());
    }

    @DisplayName("instanceOfOrFail SHOULD throw exception WHEN loading unknown type")
    @Test void instanceOfOrFailFailsOnUnknown(){
        assertThrows(KosException.class,
            () -> spiLoader.instanceOfOrFail(Map.class));
    }

}