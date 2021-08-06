package kos.apt.validation;

import generator.apt.SimplifiedAST;
import generator.apt.SimplifiedAbstractProcessor;
import kos.api.Validation;
import kos.apt.ClassGenerator;
import kos.apt.spi.CustomInjectorProcessor;
import kos.apt.spi.SPIGenerator;
import kos.validation.Validates;
import lombok.val;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static kos.core.Lang.convert;

@SupportedAnnotationTypes( { "kos.validation.*" } )
public class ValidatorProcessor extends SimplifiedAbstractProcessor {

    private final CustomInjectorProcessor injectorProcessor = new CustomInjectorProcessor();

    private ClassGenerator classGenerator;
    private String spiLocation;
    private SPIGenerator spiGenerator;

    public ValidatorProcessor(){
        super(
            emptyList(),
            singletonList(Validates.class),
            emptyList()
        );
        this.spiLocation = "META-INF/services/" + Validation.class.getCanonicalName();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        classGenerator = new ClassGenerator("template-validator-java.mustache", processingEnv);
        spiGenerator = new SPIGenerator(processingEnv, resourceLocator, spiLocation);
        injectorProcessor.init(processingEnv);
    }

    @Override
    protected void process(Collection<SimplifiedAST.Type> types) {
        spiGenerator.flushSPIClasses();

        try {
            val allValidatorTypes = convert(types, ValidatorType::from);
            for (val validatorTypes : allValidatorTypes) {
                generate(validatorTypes);
            }
            injectorProcessor.process(types);
            spiGenerator.generateSPIFiles();
        } catch (Throwable cause) {
            throw new RuntimeException(cause);
        }
    }

    private void generate(List<ValidatorType> validatorTypes) throws IOException {
        classGenerator.generateClasses(validatorTypes);
        spiGenerator.memorizeSPIFor(validatorTypes);
    }
}
