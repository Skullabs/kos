package kos.core.events;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;
import kos.api.Serializer;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class JsonServiceBusCodec<T> implements MessageCodec<T, T> {

    private final Class<T> targetClass;

    @Override
    public void encodeToWire(Buffer buffer, T t) {
        val encoded = Json.encode(t);
        buffer.appendString(encoded);
    }

    @Override
    public T decodeFromWire(int i, Buffer buffer) {
        return Json.decodeValue(buffer, targetClass);
    }

    @Override
    public T transform(T t) {
        return t;
    }

    @Override
    public String name() {
        return "JSON::"+ targetClass.getCanonicalName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
