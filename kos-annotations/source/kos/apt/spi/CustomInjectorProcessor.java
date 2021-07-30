package kos.apt.spi;

import generator.apt.SimplifiedAST;
import injector.apt.InjectorProcessor;
import lombok.val;

import java.util.Collection;

import static kos.core.Lang.filter;

public class CustomInjectorProcessor extends InjectorProcessor {

    @Override
    public void process(Collection<SimplifiedAST.Type> types) {
        val nonAbstractTypes = filter(types, t -> !t.isAbstract() && !t.isInterface());
        super.process(nonAbstractTypes);
    }
}