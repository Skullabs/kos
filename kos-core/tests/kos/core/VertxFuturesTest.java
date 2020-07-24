/*
 * Copyright © 2020 Secure Code Warrior BVBA. All rights reserved.
 * <p>
 * See your license agreement for the terms and conditions of use.
 * Any other usage without prior written consent of Secure Code Warrior BVBA is prohibited.
 * <p>
 * If there is no license agreement, this material may not be reproduced, displayed, printed, modified
 * or distributed in whole or in part in any manner or on any media without the prior written permission
 * of Secure Code Warrior BVBA. This material is provided 'as is' and 'as available'.
 * Secure Code Warrior BVBA expressly disclaims all warranties of any kind, whether express or implied,
 * including (without limitation) warranties of merchantability, fitness for a particular purpose and
 * non-infringement. Secure Code Warrior BVBA reserves the right to make changes or updates to this
 * material at any time without notice. In no event shall Secure Code Warrior BVBA be liable for any
 * damage (whether indirect, incidental, special, consequential, direct, loss of revenue or profit, loss
 * or corruption of data, loss of use, the cost of procuring replacement goods, opportunity loss, loss
 * of anticipated savings, resulting from or in connection with your use of this material.
 */
package kos.core;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class VertxFuturesTest {

    final VertxFutures futures = new VertxFutures(Vertx.vertx());

    @DisplayName("asFuture(CompletableFuture) SHOULD return a Vertx Future")
    @Test void futureCompletable()
    {
        val expectedIntValue = Integer.valueOf(1230);
        val completableFuture = new CompletableFuture<Integer>();
        val future = futures.asFuture(completableFuture);

        Executors.newSingleThreadExecutor().submit(() -> {
            sleep(100);
            completableFuture.complete(expectedIntValue);
        });

        val value = Lang.waitFor(future);
        assertEquals(expectedIntValue, value);
    }

    @DisplayName("asFuture(Future) SHOULD return itself")
    @Test void future()
    {
        val future = mock(Future.class);
        val found = futures.asFuture(future);
        assertSame(future, found);
    }

    @DisplayName("asFuture(Object) SHOULD return a completed future wrapping the object")
    @Test void futureObject()
    {
        val object = new Object();
        val future = futures.asFuture(object);

        assertTrue(future.succeeded());
        assertSame(object, future.result());
    }

    @SneakyThrows
    static void sleep(long time) {
        Thread.sleep(time);
    }
}