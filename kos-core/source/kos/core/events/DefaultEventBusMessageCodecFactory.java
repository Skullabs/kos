package kos.core.events;

import io.vertx.core.eventbus.MessageCodec;
import kos.api.EventBusMessageCodecFactory;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
@RequiredArgsConstructor
public class DefaultEventBusMessageCodecFactory implements EventBusMessageCodecFactory {

    private final Map<Class, MessageCodec> codecs;

    public DefaultEventBusMessageCodecFactory() {
        this.codecs = new HashMap<>();
    }

    @Override
    public <T> MessageCodec<T, T> constructCodecFor(Class<T> targetClass) {
        return codecs.computeIfAbsent(targetClass, JsonServiceBusCodec::new);
    }
}
