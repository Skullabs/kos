package kos.core.validation;

import kos.api.Validation;
import kos.core.Lang;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static io.vertx.core.Future.succeededFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("rawtypes")
@DisplayName("Default ValidationFactory")
class DefaultValidationTest {

    @Mock Validation validation;

    @DisplayName("Memorising validations")
    @Nested class Memorising {

        @DisplayName("should store validation in the cache")
        @Test void memorise() {
            val cache = new HashMap<Class, Validation>();
            val defaultValidation = new DefaultValidation(cache);

            doReturn(String.class).when(validation).getTypeOfTheObjectBeingValidated();
            defaultValidation.memorise(validation);

            assertTrue(cache.containsKey(String.class));
            assertEquals(validation, cache.get(String.class));
        }
    }

    @DisplayName("Validating any object")
    @Nested class Validating {

        @DisplayName("when there is a validation defined for the type a given object")
        @Nested
        class TypeExistsInCache {

            DefaultValidation defaultValidation;

            @BeforeEach
            void configureValidation(){
                doReturn(String.class).when(validation).getTypeOfTheObjectBeingValidated();
                doAnswer(ctx -> succeededFuture("World")).when(validation).validate(any(), eq(String.class));

                defaultValidation = new DefaultValidation();
                defaultValidation.memorise(validation);
            }

            @DisplayName("it should validate using the specified validation")
            @Test
            void validate() {
                defaultValidation.validate("Hello", String.class);
                verify(validation).validate(eq("Hello"), eq(String.class));
            }

            @DisplayName("it should return the result produced by the validation")
            @Test
            void validate1() {
                val future = defaultValidation.validate("Hello", String.class);
                val result = Lang.waitFor(future);
                assertEquals("World", result);
            }
        }

        @DisplayName("when there is a validation defined for the supertype of a given object")
        @Nested
        class SuperTypeExistsInCache {

            DefaultValidation defaultValidation;

            @BeforeEach
            void configureValidation(){
                doReturn(CharSequence.class).when(validation).getTypeOfTheObjectBeingValidated();
                doAnswer(ctx -> succeededFuture("World")).when(validation).validate(any(), eq(CharSequence.class));

                defaultValidation = new DefaultValidation();
                defaultValidation.memorise(validation);
            }

            @DisplayName("it should validate using the specified validation")
            @Test
            void validate()
            {
                defaultValidation.validate("Hello", String.class);
                verify(validation).validate(eq("Hello"), eq(CharSequence.class));
            }

            @DisplayName("it should return the result produced by the validation")
            @Test
            void validate1()
            {
                val future = defaultValidation.validate("Hello", String.class);
                val result = Lang.waitFor(future);
                assertEquals("World", result);
            }
        }

        @DisplayName("when no validation is available for a given object")
        @Nested
        class Fallback {

            DefaultValidation defaultValidation;
            Validation fallbackValidation = spy(new DefaultValidation.AlwaysValid());

            @BeforeEach
            void configureValidation()
            {
                defaultValidation = new DefaultValidation();
                defaultValidation.setFallbackValidation(fallbackValidation);
            }

            @DisplayName("it should validate using the fallback validation")
            @Test void validate()
            {
                defaultValidation.validate("Hello", String.class);
                verify(fallbackValidation).validate(eq("Hello"), eq(String.class));
            }

            @DisplayName("it should return the result produced by the fallback validation")
            @Test void validate1()
            {
                val future = defaultValidation.validate("Hello", String.class);
                val result = Lang.waitFor(future);
                assertEquals("Hello", result);
            }
        }
    }
}
