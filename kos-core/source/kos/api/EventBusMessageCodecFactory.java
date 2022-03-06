package kos.api;

import io.vertx.core.eventbus.MessageCodec;

public interface EventBusMessageCodecFactory {

    <T> MessageCodec<T, T> constructCodecFor(Class<T> targetClass);
}

