# Basic Concepts
Kos is just a tiny opinionated layer that wraps all the basic functionality we love from
Vert.x behind a simple syntax. In this page we will enumerate the concepts that you need
to learn before you use Kos.

## Annotation Processors
Vert.x is rather powerful. It was designed as a toolkit, and can be used to design almost everything. Kos,
on the other hand, generates the code that "glues" your application layer to Vert.x. This is process happens
at compile time through the Annotation Processing Tool provided by the JDK.

## Dependency Injection
Kos makes heavy usa of a tiny Dependency Injection (DI) library called [Injector](https://skullabs.github.com/injector)
to discover dependencies, configurations and components - becoming the backbone of the whole system.
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
- `@Listener` - automatically listens for Vert.x's EventBus events
- `@Publisher` - automatically publishes Vert.x's EventBus events
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
- Default EventBus's `MessageCodec` for message serialization
