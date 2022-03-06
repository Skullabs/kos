package kos.apt;

import kos.apt.events.EventListenerKosProcessor;
import kos.apt.events.EventPublisherKosProcessor;
import kos.sample.events.ListenerWithValidationAndBothAsyncAndSync;
import kos.sample.events.ListenerWithValidationAndBothAsyncAndSyncEventListenerConfiguration;
import kos.sample.events.PublisherWithMultipleMethods;
import kos.sample.events.PublisherWithMultipleMethodsImpl;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.Processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventPublisherProcessorTest {

    Processor processor = new EventPublisherKosProcessor();

    @Nested class ListenerMethodsCorrectlyWritten {

        @Nested class WhenParametersIsValidated {

            @Nested class WhenHasMultiplePublishers {

                @DisplayName("should generate class as expected")
                @Test void process()
                {
                    val source = APT.asSource(APT.testFile(PublisherWithMultipleMethods.class));
                    APT.run(processor, source);

                    val generatedClassName = PublisherWithMultipleMethods.class.getCanonicalName() + "Impl";
                    val generatedClass = APT.readFileAsString(APT.outputGeneratedClass(generatedClassName));

                    val expectedClass = APT.readFileAsString(APT.testFile(PublisherWithMultipleMethodsImpl.class));
                    assertEquals(expectedClass, generatedClass);
                }
            }
        }
    }
}
