package kos.sample;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.function.Function;

public class StubFuture<T> implements Future<T> {

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public Future<T> onComplete(Handler<AsyncResult<T>> handler) {
        return null;
    }

    @Override
    public T result() {
        return null;
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public boolean succeeded() {
        return false;
    }

    @Override
    public boolean failed() {
        return false;
    }

    @Override
    public <U> Future<U> compose(Function<T, Future<U>> function, Function<Throwable, Future<U>> function1) {
        return null;
    }

    @Override
    public <U> Future<U> transform(Function<AsyncResult<T>, Future<U>> function) {
        return null;
    }

    @Override
    public <U> Future<T> eventually(Function<Void, Future<U>> function) {
        return null;
    }

    @Override
    public <U> Future<U> map(Function<T, U> function) {
        return null;
    }

    @Override
    public <V> Future<V> map(V v) {
        return null;
    }

    @Override
    public Future<T> otherwise(Function<Throwable, T> function) {
        return null;
    }

    @Override
    public Future<T> otherwise(T t) {
        return null;
    }
}