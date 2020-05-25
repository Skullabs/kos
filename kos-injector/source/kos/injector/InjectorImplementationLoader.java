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

import injector.Injector;
import io.vertx.core.logging.Logger;
import kos.api.ImplementationLoader;
import kos.core.Kos;
import kos.core.Lang.Lazy;
import kos.core.Lang.Result;
import lombok.val;

import java.util.function.Consumer;

public class InjectorImplementationLoader implements ImplementationLoader {

    private static final ImplementationLoader spi = new SPIImplementationLoader();
    private final Lazy<Injector> injector = Lazy.by( this::createInjector );
    private final Logger logger = Kos.logger(this.getClass());

    private Injector createInjector() {
        val injectorLogger = Kos.logger(Injector.class);
        return Injector.create(false).setLogger(new Consumer<String>() {
            @Override
            public void accept(String s) {
                injectorLogger.debug(s);
            }
        });
    }

    @Override
    public <T> Iterable<T> instancesExposedAs(Class<T> interfaceType) {
        val found = injector.get().instancesExposedAs(interfaceType);
        if ( found.iterator().hasNext() )
            return found;
        return spi.instancesExposedAs(interfaceType);
    }

    @Override
    public <T> Result<T> instanceOf(Class<T> type) {
        try {
            return Result.of(injector.get().instanceOf(type));
        } catch ( IllegalArgumentException cause ) {
            logger.debug("Could not get instance of " + type.getCanonicalName(), cause);
            return Result.failure(cause);
        }
    }
}
