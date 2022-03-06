# The Kos Context
`kos.api.KosContext` is the backbone of the system, containing all the basic components
in which Kos will interact with. To avoid misconfiguration, there will be
only one instance of this object (managed by Kos) in the  whole application. The
only way to mutate its content is by creating a [Plugin](../kos-plugins/).

## Things you can do with it
- **Programmatically access injectable dependencies** - By invoking `KosContext.getImplementationLoader()` you will be able to directly interact
with all [injectable dependencies found at compile time](../implementation-loaders/). If you have access to a `MutableKosContext`
instance, you will also be able to define a customised dependency injection framework - replacing _Injector_ completely.
- **Use a different serialization strategy** - By default, just as any other web server, Kos will always respect the HTTP Headers to infer which
type of serialization to use when handling a Http Request. You can change this by passing your own `PayloadSerialisationStrategy`
implementation to `MutableKosContext.setPayloadSerializationStrategy`.
- **Change the default payload serializer** - When using the default serialization strategy, Kos will use JSON as default serializer whenever the response Content-Type is
not defined or the Context-Type header is not present in the request. You can use the `MutableKosContext.setDefaultSerializer`
to modify the serialization type.

For more details, check the `KosContext` and `MutableKosContext` javadoc.