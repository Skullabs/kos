# 2.2. Kos Plugins
Configuration Plugins are useful to change how any internal component
will work on Kos, being it provided by Kos itself or a Vert.x one.

## The Kos Context
Alongside the dependency injection, `kos.api.KosContext` is the backbone of the
system, contains all basic components in which Kos will interact with. Thus,
to avoid misconfiguration, there will be only one instance of this object
(managed by Kos) in the  whole application. The only way to mutate its content
is by creating a `kos.api.Plugin` implementation.

## Creating a Plugin
Creating a plugin is easy. All it's needed is exposing an implementation of
the interface `kos.api.Plugin` and you will have access to a mutable instance
of `kos.api.KosContext`.

=== "Kotlin"
    ```kotlin
    @Exposed
    class MyWebListener: Plugin {
    
        fun configure(kosContext: MutableKosContext) {
            TODO("Implement me!")
        }
    }
    ```

=== "Java"
    ```java
    @Exposed
    class MyWebListener implements Plugin {

        public void configure(MutableKosContext kosContext) {
            throw new UnsupportedOperationException("Implement me!");
        }
    }
    ```
