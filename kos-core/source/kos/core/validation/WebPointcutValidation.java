package kos.core.validation;

import injector.Singleton;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import kos.api.KosContext;
import kos.core.Request;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Singleton
@RequiredArgsConstructor
public class WebPointcutValidation {
    
    final KosContext kosContext;

    public <T> Handler<RoutingContext> wrapForBody(Class<T> type, String name, Handler<RoutingContext> handler) {
        return event -> {
            val parameter = Request.readBody(kosContext, event, name, type);
            val future = kosContext.getDefaultValidation().validate(type, parameter);
            future.onSuccess(new AttachAndForward<>("body:" + name, event, handler));
        };
    }

    public <T> T unwrapForBody(RoutingContext context, String name) {
        return context.remove("body:" + name);
    }

    @RequiredArgsConstructor
    static class AttachAndForward<T> implements Handler<T> {

        final String identifier;
        final RoutingContext event;
        final Handler<RoutingContext> next;

        @Override
        public void handle(T parameter) {
            event.put(identifier, parameter);
            next.handle(event);
        }
    }
}
