# 2.1 Internal Deployment Events
Kos triggers a few events during the application bootstrap. It might be particularly
useful to customize how Vert.x will behave internally, allowing developers to deploy
custom verticles or even changing Kos' default configuration parameters.

## Web Server events
By exposing an implementation of the interface `kos.api.WebServerEventListener`,
your code will be notified before the web server is instantiated and its routes
are deployed. This might be useful to deploy custom routes, or tweaking the web
server nobs to your needs.

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

## Application events
By exposing an implementation of the interface `kos.api.Application`,
developers will be notified whenever the whole application is about to be
started. It is useful to deploy custom Verticles that requires complex
logic to be instantiated.

=== "Kotlin"
    ```kotlin
    @Exposed
    class MyCustomAppBootstrap: Application {
    
        fun configure( deploymentContext: DeploymentContext ) {
            TODO("Implement me!")
        }
    }
    ```

=== "Java"
    ```java
    @Exposed
    class MyCustomAppBootstrap implements Application {
    
        public void configure( DeploymentContext deploymentContext ) {
            throw new UnsupportedOperationException("Implement me!");
        }
    }
    ```
