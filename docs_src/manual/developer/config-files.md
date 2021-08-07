# Reading Configuration File
Kos will use Vert.x's core api to read Yaml configuration files available in the class path.
Once the configuration is read, you will have a `JsonObject` which you can interact with
and read the desired configuration property.

By default, Kos will for a file called `application.yml` in the classpath. In case
more than one is found, all `application.yml` found in the classpath will be merged
before being used.

## Reading the configuration object
Reading the configuration file and transforming it into an object that can be accessed
globally in the application is a common pattern nowadays. Kos provides a different approach
to tackle this problem: event-driven configuration.

All you will need do is to expose an implementation of the `ConfigurationLoadedListener.Event` interface.

!!! note
    As your class is annotated with `@Exposed` annotation, you can inject other components.
    Check the [Injector](https://skullabs.github.io/injector) for more details.

=== "Kotlin"
```kotlin
@Exposed
class MyAppConfigPlugin: ConfigurationLoadedEventListener {

    fun on(event: ConfigurationLoadedEvent) {
        val vertxConf = event.applicationConfig
        val remoteUrl = URL(vertxConf.getString("myapp.remote.url"))
        // do something with the `remoteUrl`
    }
}
```
```java
@Exposed
class MyAppConfigPlugin implements ConfigurationLoadedEventListener {

    @Override
    public void on(ConfigurationLoadedEvent event) {
        try {
            JsonObject vertxConf = event.getApplicationConfig();
            URL remoteUrl = new URL(vertxConf.getString("myapp.remote.url"));
            // do something with the `remoteUrl`
        } catch (MalformedURLException cause) {
            cause.printStackTrace();
        }
    }
}

```