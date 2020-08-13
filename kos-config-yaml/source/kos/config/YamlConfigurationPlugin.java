package kos.config;

import injector.ExposedAs;
import kos.api.ConfigurationPlugin;
import kos.api.MutableKosContext;
import lombok.val;

@ExposedAs(ConfigurationPlugin.class)
public class YamlConfigurationPlugin implements ConfigurationPlugin {

    @Override
    public void configure(MutableKosContext kosConfiguration) {
        val retriever = new YamlConfigRetriever(kosConfiguration);
        kosConfiguration.setConfigRetriever(retriever);
    }
}
