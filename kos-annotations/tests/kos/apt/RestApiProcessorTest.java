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

import generator.apt.SimplifiedAPTRunner;
import injector.apt.InjectorProcessor;
import kos.api.WebServerEventListener;
import kos.core.*;
import kos.rest.sample.*;
import lombok.*;
import org.junit.jupiter.api.*;

import javax.annotation.processing.*;
import javax.tools.*;

import java.io.*;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RestApiProcessorTest {

    String
        generatedClassName = SimpleApi.class.getCanonicalName() + "RoutingContextHandler",
        generatedClassName1 = ApiWithNoPath.class.getCanonicalName() + "RoutingContextHandler"
    ;

    Processor processor = new RestApiProcessor();

    @DisplayName("SHOULD generate classes as expected WHEN find classes/methods properly annotated")
    @Test void generateClasses() throws IOException
    {
        val source = APT.asSource( APT.testFile(SimpleApi.class) );
        runAPT( processor, source );

        val generatedClass = APT.readFileAsString(APT.outputGeneratedClass( generatedClassName ));

        val expectedClass = APT.testResourceAsString("expected-generated-class.txt");
        assertEquals(expectedClass, generatedClass);
    }

    @DisplayName("SHOULD generate classes WHEN find method annotated but no @Path is defined")
    @Test void generateClasses1() throws IOException
    {
        val source = APT.asSource( APT.testFile(ApiWithNoPath.class) );
        runAPT( processor, source );
        runAPT( new InjectorProcessor(), source );

        val generatedClass = APT.readFileAsString(APT.outputGeneratedClass( generatedClassName1 ));

        val expectedClass = APT.testResourceAsString("expected-generated-class2.txt");
        assertEquals(expectedClass, generatedClass);
    }

    @DisplayName("SHOULD expose the generated class as 'SPI' WHEN find classes/methods properly annotated")
    @Test void generateClasses2() throws IOException
    {
        val source = APT.asSource( APT.testFile(SimpleApi.class) );
        runAPT( processor, source );

        val spiFileLocation = APT.outputGeneratedFile(
                "META-INF/services/" + WebServerEventListener.class.getCanonicalName());
        val spiFile = APT.readFileAsString( spiFileLocation );

        val expected = generatedClassName + SPIGenerator.EOL;
        assertTrue(spiFile.contains(expected));
    }

    static void runAPT(Processor processor, SimplifiedAPTRunner.LocalJavaSource source) {
        val result = APT.runner().run( processor, source );
        result.printErrorsIfAny();
        result.printErrorsIfAny( d -> {
            if ( d.getKind() == Diagnostic.Kind.ERROR )
                throw new KosException( d.toString() );
        });
    }
}
