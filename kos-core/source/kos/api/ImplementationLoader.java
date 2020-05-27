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

package kos.api;

import kos.core.KosException;
import kos.core.Lang;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Loads implementations.
 */
public interface ImplementationLoader {

    /**
     * Loads classes exposed by the given {@code interfaceType}.
     *
     * @param interfaceType an abstract class or interface in which concrete
     *                      implementations is expected to be loaded
     * @param <T> same type as {@code interfaceType}
     * @return found implementations or empty {@link Iterable} if none was found.
     */
    <T> Iterable<T> instancesExposedAs(Class<T> interfaceType );

    /**
     * Load the instance of the given {@code type}.
     *
     * @param type expected class to be loaded.
     * @param <T> same type as {@code type}
     * @return an {@link Optional} instance containing or not the found instance.
     */
    <T> Result<T> instanceOf(Class<T> type );

    /**
     * Load the instance of the given {@code type}.
     *
     * @param type expected class to be loaded.
     * @param <T> same type as {@code type}
     * @return found instance
     * @throws KosException if not instance was found.
     */
    default <T> T instanceOfOrFail( Class<T> type ) {
        return instanceOf( type ).orElseGet( (RuntimeException cause) -> {
            var msg = "Could not load implementation for " + type.getCanonicalName();
            if (cause == null) {
                msg+= "\n This usually happens when:";
                msg+= "\n  - there is no SPI defined for your class";
                msg+= "\n  - you are relying on a different ImplementationLoader but you haven't" +
                        " made your class manageable by it. Ex: missing annotations.";
                throw new KosException(msg);
            }
            throw new KosException(cause, msg);
        });
    }

    /**
     * Load the instance of the given {@code type}. It will try first to lookup
     * for instances using {@link ImplementationLoader#instanceOf}. If fail, it
     * will return the first one returned by {@link ImplementationLoader#instancesExposedAs(Class)}.
     * If none has been found, {@code Optional.empty()} will be returned.
     *
     * @param type expected class to be loaded.
     * @param <T> same type as {@code type}
     * @return an {@link Optional} instance containing or not the found instance.
     */
    default <T> Result<T> anyInstanceOf(Class<T> type ) {
        return Result.of( instanceOf( type )
            .orElseGet( () -> {
                val found = instancesExposedAs( type ).iterator();
                if ( found.hasNext() )
                    return found.next();
                return null;
            } )
        );
    }

    <T> void register(Class<T> type, T instance);

    @SuppressWarnings("unchecked")
    class SPIImplementationLoader implements ImplementationLoader {
        
        final Map<Class, Object> instances = new HashMap<>();

        @Override
        public <T> Iterable<T> instancesExposedAs(Class<T> interfaceType) {
            return ServiceLoader.load(interfaceType);
        }

        @Override
        public <T> Result<T> instanceOf(Class<T> type) {
            T found = (T) instances.get(type);
            if (found != null)
                return Result.of(found);
            if (type.isInterface())
                return Lang.first(instancesExposedAs(type));
            val instance = Lang.instantiate(type);
            return Result.of(instance);
        }

        @Override
        public <T> void register(Class<T> type, T instance) {
            instances.put(type, instance);
        }
    }

    /**
     * Customized optional result. It was created so developers might experience optimal
     * experience when using the {@link ImplementationLoader}.
     * @param <T>
     */
    @RequiredArgsConstructor
    @EqualsAndHashCode
    @Accessors(fluent = true)
    class Result<T> {
        
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
