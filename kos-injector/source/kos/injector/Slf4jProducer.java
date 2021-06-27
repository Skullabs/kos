package kos.injector;

import injector.Producer;
import injector.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@SuppressWarnings("all")
public class Slf4jProducer {

    @Producer
    public Logger produceLogger(Class targetClass) {
        return LoggerFactory.getLogger(targetClass);
    }
}
