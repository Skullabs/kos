package kos.events;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Publisher {

    /**
     * The {@link io.vertx.core.eventbus.EventBus} topic address.
     */
    String value();
}