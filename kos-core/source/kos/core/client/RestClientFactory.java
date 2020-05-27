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

package kos.core.client;

import injector.Singleton;
import io.vertx.core.logging.Logger;
import kos.api.KosConfiguration;
import kos.core.KosException;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is the main point to initialize RestClients.
 * Developers are encouraged to use this either instantiate
 * or register RestClients. Auto-generated clients doesn't
 * require to be registered though, as their will be discovered
 * and automatically memorized during the application initialization.
 */
@Singleton
@SuppressWarnings("unchecked")
public class RestClientFactory {

    private static final Function<Class, AutoGeneratedRestClientFactory> NOT_FOUND =
            type -> { throw new KosException("No client implementation found for " + type); };

    final Map<Class, AutoGeneratedRestClientFactory> factories = new HashMap<>();
    final Logger logger;
    final KosConfiguration kosConfiguration;

    public RestClientFactory(KosConfiguration kosConfiguration, Logger logger){
        val found = kosConfiguration.getImplementationLoader().instancesExposedAs(AutoGeneratedRestClientFactory.class);
        for (val factory : found)
            factories.put(factory.getClientType(), factory);
        this.logger = logger;
        this.kosConfiguration = kosConfiguration;
    }

    public <T> T instantiate(RestClientConfiguration conf, Class<T> type) {
        val withDefaults = conf.useDefaultsForNullProperties(kosConfiguration);
        return (T) factories.computeIfAbsent(type, NOT_FOUND)
                .initialize(withDefaults);
    }

    public void register(AutoGeneratedRestClientFactory factory) {
        val previous = factories.putIfAbsent(factory.getClientType(), factory);
        if (previous != null) {
            logger.fatal(
                "Replaced " + previous.getClass().getCanonicalName() +
                " with " + factory.getClass().getCanonicalName() +
                " as factory for Rest Client " + factory.getClientType().getCanonicalName()
            );
        }
    }
}
