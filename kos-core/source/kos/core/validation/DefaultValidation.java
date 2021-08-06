package kos.core.validation;

import io.vertx.core.Future;
import kos.api.Validation;
import lombok.*;

import java.util.*;

import static java.util.Arrays.asList;

/**
 * A predicate-based {@link Validation}.
 */
@RequiredArgsConstructor
@SuppressWarnings("all")
public class DefaultValidation implements Validation {

    @Getter(AccessLevel.PACKAGE)
    private final Map<Class, Validation> validationCache;

    /**
     * The fallback {@link Validation} implementation. It will be called
     * whenever no other validation is available for a given type.
     */
    @Setter
    private Validation fallbackValidation = new AlwaysValid();

    public DefaultValidation(){
        this(new HashMap<>());
    }

    public void memorise(Validation validation) {
        this.validationCache.put(validation.getTypeOfTheObjectBeingValidated(), validation);
    }

    @Override
    public Future validate(Object object, Class targetClass)
    {
        val classes = new ArrayDeque<Class>();
        val type = targetClass;
        populateWithClassAndItsInterfaces(classes, type);

        Class aClass = null;
        while ((aClass = classes.poll()) != null) {
            val validation = this.validationCache.get(aClass);
            if (validation != null)
                return validation.validate(object, aClass);
            populateWithClassAndItsInterfaces(classes, aClass.getSuperclass());
        }

        return fallbackValidation.validate(object, targetClass);
    }

    private void populateWithClassAndItsInterfaces(Queue<Class> stack, Class klass) {
        if (!Object.class.equals(klass) && klass != null) {
            stack.add(klass);
            stack.addAll(asList(klass.getInterfaces()));
        }
    }

    @Override
    public Class<Object> getTypeOfTheObjectBeingValidated() {
        return Object.class;
    }

    /**
     * A {@link Validation} that assume all objects are valid. It was designed
     * as an optimistic-validator for cases where no validation was defined by
     * the developer.
     */
    static class AlwaysValid implements Validation<Object> {

        @Override
        public Class getTypeOfTheObjectBeingValidated() {
            return Object.class;
        }

        @Override
        public Future<Object> validate(Object object, Class<Object> targetClass) {
            return Future.succeededFuture(object);
        }
    }
}