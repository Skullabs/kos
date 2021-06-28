# Dependency Injection
Kos uses Dependency Injection as a way to allow developers to orthogonally customize
applications without changing their codebase. This allowed Kos to generate source code,
reducing the amount of code required by developers to write down everyday routines
like Rest APIs and Web Clients.

## Implementation Loaders
Internally, Kos refers to Dependency Injection mechanisms as _Implementation Loaders_.
They are responsible to perform two basic tasks the Kos needs all the time:

1. Load an object implementing a given Interface - or extending a given class
2. Load all objects implementing a given Interface - or extending a given class

The next topics will cover all DI mechanism provided out-of-box with Kos.

### JDK Service Provider
Although it is not considered a proper Dependency Injection implementation, the
[ServiceLoader](https://docs.oracle.com/javase/9/docs/api/java/util/ServiceLoader.html)
implementation that is bundled with JDK is versatile enough to meet the two above mentioned
requirements. If you want to keep you footprint small, Service Provider might be a good
solution. It is the default Implementation Loader mechanism.

To provide an implementation of a given Kos service all you have to do is implement a given
interface (or extend a given class) and create a Service Provider meta information in the
`META-INF/services` folder. For more details on designing a service provide please read
the ServiceLoader documentation available
[here](https://docs.oracle.com/javase/9/docs/api/java/util/ServiceLoader.html#developing-service-providers).

### Injector

!!! info
    For more details on how Injector works, please proceed to its
    [documentation](https://skullabs.github.io/injector/).

Injector is lightweight and zero-overhead dependency injection library for JVM developers. It
was carefully designed to make no-use of reflection by having all required meta-information
computed at compile time. At runtime it performs only the necessary tasks required to instantiate
classes and have its dependencies injected. The result is a blistering fast Dependency Injection
implementation that has less than 7kb of footprint.

To provide an implementation of a given Kos service all you have to do is implement a given
interface (or extend a given class) and annotate with `injector.ExposedAs` annotation. Also,
to make ordinary classes able to be managed by Injector you should annotated them with 
either `injector.Singleton` or `injector.New`.

Below you can find a _Custom Exception Handler_ implementation.
```java
import injector.ExposedAs;
import kos.api.ExceptionHandler;
import kos.api.Response;

@ExposedAs(ExceptionHandler.class)
public class CustomExceptionHandler implements ExceptionHandler {

    public Response handle( RoutingContext request, Throwable cause ){
        if (cause instanceof IllegalArgumentException)
            return Response.BAD_REQUEST;
        return Response.of(cause.getMessage()).statusCode(500);
    }
}
```

To use Injector as default Implementation Loader you need to import `kos-injector` module
on your project.

=== "Gradle (kts)"
    ```kotlin
    implementation("io.skullabs.kos:kos-injector")
    ```
=== "Maven (pom.kts)"
    ```kotlin
    compile("io.skullabs.kos:kos-injector")
    ```
=== "Maven (pom.xml)"
    ```xml 
    <dependency>
        <groupId>io.skullabs.kos</groupId>
        <artifactId>kos-injector</artifactId>
    </dependency>
    ```

### Guice

!!! info
    Although we have plans to support Guice in the near future, we hasn't created a module
    to officially support it yet. Please send us a _thumbs up_ [here](https://github.com/Skullabs/kos/issues/1)
    so we know in what to put more effort for the releases.

### Custom Dependency Injection Mechanism
As mentioned before Kos dependency injection mechanism is quite simple and doesn't
enforce you to stick with a single implementation. To provide your own Implementaion Loader
you need to implement the `kos.api.ImplementationLoader` interface. Once you've
implemented you need to register it as the default Implementation Loader by exposing
it as Service Provider implementation. You can do so by creating a
`META-INF/services/kos.api.ImplementationLoader` file with the canonical name of
your just created class inside of it.
