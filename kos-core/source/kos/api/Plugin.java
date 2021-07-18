package kos.api;

/**
 * Allows external plugins to configures Kos. To enable your plugin,
 * make sure you have exposed it using standard JDK Service Loader. 
 */
public interface Plugin {

    default int priority(){
        return 0;
    }

    void configure(MutableKosContext kosConfiguration);
}
