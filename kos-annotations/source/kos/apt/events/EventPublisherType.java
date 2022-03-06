package kos.apt.events;

import generator.apt.SimplifiedAST;
import io.vertx.core.Future;
import kos.apt.TypeUtils;
import kos.apt.spi.SpiClass;
import kos.core.Lang;
import kos.events.Publisher;
import kos.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.List;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
class EventPublisherType implements SpiClass {

    final String jdkGeneratedAnnotation;
    final String eventPublisherClassName;
    final String eventPublisherInterfaceName;
    final String packageName;

    final List<EventPublisherMethod> methods;

    static EventPublisherType from(SimplifiedAST.Type type)
    {
        if (!type.isInterface()) {
            throw new UnsupportedOperationException("Cannot create Publisher for concrete classes.");
        }

        val methods = Lang.filter(
            Lang.convertIndex(type.getMethods(), EventPublisherMethod::from),
            Objects::nonNull
        );

        return new EventPublisherType(
                type.getJdkGeneratedAnnotation(),
                type.getSimpleName() + "Impl",
                type.getSimpleName(),
                type.getPackageName(),
                methods
        );
    }

    @Override
    public String getClassCanonicalName() {
        return getPackageName() + "." + this.getEventPublisherClassName();
    }
}

@Getter
@RequiredArgsConstructor
class EventPublisherMethod {

    private static final String VERTX_FUTURE_OF_VOID = Future.class.getCanonicalName() + "<java.lang.Void>";

    final String topicAddressName;
    final String messageType;
    final boolean requiresValidation;
    final boolean isAsync;
    final String targetMethodName;
    final int uniqueIdentifier;

    public static EventPublisherMethod from(int counter, SimplifiedAST.Method targetMethod)
    {
        if (targetMethod.isConstructor()) return null;

        val topicAddressName = extractTopicAddressNameFrom(targetMethod);
        if (topicAddressName == null) return null;

        if (targetMethod.getParameters().isEmpty()) {
            throw new UnsupportedOperationException("Missing event type in publisher method.");
        }

        if (targetMethod.getParameters().size() != 1) {
            throw new UnsupportedOperationException("Publisher methods cannot have more than one parameter.");
        }

        if (!targetMethod.getType().equals(VERTX_FUTURE_OF_VOID)) {
            throw new UnsupportedOperationException("Publisher methods should return " + VERTX_FUTURE_OF_VOID + ".");
        }

        val parameter = targetMethod.getParameters().get(0);
        val messageType = parameter.getType();

        val erasedMessageType = TypeUtils.rawType(messageType).orElse(messageType);
        if (erasedMessageType != messageType) {
            throw new UnsupportedOperationException("Publisher does not support types with generics");
        }

        return new EventPublisherMethod(
                topicAddressName,
                messageType,
                parameter.getAnnotation(Valid.class) != null,
                !targetMethod.isVoidMethod(),
                targetMethod.getName(),
                counter
        );
    }

    private static String extractTopicAddressNameFrom(SimplifiedAST.Method targetMethod) {
        return Lang
                .first(targetMethod.getAnnotations(), ann -> ann.getType().equals(Publisher.class.getCanonicalName()))
                .map(SimplifiedAST.Annotation::getValue)
                .map(TypeUtils::annotationValueAsString)
                .orElse(null);
    }
}
