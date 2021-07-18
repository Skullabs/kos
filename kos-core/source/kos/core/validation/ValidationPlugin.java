package kos.core.validation;

import injector.Exposed;
import kos.api.Plugin;
import kos.api.MutableKosContext;
import kos.api.Validation;
import kos.core.exception.PredicateExceptionHandler;
import lombok.val;

/**
 * Automatically configures the {@link DefaultValidation} and memorises
 * all found validations in the class path.
 */
@Exposed
public class ValidationPlugin implements Plugin {

    @Override
    public void configure(MutableKosContext kosConfiguration) {
        configureDefaultValidation(kosConfiguration);
        registerExceptionHandler(kosConfiguration);
    }

    private void configureDefaultValidation(MutableKosContext kosConfiguration) {
        val validation = kosConfiguration.getDefaultValidation();
        if (!(validation instanceof DefaultValidation)) return;

        val defaultValidation = (DefaultValidation)validation;
        val implementationLoader = kosConfiguration.getImplementationLoader();
        val validations = implementationLoader.instancesExposedAs(Validation.class);

        for (val foundValidation : validations)
            defaultValidation.memorise(foundValidation);

        kosConfiguration.getImplementationLoader().register(Validation.class, defaultValidation);
    }

    private void registerExceptionHandler(MutableKosContext kosConfiguration) {
        val exceptionHandler = kosConfiguration.getExceptionHandler();
        if (!(exceptionHandler instanceof PredicateExceptionHandler)) return;

        val predicateExceptionHandler = (PredicateExceptionHandler)exceptionHandler;
        predicateExceptionHandler.add(t -> t instanceof ValidationException, new ValidationExceptionHandler());
    }
}
