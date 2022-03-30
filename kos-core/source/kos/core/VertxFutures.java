package kos.core;

import injector.Singleton;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import kos.api.KosContext;
import lombok.val;

import java.util.concurrent.CompletableFuture;

/**
 *
 */
@Singleton
public class VertxFutures {

    final Vertx vertx;

    public VertxFutures(KosContext configuration) {
        this.vertx = configuration.getDefaultVertx();
    }

    public <T> Future<T> asFuture(Future<T> value) {
        return value;
    }

    public <T> Future<T> asFuture(CompletableFuture<T> value) {
        val promise = Promise.<T>promise();
        value.handleAsync((result, cause) -> {
            if (cause == null)
                promise.complete(result);
            else
                promise.fail(cause);
            return null;
        }, vertx.nettyEventLoopGroup());
        return promise.future();
    }

    public <T> Future<T> asFuture(java.util.concurrent.Future<T> value) {
        val promise = Promise.<T>promise();
        vertx.executeBlocking(future -> {
            try {
                val result = value.get();
                future.complete(result);
            } catch (Throwable cause) {
                future.fail(cause);
            }
        }, promise);
        return promise.future();
    }

    public <T> Future<T> asFuture(T value) {
        return Future.succeededFuture(value);
    }
}
