package kos.apt;

import kos.apt.events.EventListenerKosProcessor;
import kos.core.exception.KosException;
import kos.sample.events.*;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.Processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventListenerProcessorTest {

    Processor processor = new EventListenerKosProcessor();

    @Nested class ListenerMethodMistakenlyWritten {

        @DisplayName("Should break compilation")
        @Nested class ShouldBreakCompilation {

            @DisplayName("when has more than one parameter")
            @Test void process()
            {
                val source = APT.asSource(APT.testFile(ListenerWithMultipleParameters.class));
                assertThrows(RuntimeException.class, () -> APT.run(processor, source));
            }

            @DisplayName("when returns neither void nor Future<Void>")
            @Test void process2()
            {
                val source = APT.asSource(APT.testFile(ListenerWithInvalidReturnType.class));
                assertThrows(RuntimeException.class, () -> APT.run(processor, source));
            }
        }
    }

    @Nested class ListenerMethodsCorrectlyWritten {

        @Nested class WhenParametersIsValidated {

            @Nested class WhenHasMultipleListeners {

                @DisplayName("should generate class as expected")
                @Test void process()
                {
                    val source = APT.asSource(APT.testFile(ListenerWithValidationAndBothAsyncAndSync.class));
                    APT.run(processor, source);

                    val generatedClassName = ListenerWithValidationAndBothAsyncAndSync.class.getCanonicalName() + "EventListenerConfiguration";
                    val generatedClass = APT.readFileAsString(APT.outputGeneratedClass(generatedClassName));

                    val expectedClass = APT.readFileAsString(APT.testFile(ListenerWithValidationAndBothAsyncAndSyncEventListenerConfiguration.class));
                    assertEquals(expectedClass, generatedClass);
                }

            }
        }

        @Nested class WhenParametersIsNotValidated {

            @Nested class WhenReturnsVoid {

                @DisplayName("should generate class using sync response and basic event handling")
                @Test void process()
                {
                    val source = APT.asSource(APT.testFile(ListenerWithNoValidationAndSync.class));
                    APT.run(processor, source);

                    val generatedClassName = ListenerWithNoValidationAndSync.class.getCanonicalName() + "EventListenerConfiguration";
                    val generatedClass = APT.readFileAsString(APT.outputGeneratedClass(generatedClassName));

                    val expectedClass = APT.readFileAsString(APT.testFile(ListenerWithNoValidationAndSyncEventListenerConfiguration.class));
                    assertEquals(expectedClass, generatedClass);
                }
            }

            @Nested class WhenReturnsFuture {

                @DisplayName("should generate class using async response and basic event handling")
                @Test void process()
                {
                    val source = APT.asSource(APT.testFile(ListenerWithNoValidationAndAsync.class));
                    APT.run(processor, source);

                    val generatedClassName = ListenerWithNoValidationAndAsync.class.getCanonicalName() + "EventListenerConfiguration";
                    val generatedClass = APT.readFileAsString(APT.outputGeneratedClass(generatedClassName));

                    val expectedClass = APT.readFileAsString(APT.testFile(ListenerWithNoValidationAndAsyncEventListenerConfiguration.class));
                    assertEquals(expectedClass, generatedClass);
                }
            }
        }

    }
}
