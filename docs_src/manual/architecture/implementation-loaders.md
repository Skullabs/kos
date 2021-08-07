# Implementation Loaders
Internally, Kos refers to Dependency Injection mechanisms as _Implementation Loaders_.
They are responsible to perform two basic tasks the Kos needs all the time:

1. Load an object implementing a given Interface - or extending a given class
2. Load all objects implementing a given Interface - or extending a given class

## Types of Implementation Loaders
The next topics will cover all DI mechanism provided out-of-box with Kos.

### Injector

Injector is lightweight and zero-overhead, dependency injection library for JVM developers. It
was carefully designed to make no-use of reflection by having all required meta-information
computed at compile time. At runtime, it performs only the necessary tasks required to instantiate
classes and have its dependencies injected. The result is a blistering fast Dependency Injection
implementation that has around 7kb of footprint.

To provide an implementation of a given Kos service, all you have to do is implement a given
interface (or extend a given class) and annotate with `injector.Exposed` annotation. Also,
to make ordinary classes able to be managed by Injector you should annotate them with
either `injector.Singleton` or `injector.New`.

!!! info
    For more details on how Injector works, please proceed to its
    [documentation](https://skullabs.github.io/injector/).

Below you can find a _Custom Exception Handler_ implementation.
```java
import injector.Exposed;
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

### Custom Dependency Injection Mechanism
As mentioned before, the Implementation Loader is a simple mechanism and doesn't
enforce you to stick with a single implementation. To provide your own Implementation Loader
you need to implement the `kos.api.ImplementationLoader` interface. Once you've
implemented you need to register it as the default Implementation Loader by exposing
it as Service Provider implementation. You can do so by creating a
`META-INF/services/kos.api.ImplementationLoader` file containing the canonical name of
your just created class.
