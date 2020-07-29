package kos.config;

import injector.ExposedAs;
import kos.api.ConfigurationPlugin;
import kos.api.MutableKosConfiguration;
import lombok.val;

@ExposedAs(ConfigurationPlugin.class)
public class YamlConfigurationPlugin implements ConfigurationPlugin {

    @Override
    public void configure(MutableKosConfiguration kosConfiguration) {
        val retriever = new YamlConfigRetriever(kosConfiguration);
        kosConfiguration.setConfigRetriever(retriever);
    }
}
