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

import generator.apt.ResourceLocator;
import kos.core.exception.KosException;
import lombok.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

@RequiredArgsConstructor
public class SPIGenerator {

    static final String EOL = "\n";

    private final ProcessingEnvironment processingEnv;
    private final ResourceLocator resourceLocator;
    private final String spiLocation;
    private List<String> spiClasses;

    public void flushSPIClasses(){
        spiClasses = new ArrayList<>();
    }

    public void memorizeSPIFor( List<? extends SpiClass> spiClasses ) {
        for (val spiClass : spiClasses)
            memorizeSPIFor(spiClass);
    }

    public void memorizeSPIFor( SpiClass spiClass ) {
        spiClasses.add( spiClass.getClassCanonicalName() );
    }

    public void generateSPIFiles() throws IOException {
        val implementations = readResourceIfExists( spiLocation );
        implementations.addAll( this.spiClasses );

        try (val resource = createResource( spiLocation )) {
            for (val implementation : implementations)
                resource.write(implementation + EOL);
        }
    }

    private Set<String> readResourceIfExists( final String resourcePath ) throws IOException {
        val resourceContent = new HashSet<String>();
        val file = new File( resourceLocator.locate(resourcePath) );
        if ( file.exists() ) {
            val content = Files.readAllLines( file.toPath() );
            resourceContent.addAll( content );
        }
        return resourceContent;
    }

    private Writer createResource(final String resourcePath ) throws IOException {
        info("Generating " + resourcePath);
        val uri = resourceLocator.locate(resourcePath);
        createNeededDirectoriesTo( uri );
        val file = createFile( uri );
        return new FileWriter( file );
    }

    private void createNeededDirectoriesTo(final URI uri) {
        val dir = ( uri.isAbsolute() )
                ? new File( uri ).getParentFile()
                : new File( uri.toString() ).getParentFile();
        if ( !dir.exists() && !dir.mkdirs() )
            throw new KosException("Can't create %s", dir.getAbsolutePath());
    }

    private File createFile(final URI uri) throws IOException {
        val file = new File( uri );
        if ( !file.exists() && !file.createNewFile() )
            throw new KosException("Can't create %s", file.getAbsolutePath());
        return file;
    }

    private void info(String msg){
        processingEnv.getMessager().printMessage(Diagnostic.Kind.OTHER, msg);
    }
}
