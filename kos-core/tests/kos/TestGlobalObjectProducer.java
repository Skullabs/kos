package kos;

import injector.Producer;
import injector.Singleton;
import io.vertx.core.Vertx;
import kos.api.KosContext;
import kos.api.MutableKosContext;

@Singleton
public class TestGlobalObjectProducer {

    private Vertx vertx = Vertx.vertx();
    private KosContext kosContext = new MutableKosContext().setDefaultVertx(vertx);

    @Producer
    public KosContext kosContext() {
        return kosContext;
    }
}
