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
import kos.api.ImplementationLoader;
import kos.core.exception.KosException;
import lombok.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

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

    public static <T> T firstNotNull( T...args ){
        for ( T arg : args ) {
            if (arg != null)
                return arg;
        }
        return null;
    }

    public static <T> ImplementationLoader.Result<T> first(Iterable<T> data ) {
        return first( data, i -> true );
    }

    public static <T> boolean matches(Iterable<T> data, Predicate<T> matcher ){
        for (val item: data){
            if ( matcher.test(item) )
                return true;
        }
        return false;
    }

    public static <T> ImplementationLoader.Result<T> first(Iterable<T> data, Predicate<T> matcher ) {
        for (val item: data){
            if ( matcher.test(item) )
                return ImplementationLoader.Result.of( item );
        }
        return ImplementationLoader.Result.empty();
    }

    public static <T> List<T> filter( Collection<T> data, Predicate<T> matcher ) {
        val buffer = new ArrayList<T>();
        for ( val item : data )
            if (matcher.test(item))
                buffer.add(item);
        return buffer;
    }

    public static <T,N> List<N> convert(Iterable<T> data, Function<T,N> converter) {
        val buffer = new ArrayList<N>();
        for ( val item : data )
            buffer.add( converter.apply(item) );
        return buffer;
    }

    public static <T,N> List<N> convertIndex(Iterable<T> data, BiFunction<Integer,T, N> converter) {
        val buffer = new ArrayList<N>();
        int i = 0;
        for ( val item : data )
            buffer.add( converter.apply(i++, item) );
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

    /**
     * This method has been place here for the sake of convenience.
     * It should avoided by developers as it is quite dangerous and
     * might introduce slowness into the system.
     */
    public static <T> T waitFor(Future<T> future) {
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

    public static <T> T waitFor(AtomicReference<T> reference ) {
        T result = null;
        while ( (result = reference.get()) == null )
            LockSupport.parkNanos(1L);
        return result;
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
}
