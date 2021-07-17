package kos.core.validation;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import kos.api.MutableKosContext;
import kos.api.PayloadSerializationStrategy;
import kos.api.Serializer;
import kos.api.Validation;
import kos.core.exception.KosException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.vertx.core.Future.succeededFuture;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebPointcutValidationTest {

    MutableKosContext kosContext = new MutableKosContext();
    WebPointcutValidation webPointcutValidation = new WebPointcutValidation(kosContext);

    @Mock Validation validation;
    @Mock Handler<RoutingContext> handler;
    @Mock RoutingContext routingContext;
    @Mock HttpServerRequest request;

    @BeforeEach void setupValidationMocks(){
        doAnswer(a -> succeededFuture(a.getArgument(1)))
            .when(validation).validate(any(), any());

        kosContext.setDefaultValidation(validation);
    }

    @DisplayName("Request Payload (body) Validation")
    @Nested class RequestPayloadValidation {

        @Mock PayloadSerializationStrategy serializationStrategy;
        @Mock Serializer serializer;
        @Mock Buffer receivedRequestPayload;
        @Mock Object deserializedObject;

        @BeforeEach
        void setupMocks() {
            doReturn(request).when(routingContext).request();
            doReturn(deserializedObject).when(serializer).deserialize(eq(receivedRequestPayload), any());
            doReturn(serializer).when(serializationStrategy).serializerFor(eq(request));
            doReturn(receivedRequestPayload).when(routingContext).getBody();

            kosContext.setPayloadSerializationStrategy(serializationStrategy);
        }

        @DisplayName("should delegate to kosContext.defaultValidation")
        @Test void wrapForBody ()
        {
            val newHandler = webPointcutValidation.wrapForBody(Object.class, "", handler);
            newHandler.handle(routingContext);

            verify(handler).handle(routingContext);
            verify(validation).validate(any(), eq(deserializedObject));
        }
    }
}