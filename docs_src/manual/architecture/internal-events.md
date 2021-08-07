# Internal Events
Kos triggers a few events during the application bootstrap. It might be particularly
useful to customize how Vert.x will behave internally, allowing developers to deploy
custom verticles or even changing Kos' default configuration parameters.

## Web Server events
By exposing an implementation of the interface `kos.api.WebServerEventListener`,
your code will be notified before the web server is instantiated and its routes
are deployed. This might be useful to deploy custom routes, or tweaking the web
server nobs to your needs.

> *Note*: this event won't be triggered if the default WebServer verticle is turned off.

=== "Kotlin"
    ```kotlin
    @Exposed
    class MyWebListener: WebServerEventListener {
    
        fun on( event: BeforeDeployWebServerEvent ) {
            TODO("Implement me!")
        }
    }
    ```

=== "Java"
    ```java
    @Exposed
    class MyWebListener implements WebServerEventListener {
        public void on( BeforeDeployWebServerEvent event ) {
            throw new UnsupportedOperationException("Implement me!");
        }
    }
    ```

## Configuration Loaded events
By exposing an implementation of the interface `kos.api.ConfigurationLoadedListener`,
developers will be notified whenever the whole application is about to be
started. It allow one to read the whole configuration of the software.

=== "Kotlin"
    ```kotlin
    @Exposed
    class MyCustomAppBootstrap: ConfigurationLoadedEventListener {
    
        fun on( event: ConfigurationLoadedEvent ) {
            TODO("Implement me!")
        }
    }
    ```

=== "Java"
    ```java
    @Exposed
    class MyCustomAppBootstrap implements ConfigurationLoadedEventListener {
    
        public void on( ConfigurationLoadedEventListener event ) {
            throw new UnsupportedOperationException("Implement me!");
        }
    }
    ```
