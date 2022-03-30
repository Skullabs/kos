package kos.core;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import kos.api.MutableKosContext;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class VertxFuturesTest {

    final MutableKosContext configuration =
        new MutableKosContext().setDefaultVertx(Vertx.vertx());

    final VertxFutures futures = new VertxFutures(configuration);

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

    @DisplayName("asFuture(JDK Future) SHOULD return a Vertx Future")
    @Test void jdkFuture()
    {
        val expectedIntValue = Integer.valueOf(2120);
        val jdkFuture = Executors.newSingleThreadExecutor().submit( () -> {
            sleep(100);
            return expectedIntValue;
        });
        val future = futures.asFuture(jdkFuture);
        val value = Lang.waitFor(future);
        assertEquals(expectedIntValue, value);
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