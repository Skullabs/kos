/*
 * Copyright 2020 Skullabs Contributors (https://github.com/skullabs)
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
package kos.logging;

import injector.ExposedAs;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import kos.api.ConfigurationPlugin;
import kos.api.MutableKosContext;

/**
 *
 */
@ExposedAs(ConfigurationPlugin.class)
public class Slf4jConfigurationPlugin implements ConfigurationPlugin
{
    @Override public void configure(MutableKosContext kosConfiguration)
    {
        kosConfiguration.setLogDelegateFactory(new SLF4JLogDelegateFactory());
    }
}
