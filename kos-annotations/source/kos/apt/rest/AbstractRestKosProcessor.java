/*
 * Copyright 2019 Skullabs Contributors (https://github.com/skullabs)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kos.apt.rest;

import generator.apt.SimplifiedAST;
import generator.apt.SimplifiedAbstractProcessor;
import kos.apt.spi.SPIGenerator;
import kos.core.exception.KosException;
import kos.rest.*;
import lombok.val;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

abstract class AbstractRestKosProcessor extends SimplifiedAbstractProcessor {

    private final String spiLocation;
    protected SPIGenerator spiGenerator;

    public AbstractRestKosProcessor(Class<?> spiType){
        super(
            Collections.emptyList(),
            Arrays.asList(GET.class, POST.class, PUT.class, PATCH.class, DELETE.class),
            Collections.emptyList()
        );
        this.spiLocation = "META-INF/services/" + spiType.getCanonicalName();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        spiGenerator = new SPIGenerator(processingEnv, resourceLocator, spiLocation);
    }

    @Override
    protected void process(Collection<SimplifiedAST.Type> types) {
        spiGenerator.flushSPIClasses();

        val filteredTypes = types.stream()
            .filter(this::matchesExpectedAnnotation)
            .collect(Collectors.toList());

        try {
            processClasses(filteredTypes);
            spiGenerator.generateSPIFiles();
        } catch (Exception e) {
            throw new KosException(e);
        }
    }

    protected abstract void processClasses(List<SimplifiedAST.Type> routeMethods) throws Exception;

    protected abstract boolean matchesExpectedAnnotation(SimplifiedAST.Type type);
}
