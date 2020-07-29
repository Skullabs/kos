package kos.injector;

import injector.ExposedAs;
import kos.api.ConfigurationPlugin;
import kos.api.KosConfiguration;
import kos.api.MutableKosConfiguration;
import lombok.val;

@ExposedAs(ConfigurationPlugin.class)
public class InjectorConfigurationPlugin implements ConfigurationPlugin {

    @Override
    public void configure(MutableKosConfiguration kosConfiguration) {
        val loader = new InjectorImplementationLoader(kosConfiguration);
        loader.register(KosConfiguration.class, kosConfiguration);
        kosConfiguration.setImplementationLoader(loader);
    }
}
