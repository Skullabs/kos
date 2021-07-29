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
import injector.apt.InjectorProcessor;
import kos.apt.ClassGenerator;
import kos.core.Lang;
import kos.api.WebServerEventListener;
import kos.rest.RestClient;
import lombok.val;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static kos.core.Lang.filter;

@SupportedAnnotationTypes( { "kos.rest.*" } )
public class RestApiProcessor extends AbstractRestKosProcessor {

    private ClassGenerator routeClassGenerator;
    private CustomInjectorProcessor injectorProcessor = new CustomInjectorProcessor();

    public RestApiProcessor() {
        super(WebServerEventListener.class);
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        routeClassGenerator = new ClassGenerator("template-route-java.mustache", processingEnv);
        injectorProcessor.init(processingEnv);
    }

    protected void processClasses(List<SimplifiedAST.Type> types) throws Exception {
        val routes = types.stream()
            .map((SimplifiedAST.Type type) -> Type.from(type, "RoutingContextHandler"))
            .collect(Collectors.toList());

        routeClassGenerator.generateClasses(routes);
        spiGenerator.memorizeSPIFor(routes);

        val nonAbstractTypes = filter(types, t -> !t.isAbstract() && !t.isInterface());
        injectorProcessor.process(nonAbstractTypes);
    }

    @Override
    protected boolean matchesExpectedAnnotation(SimplifiedAST.Type type) {
        return Lang.first(
            type.getAnnotations(),
            ann -> ann.getType().equals(RestClient.class.getCanonicalName())
        ).isEmpty();
    }

    private static class CustomInjectorProcessor extends InjectorProcessor {

        @Override
        public void process(Collection<SimplifiedAST.Type> types) {
            super.process(types);
        }
    }
}
