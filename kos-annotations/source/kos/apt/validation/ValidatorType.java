package kos.apt.validation;

import generator.apt.SimplifiedAST;
import io.vertx.core.Future;
import kos.apt.TypeUtils;
import kos.apt.spi.SpiClass;
import kos.core.Lang;
import kos.validation.Validates;
import lombok.Value;
import lombok.val;

import java.util.List;
import java.util.Objects;

@Value
class ValidatorType implements SpiClass {

    String jdkGeneratedAnnotation;
    String validatorClassName;
    String targetClassSimpleName;
    String packageName;
    String validatedType;
    String validatedTypeWithTypeErasure;
    String validatorMethod;
    boolean isAsync;

    public static List<ValidatorType> from(SimplifiedAST.Type type)
    {
        return Lang.filter(
            Lang.convertIndex(type.getMethods(), (i, m) -> from(type, m, i)),
            Objects::nonNull
        );
    }

    private static ValidatorType from(SimplifiedAST.Type type, SimplifiedAST.Method targetMethod, int counter)
    {
        if (targetMethod.isConstructor()) return null;

        val methodCanonicalName = type.getCanonicalName() + "." + targetMethod.getName();
        val validatesAnn = Lang.first(targetMethod.getAnnotations(), ann -> ann.getType().equals(Validates.class.getCanonicalName()));
        if (validatesAnn == null) return null;

        if (targetMethod.getParameters().isEmpty()) {
            throw new UnsupportedOperationException("Missing event type in listener method: " + methodCanonicalName);
        }

        if (targetMethod.getParameters().size() != 1) {
            throw new UnsupportedOperationException("Listener methods cannot have more than one parameter: " + methodCanonicalName);
        }

        val parameter = targetMethod.getParameters().get(0);
        val validatedType = parameter.getType();

        val expectedFutureType = Future.class.getCanonicalName() + "<" +validatedType+ ">";
        val returnType = computeMethodReturnTypeWithPartialErasure(targetMethod);
        if (!targetMethod.isVoidMethod() && !returnType.equals(expectedFutureType)) {
            throw new UnsupportedOperationException("Listener methods should return void or " + expectedFutureType + ": " + methodCanonicalName);
        }

        val validatedTypeSimpleName = TypeUtils.typeSimpleName(validatedType);
        return new ValidatorType(
            type.getJdkGeneratedAnnotation(),
            type.getSimpleName() + "$" + validatedTypeSimpleName + "$Validation" + counter,
            type.getSimpleName(),
            type.getPackageName(),
            validatedType,
            TypeUtils.rawType(validatedType).orElse(validatedType),
            targetMethod.getName(),
            !targetMethod.isVoidMethod()
        );
    }

    private static String computeMethodReturnTypeWithPartialErasure(SimplifiedAST.Method targetMethod) {
        val type = targetMethod.getType();

        val genericType = TypeUtils.unwrapFutureGenericType(type);
        if (genericType.equals(type)) return type;

        val rawGenericType = TypeUtils.rawType(genericType).orElse(genericType);
        return Future.class.getCanonicalName() + "<"+ rawGenericType +">";
    }

    @Override
    public String getClassCanonicalName() {
        return getPackageName() + "." + getValidatorClassName();
    }
}
