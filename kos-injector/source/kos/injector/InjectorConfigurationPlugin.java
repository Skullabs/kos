package kos.injector;

import injector.ExposedAs;
import kos.api.ConfigurationPlugin;
import kos.api.KosContext;
import kos.api.MutableKosContext;
import lombok.val;

@ExposedAs(ConfigurationPlugin.class)
public class InjectorConfigurationPlugin implements ConfigurationPlugin {

    @Override
    public void configure(MutableKosContext kosConfiguration) {
        val loader = new InjectorImplementationLoader(kosConfiguration);
        loader.register(KosContext.class, kosConfiguration);
        kosConfiguration.setImplementationLoader(loader);
    }
}
