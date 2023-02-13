/*
 * Copyright 2019-2021 Skullabs Contributors (https://github.com/skullabs)
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
import kos.apt.TypeUtils;
import kos.apt.spi.SpiClass;
import kos.core.Lang;
import kos.core.exception.KosException;
import kos.rest.*;
import lombok.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static kos.core.Lang.*;

@SuppressWarnings("SameParameterValue")
@Value class Type implements SpiClass {

    String jdkGeneratedAnnotation;
    String packageName;
    String simpleName;
    String className;
    String importantSuperClassOrInterface;

    Iterable<Method> methods;

    public String getClassCanonicalName(){
        return packageName + "." + className;
    }

    static Type from(SimplifiedAST.Type type, String suffix){
        return from(type, suffix, null);
    }

    static Type from(SimplifiedAST.Type type, String suffix, String importantSuperClassOrInterface) {
        TypeReference.resetReferenceCounter();

        val rootPath = TypeUtils.parseMultiParamValue(extractRootPath(type));
        val methods = convert(
            filter(
                type.getMethods(),
                m-> filter( m.getAnnotations(), TypeUtils::isRouteAnn ).iterator().hasNext()
            ),
            m -> Method.from(rootPath, m)
        );

        methods.sort(Comparator.comparing(Method::getName));

        return new Type(
            type.getJdkGeneratedAnnotation(),
            type.getPackageName(),
            type.getSimpleName(),
            type.getSimpleName() + suffix,
            importantSuperClassOrInterface,
            methods
        );
    }

    private static String extractRootPath(SimplifiedAST.Type type) {
        return first(
            type.getAnnotations(),
            ann -> ann.getType().equals(RestApi.class.getCanonicalName())
                || ann.getType().equals(RestClient.class.getCanonicalName())
        ).map( ann ->
            first(ann.getParameters().values())
              .orElse("").toString()
        ).orElse("");
    }
}

@EqualsAndHashCode(exclude = "uniqueName")
@ToString(exclude = "uniqueName")
@Value class Method {

    String httpMethod;
    Iterable<String> httpPath;
    String name;
    Boolean containsResponseType;
    Boolean containsRequestPayload;
    String variableWithRequestPayload;
    String responseType;
    String unwrappedResponseType;
    SimplifiedAST.WrappedDataIterable parameters;
    TypeReference typeReference;
    List<ReplaceablePathParam> replaceablePathParams;
    List<MethodDefinedHeaders> definedHeaders;
    boolean containsDefinedHeaders;

    @Getter(lazy = true)
    String uniqueName = computeUniqueName();

    private String computeUniqueName() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(toString().getBytes(StandardCharsets.UTF_8));
            String hashCode = bytesToHex(encodedhash);
            return Character.toUpperCase(getName().charAt(0)) + getName().substring(1) + "$" + hashCode;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    static Method from(Iterable<String> rootPath, SimplifiedAST.Method method) {
        val methodAnn = first( method.getAnnotations(), TypeUtils::isRouteAnn ).get();
        val path = first(methodAnn.getParameters().values()).orElse("").toString();
        val absolutePath = TypeUtils.asAbsolutePath( rootPath, path );
        val containsResponseType = !"void".equals(method.getType());
        val responseType = "void".equals(method.getType()) ? null : TypeUtils.getBoxedType(method.getType());
        val unwrappedResponseType = responseType == null ? null : TypeUtils.unwrapFutureGenericType(responseType);
        val variableWithRequestPayload = retrieveRequestPayloadVariable(method);
        val definedHeaders = MethodDefinedHeaders.extractAnnotatedHeadersFrom(method);

        return new Method(
            TypeUtils.typeSimpleName(methodAnn.getType()),
            absolutePath,
            method.getName(),
            containsResponseType,
            variableWithRequestPayload != null,
            variableWithRequestPayload,
            responseType,
            unwrappedResponseType,
            MethodParam.from(method),
            TypeReference.from(method),
            ReplaceablePathParam.from(method),
            definedHeaders,
            !definedHeaders.isEmpty()
        );
    }

    private static String retrieveRequestPayloadVariable(SimplifiedAST.Method method){
        for (val parameter : method.getParameters()) {
            for (val annotation : parameter.getAnnotations()) {
                if (annotation.getType().equals(Body.class.getCanonicalName()))
                    return parameter.getName();
            }
        }
        return null;
    }
}

@Value class MethodParam {

    String annotation;
    String name;
    String variableName;
    String type;
    boolean shouldBeValidated;

    static SimplifiedAST.WrappedDataIterable from( SimplifiedAST.Method method ) {
        val params = convert(method.getParameters(), p -> {
            SimplifiedAST.Annotation annotation = extractMainAnnotation( method, p );
            String name = first(annotation.getParameters().values()).orElse( p.getName() ).toString();
            boolean shouldBeValidated = shouldBeValidated(p);

            if (shouldBeValidated) {
                ensureIsATypeOfParameterThatCanBeValidated(method, p, annotation);
                ensureIsNotAJavaNativeType(method, p, annotation);
            }

            return new MethodParam(
                TypeUtils.typeSimpleName(annotation.getType()),
                name.replace("\"",""),
                p.getName(),
                p.getType(),
                shouldBeValidated
            );
        });

        return new SimplifiedAST.WrappedDataIterable(params);
    }

    private static SimplifiedAST.Annotation extractMainAnnotation(
            SimplifiedAST.Method method,
            SimplifiedAST.Element parameter
    ) {
        return first( parameter.getAnnotations(), TypeUtils::isParamAnn )
                .orElseThrow( () -> new IllegalArgumentException(
                    "Missing annotation in method parameter." +
                        " Method: " + method.getName() +
                        " Parameter: " + parameter.getName() ));
    }

    private static boolean shouldBeValidated(
            SimplifiedAST.Element parameter
    ) {
        return first(parameter.getAnnotations(), ann -> ann.getType().equals(TypeUtils.validationAnnotation))
            .isPresent();
    }

    private static void ensureIsATypeOfParameterThatCanBeValidated(
            SimplifiedAST.Method method,
            SimplifiedAST.Element parameter,
            SimplifiedAST.Annotation annotation
    ) {
        val annType = annotation.getType();
        if (!annType.equals(TypeUtils.bodyAnnotation)) {
            throw new UnsupportedOperationException(
                "Cannot enforce validation on parameter annotated with @" + annType + ". " +
                "Only parameters annotated with @" + TypeUtils.bodyAnnotation + "." +
                " Method: " + method.getName() +
                " Parameter: " + parameter.getName()
            );
        }
    }

    private static void ensureIsNotAJavaNativeType(
            SimplifiedAST.Method method,
            SimplifiedAST.Element parameter,
            SimplifiedAST.Annotation annotation
    ){
        val paramType = annotation.getType();
        if (TypeUtils.isJavaBasicType(paramType)) {
            throw new UnsupportedOperationException(
                "Cannot enforce validation on parameter which type is" + paramType + ". " +
                        "Ensure the type of the object you're enforcing validation is not a basic Java type (e.g. java.lang.String)." +
                        " Method: " + method.getName() +
                        " Parameter: " + parameter.getName()
            );
        }
    }
}

@Value class ReplaceablePathParam {
    String pathParam;
    String pathVariable;

    static List<ReplaceablePathParam> from(SimplifiedAST.Method method) {
        return method
            .getParameters().stream()
            .filter(e -> matches(e.getAnnotations(), ReplaceablePathParam::isReplaceableParam))
            .map(ReplaceablePathParam::toReplaceableParam)
            .collect(Collectors.toList());
    }

    private static ReplaceablePathParam toReplaceableParam(SimplifiedAST.Element param) {
        var pathParameterPlaceholder = "";
        for (val annotation : param.getAnnotations()) {
            if (isReplaceableParam(annotation)){
                pathParameterPlaceholder = first(annotation.getParameters().values())
                    .orElse("").toString()
                    .replace(":", "")
                    .replace("\"", "")
                ;
                break;
            }
        }

        return new ReplaceablePathParam(pathParameterPlaceholder, param.getName());
    }

    private static boolean isReplaceableParam(SimplifiedAST.Annotation annotation) {
        return annotation.getType().equals(Param.class.getCanonicalName());
    }
}

@Value class TypeReference {

    private static AtomicInteger REFERENCE_COUNTER = new AtomicInteger();

    String name = "TYPE_REFERENCE_" + REFERENCE_COUNTER.getAndIncrement();
    String mappedType;

    static TypeReference from(SimplifiedAST.Method method) {
        val type = method.getType();
        if (TypeUtils.isVertxFuture(type)) {
            val wrappedType = TypeUtils.unwrapFutureGenericType(type);
            if (wrappedType.equals(type))
                throw new IllegalStateException("Could not parse the wrapped Future instance: " + wrappedType);
            if (isATypeThatRequiresTypeReference(wrappedType))
                return new TypeReference(wrappedType);
        }
        return null;
    }

    private static boolean isATypeThatRequiresTypeReference(String type){
        try {
            val rawType = TypeUtils.rawType(type).orElse(type);
            val actualClass = Lang.classFor(rawType);
            return Map.class.isAssignableFrom(actualClass)
                    || Collection.class.isAssignableFrom(actualClass);
        } catch (KosException cause) {
            return false;
        }
    }

    static void resetReferenceCounter(){
        REFERENCE_COUNTER.set(0);
    }
}

@Value class MethodDefinedHeaders
{
    String headerName;
    String variableWithHeaderValue;

    static List<MethodDefinedHeaders> extractAnnotatedHeadersFrom(SimplifiedAST.Method method) {
        val headers = new ArrayList<MethodDefinedHeaders>();

        parameter: for (val parameter : method.getParameters()) {
            for (val annotation : parameter.getAnnotations()) {
                if (annotation.getType().equals(Header.class.getCanonicalName())) {
                    headers.add(createHeaderFrom(parameter, annotation));
                    continue parameter;
                }
            }
        }

        return headers;
    }

    private static MethodDefinedHeaders createHeaderFrom(SimplifiedAST.Element parameter, SimplifiedAST.Annotation annotation) {
        return new MethodDefinedHeaders(
            first(annotation.getParameters().values()).orElse(parameter.getName())
                    .toString().replace("\"", ""),
            parameter.getName()
        );
    }
}