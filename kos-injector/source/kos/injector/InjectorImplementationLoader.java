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

package kos.injector;

import injector.Factory;
import injector.Injector;
import io.vertx.core.logging.Logger;
import kos.api.ImplementationLoader;
import kos.api.KosContext;
import lombok.Value;
import lombok.val;

public class InjectorImplementationLoader implements ImplementationLoader {

    private final Injector injector;
    private final KosContext kosContext;
    private final Logger logger;

    public InjectorImplementationLoader(KosContext kosContext) {
        this.kosContext = kosContext;
        this.logger = kosContext.createLoggerFor(getClass());
        this.injector = createInjector();
    }

    private Injector createInjector() {
        val injectorLogger = kosContext.createLoggerFor(Injector.class);
        return Injector.create(false).setLogger(injectorLogger::debug);
    }

    @Override
    public <T> Iterable<T> instancesExposedAs(Class<T> interfaceType) {
        val found = injector.instancesExposedAs(interfaceType);
        if ( found.iterator().hasNext() )
            return found;
        return kosContext.getSpi().instancesExposedAs(interfaceType);
    }

    @Override
    public <T> Result<T> instanceOf(Class<T> type) {
        try {
            return Result.of(injector.instanceOf(type));
        } catch ( IllegalArgumentException cause ) {
            logger.debug("Could not get instance of " + type.getCanonicalName(), cause);
            return Result.failure(cause);
        }
    }

    @Override
    public <T> void register(Class<T> type, T instance) {
        injector.registerFactoryOf(type, new StaticFactory<>(type, instance));
    }

    @Value
    private static class StaticFactory<T> implements Factory<T> {

        Class<T> exposedType;
        T instance;

        @Override
        public T create(Injector injector, Class aClass) {
            return instance;
        }
    }
}
