package kos.config;

import injector.Exposed;
import kos.api.ConfigurationPlugin;
import kos.api.MutableKosConfiguration;
import lombok.val;

@Exposed
public class YamlConfigurationPlugin implements ConfigurationPlugin {

    @Override
    public void configure(MutableKosConfiguration kosConfiguration) {
        val retriever = new LazyConfigRetriever(kosConfiguration);
        kosConfiguration.setConfigRetriever(retriever);
    }
}
