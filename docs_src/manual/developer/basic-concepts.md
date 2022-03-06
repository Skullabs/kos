# Basic Concepts
Kos is just a tiny opinionated layer that wraps all the basic functionality we love from
Vert.x behind a simple syntax. In this page we will enumerate the concepts that you need
to learn before you use Kos.

## Motivation
Vert.x is a JVM reactive toolkit. From Database queries to web apis, it aims to provide
all the benefits of the reactive paradigm when developing services. However, as reactive
APIs, in general, are slightly more elaborate than traditional imperative programming,
using it for big projects might be challenging. Consider the following sample code:

```kotlin
class Server : AbstractVerticle() {
  override fun start() {
    vertx.createHttpServer().requestHandler { req ->
      val queryParams = context.queryParams();
      val name = queryParams.get("name") ?: "World";
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello $name")
    }.listen(8080)
  }
}
```

It is supposed to be a simple `Hello $name` endpoint, but it hides a few challenges to those unfamiliar with Vert.x or
reactive programming. A wise developer will certainly identify that this class has two responsibilities and will
segregate this into (at least) two different units: the controller and business logic.

```kotlin
class HelloBusiness {
    fun sayHelloTo(name: String) = "Hello $name"
}

class Server :  AbstractVerticle() {
    
    private val helloBusiness = HelloBusiness() 

    override fun start() {
        vertx.createHttpServer().requestHandler { req ->
            val queryParams = context.queryParams();
            val name = queryParams.get("name") ?: "World";
            
            val response = helloBusiness.sayHelloTo(name)
            
            req.response()
                .putHeader("content-type", "text/plain")
                .end(response)
        }.listen(8080)
    }
}
```

For this small example, it doesn't worth the effort: the `Server` class is a bit more complex, and the business logic
is a just a single line. If we expand this example ever further, though, it would become unmaintainable. Imagine what would happen if we try to
persist users, parse input parameters, deserialize request payload or use a configuration file to define the web server
port. Unless you have strong discipline, it's fairly likely that this project would soon become a big
ball of mud.

## Annotation Processors
Vert.x is rather powerful though. It was designed as a toolkit, and can be used to design almost everything. Kos helps
developers to focus on the business layer by generating the code that "glues" it to Vert.x. This is process happens
at compile time through the Annotation Processing Tool provided by the JDK. This allows you to develop something
like the code below and achieve the same result as our previous example.

```kotlin
@RestApi
class HelloBusiness {

    @GET
    fun sayHelloTo(@Param name: String) = "Hello $name"
}
```
## Dependency Injection
As they are generated at compile time, Kos needs a way to figure out how to discover and instantiate these classes.
This is why Kos makes usage of a tiny Dependency Injection (DI) library called [Injector](https://skullabs.github.com/injector).
Whenever the server is initialised, Kos will ask Injector for Web Routes, Validators, Event Listeners and other
Vert.x components that might have been created during the compilation process.

!!! info
    You can check the [Dependency Injection](../../architecture/implementation-loaders/) guide in case you want to
    a different DI library as a replacement for Injector.

### Implementation Discovery
Most of the Kos components are trivial to be configured. In the process, you might be asked to _"Expose"_ an implementation
of a given interface, so Kos can find it during the bootstrap process. There are two annotations that can be used
to make an interface implementation discoverable (or exposed): `injector.Exposed` and `injector.ExposedAs`.

- `ExposedAs` is used to explicitly expose the current class as an implementation of a given interface (or superclass)
- `Exposed` will expose the current to all interfaces it is directly implementing (ignoring superinterfaces or 
  interfaces implemented by its superclass).

### Automatically discovered classes
As the annotation process takes place, a few classes will be generated making the target class automatically
exposed on the Class Path. Classes (or classes which methods are) annotated with the following Kos annotations
will be automatically exposed:

- `@RestApi` - automatically exposes [Rest endpoints](../rest-apis/)
- `@RestClient` - automatically exposes [Rest clients](../rest-clients/)
- `@Listener` - automatically listens for Vert.x's Event-Loop internal events
- `@Validates` - turns a method into an object validator for Event listeners and Rest endpoints

## The Launcher
Kos has a small bootstrap class called `kos.core.Launcher` that will automatically spin up the server. With a little
help from [the Implementation Loader](../../architecture/implementation-loaders/), it will read the Vert.x configuration and deploy all verticles found on the class path. So, make
sure you set this class as your `Main-Class`.

## Kos Context
The `kos.api.KosContext` object contains all the internal components managed by Kos. Among many other features, it holds:

- `io.vertx.core.Vertx` instance - used whenever interacting with Vert.x components
- Log configuration
- Serialization mechanisms (for both Rest API and Clients)
- Implementation Loader (Dependency Injection) configuration
