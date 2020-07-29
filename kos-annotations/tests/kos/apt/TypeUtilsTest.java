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

package kos.apt;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeUtilsTest {

    @Test @DisplayName("SHOULD return the boxed version of given type")
    void extractGenericType2() {
        assertEquals("java.lang.Integer", TypeUtils.getBoxedType("int"));
        assertEquals("java.lang.Long", TypeUtils.getBoxedType("long"));
        assertEquals("java.lang.Float", TypeUtils.getBoxedType("float"));
        assertEquals("java.lang.Short", TypeUtils.getBoxedType("short"));
        assertEquals("java.lang.Double", TypeUtils.getBoxedType("double"));
        assertEquals("java.lang.Float", TypeUtils.getBoxedType("float"));
        assertEquals("java.lang.Character", TypeUtils.getBoxedType("char"));
    }

    @Test @DisplayName("SHOULD return object unwrapped (inside Future generic)")
    void unwrapFutureGenericType(){
        val wrapped = "io.vertx.core.Future<java.client.List<java.lang.String>>";
        val expected = "java.client.List<java.lang.String>";
        assertEquals( expected, TypeUtils.unwrapFutureGenericType(wrapped) );
    }

    @Test @DisplayName("SHOULD return object unwrapped (inside JDK Future generic)")
    void unwrapFutureGenericType2(){
        val wrapped = "java.util.concurrent.Future<java.client.List<java.lang.String>>";
        val expected = "java.client.List<java.lang.String>";
        assertEquals( expected, TypeUtils.unwrapFutureGenericType(wrapped) );
    }

    @Test @DisplayName("SHOULD return object unwrapped (inside CompletableFuture generic)")
    void unwrapFutureGenericType3(){
        val wrapped = "java.util.concurrent.CompletableFuture<java.client.List<java.lang.String>>";
        val expected = "java.client.List<java.lang.String>";
        assertEquals( expected, TypeUtils.unwrapFutureGenericType(wrapped) );
    }

    @Test @DisplayName("SHOULD return origin class when is not inside Future generic")
    void unwrapFutureGenericType1(){
        val wrapped = "java.client.List<java.lang.String>";
        val expected = "java.client.List<java.lang.String>";
        assertEquals( expected, TypeUtils.unwrapFutureGenericType(wrapped) );
    }

    @Test @DisplayName("SHOULD return raw class (without generics)")
    void rawType(){
        val wrapped = "java.client.List<java.lang.String>";
        val expected = "java.client.List";
        assertEquals( expected, TypeUtils.rawType(wrapped).get() );
    }
}
