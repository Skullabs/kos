# Logging Configuration
Kos uses [SLF4J](http://www.slf4j.org) as main logging implementation. Vert.x
internal logging is also automatically configured to use SLF4J as its default
logging mechanism.

## Injecting the logger
Kos allows developers to inject SLF4J Loggers into their components.

=== "Kotlin"
    ```kotlin
    import org.slf4j.Logger;
    
    @Service
    class MyComponent(val logger: Logger) {
        
    }
    ```
=== "Java"
    ```java
    import org.slf4j.Logger;
    
    @Service
    class MyComponent {
    
        Logger logger;
        
        MyComponent(Logger logger) {
            this.logger = logger;
        }
    }
    ```
