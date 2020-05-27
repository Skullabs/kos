package kos.injector;

import injector.Exposed;
import kos.api.ConfigurationPlugin;
import kos.api.MutableKosConfiguration;
import lombok.val;

@Exposed
public class InjectorConfigurationPlugin implements ConfigurationPlugin {

    @Override
    public void configure(MutableKosConfiguration kosConfiguration) {
        val loader = new InjectorImplementationLoader(kosConfiguration);
        kosConfiguration.setImplementationLoader(loader);
    }
}
