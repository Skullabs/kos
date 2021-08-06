package kos.apt;

import kos.apt.validation.ValidatorProcessor;
import kos.sample.validation.*;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.Processor;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidatorProcessorTest {
    
    Processor processor = new ValidatorProcessor();

    @Nested
    class ValidatorMethodMistakenlyWritten {

        @DisplayName("Should break compilation")
        @Nested class ShouldBreakCompilation {

            @DisplayName("when has more than one parameter")
            @Test
            void process()
            {
                val source = APT.asSource(APT.testFile(ValidatorMethodWithMultipleParameters.class));
                assertThrows(RuntimeException.class, () -> APT.run(processor, source));
            }

            @DisplayName("when returns neither void nor Future<Void>")
            @Test void process2()
            {
                val source = APT.asSource(APT.testFile(ValidatorMethodWithInvalidReturnType.class));
                assertThrows(RuntimeException.class, () -> APT.run(processor, source));
            }
        }
    }

    @Nested class ValidatorMethodsCorrectlyWritten {

        @Nested
        class WhenParametersIsValidated {

            @Nested
            class WhenHasMultipleValidators {

                @DisplayName("should generate one class for each method as expected")
                @Test
                void process() {
                    val source = APT.asSource(APT.testFile(ValidatorWithMultipleValidations.class));
                    APT.run(processor, source);

                    val expectedClasses = asList(
                            ValidatorWithMultipleValidations$List$Validation1.class,
                            ValidatorWithMultipleValidations$List$Validation2.class);

                    for (int i = 0; i < expectedClasses.size(); i++) {
                        val expectedClass = expectedClasses.get(i);
                        val expectedClassAsString = APT.readFileAsString(APT.testFile(expectedClass));

                        val generatedClassName = ValidatorWithMultipleValidations.class.getCanonicalName() + "$List$Validation" + (i+1);
                        val generatedClassAsString = APT.readFileAsString(APT.outputGeneratedClass(generatedClassName));

                        assertEquals(expectedClassAsString, generatedClassAsString);
                    }
                }

            }
        }

        @Nested
        class WhenParametersIsNotValidated {

            @Nested
            class WhenReturnsVoid {

                @DisplayName("should generate class using sync response and basic event handling")
                @Test
                void process() {
                    val source = APT.asSource(APT.testFile(SyncValidator.class));
                    APT.run(processor, source);

                    val generatedClassName = SyncValidator.class.getCanonicalName() + "$UUID$Validation1";
                    val generatedClassAsString = APT.readFileAsString(APT.outputGeneratedClass(generatedClassName));

                    val expectedClassAsString = APT.readFileAsString(APT.testFile(SyncValidator$UUID$Validation1.class));
                    assertEquals(expectedClassAsString, generatedClassAsString);
                }
            }

            @Nested
            class WhenReturnsFuture {

                @DisplayName("should generate class using async response and basic event handling")
                @Test
                void process() {
                    val source = APT.asSource(APT.testFile(AsyncValidator.class));
                    APT.run(processor, source);

                    val generatedClassName = AsyncValidator.class.getCanonicalName() + "$UUID$Validation1";
                    val generatedClassAsString = APT.readFileAsString(APT.outputGeneratedClass(generatedClassName));

                    val expectedClassAsString = APT.readFileAsString(APT.testFile(AsyncValidator$UUID$Validation1.class));
                    assertEquals(expectedClassAsString, generatedClassAsString);
                }
            }
        }
    }
}
