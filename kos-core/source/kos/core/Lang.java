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
import lombok.*;
import lombok.experimental.Accessors;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility methods internally used by Kos. We do not encourage
 * you to rely on them as they may be discontinued without any notice.
 */
@SuppressWarnings("unchecked")
public final class Lang {

    private Lang(){}

    public static <T> T instantiate( String canonicalName ) {
        return (T) instantiate( classFor(canonicalName) );
    }

    public static <T> Class<T> classFor( String canonicalName ){
        try {
            return (Class<T>) Class.forName( canonicalName );
        } catch (ClassNotFoundException e) {
            throw new KosException( e );
        }
    }

    public static <T> T instantiate( Class<T> type ) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new KosException( e );
        }
    }

    public static <T> Lang.Result<T> first(Iterable<T> data ) {
        return first( data, i -> true );
    }

    public static <T> boolean matches(Iterable<T> data, Function<T, Boolean> matcher ){
        for (val item: data){
            if ( matcher.apply(item) )
                return true;
        }
        return false;
    }

    public static <T> Lang.Result<T> first( Iterable<T> data, Function<T, Boolean> matcher ) {
        for (val item: data){
            if ( matcher.apply(item) )
                return Lang.Result.of( item );
        }
        return Lang.Result.empty();
    }

    public static <T> List<T> filter( Collection<T> data, Function<T, Boolean> matcher ) {
        val buffer = new ArrayList<T>();
        for ( val item : data )
            if (matcher.apply(item))
                buffer.add(item);
        return buffer;
    }

    public static <T,N> List<N> convert(Iterable<T> data, Function<T,N> converter) {
        val buffer = new ArrayList<N>();
        for ( val item : data )
            buffer.add( converter.apply(item) );
        return buffer;
    }

    public static <K,V> MapBuilder<K,V> mapOf( K key, V value ) {
        return new MapBuilder<K,V>().and(key,value);
    }

    public static List<String> nonEmptySetOfString(Iterable<String> values ) {
        val buffer = new ArrayList<String>();
        for ( val value : values )
            if ( value != null && !value.isEmpty() )
                buffer.add(value);
        return buffer;
    }

    public static <T> Iterable<T> sorted(Iterable<T> data, Comparator<T> comparator ) {
        val buffered = new ArrayList<T>();
        data.forEach( buffered::add );
        buffered.sort( comparator );
        return buffered;
    }

    public static <T> Future<T> asFuture(Future<T> value) {
        return value;
    }

    public static <T> Future<T> asFuture(T value) {
        return Future.succeededFuture(value);
    }

    /**
     * This method has been place here for the sake of convenience.
     * It should avoided by developers as it is quite dangerous and
     * might introduce slowness into the system.
     */
    public static <T> T await(Future<T> future) {
        val parent = Thread.currentThread().getStackTrace()[1];
        if (!parent.getClassName().startsWith("kos."))
            throw new UnsupportedOperationException("Await was not designed for production usage");

        while (!future.succeeded() && !future.failed())
            LockSupport.parkNanos(2);

        if (future.succeeded())
            return future.result();
        else
            throw new KosException(future.cause().getMessage(), future.cause());
    }

    /**
     * Convenient HashMap builder.
     *
     * @param <K>
     * @param <V>
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MapBuilder<K,V> {
        final Map<K,V> data = new HashMap<>();

        public MapBuilder<K,V> and( K key, V value ) {
            data.put(key, value);
            return this;
        }

        public Map<K,V> build(){
            return data;
        }
    }

    /**
     * Wraps elements that should be load lazily to avoid issues
     * when loading them using Dependency Injection libraries other
     * than SPI.
     *
     * @param <T>
     */
    @RequiredArgsConstructor
    public static class Lazy<T> implements Supplier<T> {

        private final Supplier<T> supplier;
        private T data;

        public T get(){
            if ( data == null )
                synchronized (this) {
                    if ( data == null )
                        data = supplier.get();
                }
            return data;
        }

        public synchronized void set( T newData ) {
            this.data = newData;
        }

        public static <T> Lazy<T> by( Supplier<T> supplier ) {
            return new Lazy<>( supplier );
        }
    }
    
    @RequiredArgsConstructor
    @EqualsAndHashCode
    @Accessors(fluent = true)
    public static class Result<T> {
        
        final RuntimeException cause;
        final T data;
        
        public boolean isEmpty(){
            return data == null;
        }

        public boolean isPresent(){
            return !isEmpty();
        }
        
        public boolean failed(){
            return cause != null;
        }
        
        public T get(){
            return data;
        }

        public <E extends RuntimeException> T orElseThrow(Supplier<E> cause) {
            if (isEmpty())
                throw cause.get();
            return data;
        }

        public T orElse(T other){
            if (isEmpty())
                return other;
            return data;
        }

        public T orElseGet(Supplier<T> other) {
            if (isEmpty())
                return other.get();
            return data;
        }

        public T orElseGet(Function<RuntimeException,T> other) {
            if (isEmpty())
                return other.apply(cause);
            return data;
        }

        public <R> Result<R> map(Function<T, R> mapper) {
            if (isEmpty())
                return empty();
            return of(mapper.apply(data));
        }

        @Override
        public String toString() {
            if (cause == null && data == null)
                return "Result{empty=true}";
            return "Result{" +
                    "cause=" + cause +
                    ", data=" + data +
                    '}';
        }

        public static <T, E extends RuntimeException> Result<T> failure(E cause){
            return new Result<>(cause, null);
        }

        public static <T> Result<T> of(T value){
            return new Result<>(null, value);
        }

        public static <T> Result<T> empty(){
            return new Result<>(null, null);
        }
    }
}
