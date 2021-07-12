package kos.core.validation;

import kos.api.ExceptionHandler;
import kos.api.ImplementationLoader;
import kos.api.MutableKosContext;
import kos.api.Validation;
import kos.core.exception.PredicateExceptionHandler;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SuppressWarnings("all")
class ValidationPluginTest {

    final ImplementationLoader implLoader = mock(ImplementationLoader.class);
    final MutableKosContext kosConf = spy(new MutableKosContext(implLoader));

    @DisplayName("when PredicateExceptionHandler is used as default exception handler mechanism")
    @Nested class PredicateExceptionHandlerIsPresent {

        PredicateExceptionHandler exceptionHandler;
        DefaultValidation defaultValidation;

        @BeforeEach
        void setupMock(){
            exceptionHandler = mock(PredicateExceptionHandler.class);
            kosConf.setExceptionHandler(exceptionHandler);
        }

        @DisplayName("should register the ValidationExceptionHandler")
        @Test void memorise()
        {
            val plugin = new ValidationPlugin();
            plugin.configure(kosConf);

            verify(exceptionHandler).add(any(), any(ValidationExceptionHandler.class));
        }
    }

    @DisplayName("when DefaultValidation is used as kos default validation mechanism")
    @Nested class DefaultValidationIsPresent {

        Validation validation;
        DefaultValidation defaultValidation;

        @BeforeEach
        void setupMock(){
            validation = mock(Validation.class);
            val availableValidation = List.of(validation);

            doReturn(Object.class).when(validation).getTypeOfTheObjectBeingValidated();
            doReturn(availableValidation).when(implLoader).instancesExposedAs(eq(Validation.class));

            defaultValidation = mock(DefaultValidation.class);
            kosConf.setDefaultValidation(defaultValidation);
        }

        @DisplayName("Should populate cache with all found Validation in the class path")
        @Test void populate()
        {
            val plugin = new ValidationPlugin();
            plugin.configure(kosConf);

            verify(defaultValidation).memorise(eq(validation));
        }

        @DisplayName("DefaultValidation must be registered as dependency in ImplementationLoader")
        @Test void register()
        {
            val plugin = new ValidationPlugin();
            plugin.configure(kosConf);

            verify(implLoader).register(eq(Validation.class), eq(defaultValidation));
        }
    }
}