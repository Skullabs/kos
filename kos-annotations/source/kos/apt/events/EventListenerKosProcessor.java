package kos.apt.events;

import generator.apt.SimplifiedAST;
import generator.apt.SimplifiedAbstractProcessor;
import kos.api.ConfigurationLoadedEventListener;
import kos.apt.ClassGenerator;
import kos.apt.spi.CustomInjectorProcessor;
import kos.apt.spi.SPIGenerator;
import kos.events.Listener;
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
public class EventListenerKosProcessor extends SimplifiedAbstractProcessor {

    private final CustomInjectorProcessor injectorProcessor = new CustomInjectorProcessor();

    private ClassGenerator classGenerator;
    private String spiLocation;
    private SPIGenerator spiGenerator;

    public EventListenerKosProcessor() {
        super(
            emptyList(),
            singletonList(Listener.class),
            emptyList()
        );
        this.spiLocation = "META-INF/services/" + ConfigurationLoadedEventListener.class.getCanonicalName();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        classGenerator = new ClassGenerator("template-event-listener-java.mustache", processingEnv);
        spiGenerator = new SPIGenerator(processingEnv, resourceLocator, spiLocation);
        injectorProcessor.init(processingEnv);
    }

    @Override
    protected void process(Collection<SimplifiedAST.Type> types) {
        try {
            val eventListenerTypes = convert(types, EventListenerType::from);
            classGenerator.generateClasses(eventListenerTypes);
            injectorProcessor.process(types);
            generateSpiDescriptors(eventListenerTypes);
        } catch (Throwable cause) {
            throw new RuntimeException(cause);
        }

    }

    private void generateSpiDescriptors(List<EventListenerType> eventListenerTypes) throws IOException {
        spiGenerator.flushSPIClasses();
        spiGenerator.memorizeSPIFor(eventListenerTypes);
        spiGenerator.generateSPIFiles();
    }
}
