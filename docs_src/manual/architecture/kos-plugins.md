# Kos Plugins
Kos Plugins are useful to change how any internal component will work, being it provided
by Kos itself or a Vert.x one. Plugins are the only entrypoint that allows developers to
mutate `kos.api.KosContext` (through `kos.api.MutableKosContext`).

To learn more about KosContext, check [this page](../kos-context/).

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
