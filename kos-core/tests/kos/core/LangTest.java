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

import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LangTest {

    @DisplayName("future(Future) SHOULD return itself")
    @Test void future()
    {
        val future = mock(Future.class);
        val found = Lang.asFuture(future);
        assertSame(future, found);
    }

    @DisplayName("future(Object) SHOULD return a completed future wrapping the object")
    @Test void futureObject()
    {
        val object = new Object();
        val future = Lang.asFuture(object);

        assertTrue(future.succeeded());
        assertSame(object, future.result());
    }

    @DisplayName("instantiate(String) SHOULD instantiate a class with default constructor")
    @Test void instantiateString()
    {
        val map = Lang.instantiate("java.util.HashMap");
        assertTrue(map instanceof HashMap);
    }

    @DisplayName("first(Collection) SHOULD return only the first value")
    @Test void filterCollection()
    {
        val even = Lang.first(asList(1,2,3,4));
        assertTrue(even.isPresent());
        assertEquals(1, (int)even.get());
    }

    @DisplayName("filter(Collection,Function) SHOULD return only matched values")
    @Test void filterCollectionFunction()
    {
        val even = Lang.filter(asList(1,2,3,4), i -> i%2 == 0);
        assertEquals(2, even.size());
    }

    @DisplayName("nonEmptySetOfString(Iterable) SHOULD return only non-null and non-blank values")
    @Test void nonEmptySetOfString()
    {
        val hello = Lang.nonEmptySetOfString(asList("", null, "Hello"));
        assertEquals(1, hello.size());
        assertEquals("Hello", hello.get(0));
    }

    @DisplayName("convert(Collection,Function) SHOULD convert all items inside collection using a function")
    @Test void convertCollectionFunction()
    {
        val even = Lang.convert(asList(1,2,3,4), i -> i%2 == 0);
        assertEquals(4, even.size());
        assertFalse(even.get(0));
        assertTrue(even.get(1));
        assertFalse(even.get(2));
        assertTrue(even.get(3));
    }

    @DisplayName("await(Future) SHOULD wait until future is resolved")
    @Test void awaitFuture()
    {
        val waitTime = ThreadLocalRandom.current().nextInt(0, 2000);
        val promise = Promise.<Integer>promise();
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(waitTime);
                promise.complete(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        assertEquals(waitTime, (int)Lang.await(promise.future()));
    }

    @DisplayName("sorted(Iterable,Comparator) SHOULD sort according to the order defined by the Comparator")
    @Test void sortedIterableComparator(){
        val numbers = asList(2, 1, 3, 0);
        val sorted = Lang.sorted(numbers, Integer::compareTo);

        var i = 0;
        for (val number : sorted) {
            assertEquals((int)number, i++);
        }
    }
}