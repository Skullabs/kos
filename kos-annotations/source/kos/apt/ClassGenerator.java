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

package kos.apt;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import lombok.RequiredArgsConstructor;
import lombok.val;

import javax.annotation.processing.ProcessingEnvironment;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class ClassGenerator {

    private final MustacheFactory mf = new DefaultMustacheFactory();
    private final String template;
    private final ProcessingEnvironment processingEnv;

    protected void generateClasses(List<? extends SpiClass> types) throws IOException {
        for ( val route : types ) {
            val filer = processingEnv.getFiler();
            val source = filer.createSourceFile( route.getClassCanonicalName() );
            try ( val writer = source.openWriter() ) {
                val mustache = mf.compile(template);
                mustache.execute( writer, route );
            }
        }
    }
}
