package kos.apt.events;

import generator.apt.SimplifiedAST;
import generator.apt.SimplifiedAbstractProcessor;
import kos.api.ConfigurationLoadedEventListener;
import kos.apt.ClassGenerator;
import kos.apt.spi.CustomInjectorProcessor;
import kos.apt.spi.SPIGenerator;
import kos.events.Publisher;
import lombok.val;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static kos.core.Lang.convert;

@SupportedAnnotationTypes( { "kos.events.*" } )
public class EventPublisherKosProcessor extends SimplifiedAbstractProcessor {

    private final CustomInjectorProcessor injectorProcessor = new CustomInjectorProcessor();
    private ClassGenerator classGenerator;
    private SPIGenerator configurationLoadedSpiGenerator;

    public EventPublisherKosProcessor() {
        super(
            emptyList(),
            singletonList(Publisher.class),
            emptyList()
        );
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        classGenerator = new ClassGenerator("template-event-publisher-java.mustache", processingEnv);
        injectorProcessor.init(processingEnv);

        val configurationLoadedSpiLocation = "META-INF/services/" + ConfigurationLoadedEventListener.class.getCanonicalName();
        configurationLoadedSpiGenerator = new SPIGenerator(processingEnv, resourceLocator, configurationLoadedSpiLocation);
    }

    @Override
    protected void process(Collection<SimplifiedAST.Type> types) {
        try {
            val eventPublisherTypes = convert(types, EventPublisherType::from);
            classGenerator.generateClasses(eventPublisherTypes);
            generateSpiDescriptors(eventPublisherTypes);
            createSPIFileForInterfaces(eventPublisherTypes);
        } catch (Throwable cause) {
            throw new RuntimeException(cause);
        }
    }

    /**
     * Writes the SPI files for {@link ConfigurationLoadedEventListener}.
     */
    private void generateSpiDescriptors(List<EventPublisherType> eventListenerTypes) throws IOException {
        configurationLoadedSpiGenerator.flushSPIClasses();
        configurationLoadedSpiGenerator.memorizeSPIFor(eventListenerTypes);
        configurationLoadedSpiGenerator.generateSPIFiles();
    }

    /**
     * Will generate SPI files for the just created Publisher types.
     */
    private void createSPIFileForInterfaces(List<EventPublisherType> eventPublisherTypes) throws IOException {
        for (val publisherType : eventPublisherTypes) {
            val spiLocation = "META-INF/services/" + publisherType.packageName + "." + publisherType.eventPublisherInterfaceName;
            val generator = new SPIGenerator(processingEnv, resourceLocator, spiLocation);
            generator.flushSPIClasses();
            generator.memorizeSPIFor(publisherType);
            generator.generateSPIFiles();
        }
    }
}
