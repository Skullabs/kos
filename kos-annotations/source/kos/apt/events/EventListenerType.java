package kos.apt.events;

import generator.apt.SimplifiedAST;
import io.vertx.core.Future;
import kos.apt.TypeUtils;
import kos.apt.spi.SpiClass;
import kos.core.Lang;
import kos.events.Listener;
import kos.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.List;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
class EventListenerType implements SpiClass {

    final String jdkGeneratedAnnotation;
    final String eventListenerClassName;
    final String targetClassSimpleName;
    final String packageName;

    final List<EventListenerMethod> listenerMethods;

    static EventListenerType from(SimplifiedAST.Type type)
    {
        val methods = Lang.filter(
            Lang.convertIndex(type.getMethods(), EventListenerMethod::from),
            Objects::nonNull
        );

        return new EventListenerType(
            type.getJdkGeneratedAnnotation(),
            type.getSimpleName() + "EventListenerConfiguration",
            type.getSimpleName(),
            type.getPackageName(),
            methods
        );
    }

    @Override
    public String getClassCanonicalName() {
        return getPackageName() + "." + getEventListenerClassName();
    }
}

@Getter
@RequiredArgsConstructor
class EventListenerMethod {

    private static final String VERTX_FUTURE_OF_VOID = Future.class.getCanonicalName() + "<java.lang.Void>";

    final String topicAddressName;
    final String messageType;
    final boolean requiresValidation;
    final boolean isAsync;
    final String targetMethodName;
    final int uniqueIdentifier;

    public static EventListenerMethod from(int counter, SimplifiedAST.Method targetMethod)
    {
        val topicAddressName = extractTopicAddressNameFrom(targetMethod);
        if (topicAddressName == null) return null;

        if (targetMethod.getParameters().isEmpty()) {
            throw new UnsupportedOperationException("Missing event type in listener method.");
        }

        if (targetMethod.getParameters().size() != 1) {
            throw new UnsupportedOperationException("Listener methods cannot have more than one parameter.");
        }

        if (!targetMethod.isVoidMethod() && !targetMethod.getType().equals(VERTX_FUTURE_OF_VOID)) {
            throw new UnsupportedOperationException("Listener methods should return void or " + VERTX_FUTURE_OF_VOID + ".");
        }

        val parameter = targetMethod.getParameters().get(0);

        return new EventListenerMethod(
            topicAddressName,
            parameter.getType(),
            parameter.getAnnotation(Valid.class) != null,
            !targetMethod.isVoidMethod(),
            targetMethod.getName(),
            counter
        );
    }

    private static String extractTopicAddressNameFrom(SimplifiedAST.Method targetMethod) {
        return Lang
            .first(targetMethod.getAnnotations(), ann -> ann.getType().equals(Listener.class.getCanonicalName()))
            .map(SimplifiedAST.Annotation::getValue)
            .map(TypeUtils::annotationValueAsString)
            .orElse(null);
    }
}