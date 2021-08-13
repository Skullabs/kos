package kos.injector;

import injector.ExposedAs;
import kos.api.Plugin;
import kos.api.KosContext;
import kos.api.MutableKosContext;
import lombok.val;

@ExposedAs(Plugin.class)
public class InjectorPlugin implements Plugin {

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void configure(MutableKosContext kosConfiguration) {
        val loader = new InjectorImplementationLoader();
        loader.register(KosContext.class, kosConfiguration);
        kosConfiguration.setImplementationLoader(loader);
    }
}
