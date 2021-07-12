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

import kos.api.ImplementationLoader;
import kos.core.exception.KosException;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {
    
    @DisplayName("SHOULD be able to create empty Result")
    @Test void empty(){
        val result = ImplementationLoader.Result.empty();
        assertTrue(result.isEmpty());
        assertFalse(result.failed());
    }

    @DisplayName("SHOULD be able to create a Result with data")
    @Test void of(){
        val result = ImplementationLoader.Result.of("Hello");
        assertFalse(result.failed());
        assertFalse(result.isEmpty());
        assertEquals("Hello", result.get());
    }

    @DisplayName("SHOULD be able to create a Result with a failure")
    @Test void failure(){
        val result = ImplementationLoader.Result.failure(new KosException("Unknown Error"));
        assertTrue(result.failed());
        assertTrue(result.isEmpty());
    }
}
