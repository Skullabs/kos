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

import generator.apt.*;
import kos.core.exception.KosException;
import lombok.experimental.*;
import lombok.*;

import javax.annotation.processing.Processor;
import javax.tools.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

import static java.util.Arrays.*;

@SuppressWarnings("unchecked")
@UtilityClass
public class APT {

    public SimplifiedAPTRunner runner(){
        return new SimplifiedAPTRunner( APT.createDefaultConfiguration(), ToolProvider.getSystemJavaCompiler() );
    }

    public SimplifiedAPTRunner.Config createDefaultConfiguration(){
        final SimplifiedAPTRunner.Config config = new SimplifiedAPTRunner.Config();
        config.sourceDir = asList( new File( "tests" ), new File( "tests-resources" ) );
        config.outputDir = asList( new File( "output/apt" ) );
        config.classOutputDir = asList( new File( "output/apt" ) );
        return config;
    }

    public SimplifiedAPTRunner.LocalJavaSource asSource(File file ) {
        return new SimplifiedAPTRunner.LocalJavaSource( file );
    }

    public File testFile(Class clazz ) {
        val path = clazz.getCanonicalName().replace('.', '/');
        return new File("tests/" + path + ".java");
    }

    public String testResourceAsString(String path ) {
        return readFileAsString( new File("tests-resources/" + path) );
    }

    public File outputGeneratedClass(String clazz ) {
        val path = clazz.replace('.', '/');
        return new File("output/apt/" + path + ".java");
    }

    public File outputGeneratedFile( String path ) {
        return new File("output/apt/" + path);
    }

    @SneakyThrows
    public String readFileAsString( File file ) {
        val bytes = Files.readAllBytes(file.toPath());
        return new String( bytes, StandardCharsets.UTF_8);
    }

    public static void run(Processor processor, SimplifiedAPTRunner.LocalJavaSource source) {
        val result = APT.runner().run( processor, source );
        result.printErrorsIfAny();
        result.printErrorsIfAny( d -> {
            if ( d.getKind() == Diagnostic.Kind.ERROR )
                throw new KosException( d.toString() );
        });
    }
}