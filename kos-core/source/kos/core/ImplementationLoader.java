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

import lombok.*;
import java.util.*;

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
    <T> Lang.Result<T> instanceOf(Class<T> type );

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
    default <T> Lang.Result<T> anyInstanceOf(Class<T> type ) {
        return Lang.Result.of( instanceOf( type )
            .orElseGet( () -> {
                val found = instancesExposedAs( type ).iterator();
                if ( found.hasNext() )
                    return found.next();
                return null;
            } )
        );
    }

    class SPIImplementationLoader implements ImplementationLoader {

        @Override
        public <T> Iterable<T> instancesExposedAs(Class<T> interfaceType) {
            return ServiceLoader.load(interfaceType);
        }

        @Override
        public <T> Lang.Result<T> instanceOf(Class<T> interfaceType) {
            if (interfaceType.isInterface())
                return Lang.first(instancesExposedAs(interfaceType));
            val instance = Lang.instantiate(interfaceType);
            return Lang.Result.of(instance);
        }
    }
}
