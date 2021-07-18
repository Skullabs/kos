package kos.config;

import injector.ExposedAs;
import kos.api.Plugin;
import kos.api.MutableKosContext;
import lombok.val;

@ExposedAs(Plugin.class)
public class YamlPlugin implements Plugin {

    @Override
    public void configure(MutableKosContext kosConfiguration) {
        val retriever = new YamlConfigRetriever(kosConfiguration);
        kosConfiguration.setConfigRetriever(retriever);
    }
}
