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

import generator.apt.SimplifiedAST;
import io.vertx.core.Future;
import kos.core.Lang;
import kos.rest.*;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static kos.core.Lang.convert;
import static kos.core.Lang.nonEmptySetOfString;

@UtilityClass
class TypeUtils {

    private SimplerRegexPattern
        futureWrapper = new SimplerRegexPattern(
            "(io.vertx.core.Future|" +
            "java.util.concurrent.Future|" +
            "java.util.concurrent.CompletableFuture)<(.+)>"),
        rawClass = new SimplerRegexPattern("([^<]+)<.*>")
    ;

    private Map<String, String> primitiveTypeObjects = Lang
        .mapOf( "long", "java.lang.Long" )
          .and( "int", "java.lang.Integer" )
          .and( "short", "java.lang.Short" )
          .and( "double", "java.lang.Double" )
          .and( "float", "java.lang.Float" )
          .and( "char", "java.lang.Character" )
          .and( "boolean", "java.lang.Boolean" )
            .build();

    private List<String> validParamAnnotations = asList(
        Param.class.getCanonicalName(),
        Header.class.getCanonicalName(),
        Body.class.getCanonicalName(),
        Context.class.getCanonicalName()
    );

    private List<String> validRouteAnnotations = asList(
        GET.class.getCanonicalName(),
        POST.class.getCanonicalName(),
        PUT.class.getCanonicalName(),
        PATCH.class.getCanonicalName(),
        DELETE.class.getCanonicalName()
    );

    boolean isParamAnn( SimplifiedAST.Annotation element ) {
        return validParamAnnotations.contains(element.getType());
    }

    boolean isRouteAnn( SimplifiedAST.Annotation element ) {
        return validRouteAnnotations.contains(element.getType());
    }

    String typeSimpleName( String canonicalName ) {
        val tokens = canonicalName.split("\\.");
        return tokens[tokens.length-1];
    }

    List<String> asAbsolutePath(Iterable<String> rootPaths, String value) {
        val buffer = new ArrayList<String>();
        val parsedValues = parseMultiParamValue(value);

        val nonEmptyRootPaths = nonEmptySetOfString(rootPaths);
        if (nonEmptyRootPaths.isEmpty())
            nonEmptyRootPaths.add("/");

        for ( val root : nonEmptyRootPaths )
            for ( val parsed : parsedValues ) {
                val grouped = nonEmptySetOfString( asList( root, parsed ) );
                if ( !grouped.isEmpty() ) {
                    val path = String.join("/", grouped)
                        .replaceAll("//+", "/")
                        .replaceFirst("^(/.+)/$", "$1");
                    buffer.add(path);
                } else
                    buffer.add("/");
            }
        return buffer;
    }

    Iterable<String> parseMultiParamValue( String value ) {
        val found = nonEmptySetOfString(convert(
            asList(value.replaceAll("[{}]","").split(",")),
            entry -> entry.replaceAll("[\" ]","")
        ));

        if ( found.isEmpty() )
            return singletonList("");
        return found;
    }

    String getBoxedType( String type ) {
        return primitiveTypeObjects.getOrDefault(type, type);
    }

    String unwrapFutureGenericType(String responseType) {
        return futureWrapper.matchedGroup(responseType, 2).orElse(responseType);
    }

    boolean isVertxFuture( String type ){
        return type.startsWith(Future.class.getCanonicalName());
    }

    Optional<String> rawType(String wrapped) {
        return rawClass.matchedGroup(wrapped, 1);
    }

    static class SimplerRegexPattern{
        final Pattern pattern;

        SimplerRegexPattern(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        Optional<String> matchedGroup(String target, int group) {
            val matcher = pattern.matcher(target);
            if (matcher.matches())
                return Optional.of(matcher.group(group));
            return Optional.empty();
        }
    }
}
