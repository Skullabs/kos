# 1.3. Rest Clients
Write down rest clients is usually a task most people doesn't like to do,
specially in the JVM ecosystem. We have several Http abstractions and
implementations, each of them with different dependency stack. Vert.x team
introduced a web client API on their `vertx-web` module seamlessly integrated
with `vertx-core`, avoiding developers to face the so called dependency-hell.

By borrowing a few ideas from [Netflix Feign](https://github.com/OpenFeign/feign)
and other known open source frameworks, Kos provides a simple API to create
Rest Clients that takes advantage of the well crafted `vertx-web` module. And,
if you've read the [Rest API](../02-rest-apis) documentation, you'll notice that
Rest Clients also relies on `kos.rest.*` annotations being semantically similar
to its [Rest API](../02-rest-apis), so you only have to memorize a single set
of annotations.

It means:

- you can use the [same annotations](../02-rest-apis/#exposing-methods-as-rest-endpoints) to map endpoints to methods
- the same applies to map [path parameters](../02-rest-apis/#path-parameters),
  [query parameters](../02-rest-apis/#query-parameters)
  and [headers](../02-rest-apis/#http-headers)
  to arguments
- You can even use the same [Body annotation](../02-rest-apis/#capturing-the-request-payload)
  to serialize and send request payloads

## Interfaces as API entrypoints
The biggest difference though lies on the fact that you don't write concrete classes,
but interfaces to define your Rest Clients. You also have to annotate them with `kos.rest.RestClient`
instead of the `kos.core.RestApi` one.

=== "Kotlin"
    ```kotlin
    import kos.rest.*
    
    @RestClient("/calc")
    interface CalculatorApiClient {
    
        @GET("/plus/:a/:b")
        fun plus(
            @Param a: Int,
            @Param b: Int
        ): Future<Int>
    
        @GET("/minus/:a/:b")
        fun minus(
            @Param a: Int,
            @Param b: Int
        ): Future<Int>
    }
    ```
=== "Java"
    ```java
    import kos.rest.*;
    
    @RestClient("/calc")
    interface CalculatorApiClient {
    
        @GET("/plus/:a/:b")
        Future<Int> plus(
            @Param Integer a,
            @Param Integer b
        );
    
        @GET("/minus/:a/:b")
        Future<Int> minus(
            @Param Integer a,
            @Param Integer b
        );
    }
    ```

## Consuming responses to the Http Server
Kos Rest API's will infer the response from the return type defined on your functions:

- `void`: methods that doesn't return a value will make the call to the desired API in
  a fire-and-forget fashion, completely ignoring the response received by the server.
- `io.vertx.core.Future<?>`: Http Clients will hold the received response in a Future object
  allowing you to handle the response asynchronously on your first convenience.

As you probably notice, Http Clients never blocks the event loop. It comes with a few
restrictions though:

- You have no automated mechanism to map failures into objects
- In case of Http responses other than `2xx` an Exception (`UnexpectedRestClientResponse`)
  will be set as future Response allowing you to manually handle the unexpected behavior.

## Instantiating your Rest Client
Unlike Rest API's endpoints which are automatically deployed by Kos, Rest Clients
will only be instantiated when required by your source code. They are instantiated
by the `RestClientFactory.instantiate(RestClientConfiguration, T)` method.

Below we discuss how you can instantiate them either using or not dependency injection.
You're going to notice that in both cases we need a factory method to expose it to the
rest of the system. The big difference is how you can access the factory and the memory
consumption they have. For further details on how Dependency Injection works with Kos
please process to [this topic](../11-dependency-injection). 

### Using Dependency Injection
=== "Kotlin"
    ```kotlin
    import injector.*
    import kos.core.client.RestClientConfiguration
    
    @Singleton
    class CalculatorApiClientConfiguration(
        val restClientFactory: RestClientFactory
    ){
    
        @Produces
        // Note: for optimal performance you might consider lazy load the client
        fun produceClient(): CalculatorApiClient {
            val baseUrl = "https://empty.url"
            val restConf = RestClientConfiguration.withUrl(baseUrl).build()
            return restClientFactory.instantiate(restConf, CalculatorApiClient::class.java)
        }
    }
    
    @RestApi
    class MyApi(val calculator: CalculatorApiClient) {
    
        @GET("/calc/2/plus/2")
        fun calculateTwoPlusTwo() = calculator.plus(2, 2)
    }
    ```
=== "Java"
    ```java
    import injector.*;
    import io.vertx.core.Future;
    import kos.core.client.RestClientConfiguration;
    
    @Singleton
    public class CalculatorApiClientConfiguration {
    
        final RestClientFactory restClientFactory;
    
        public CalculatorApiClientConfiguration(RestClientFactory restClientFactory) {
            this.restClientFactory = restClientFactory;
        }
    
        @Produces
        // Note: for optimal performance you might consider lazy load the client
        private CalculatorApiClient produceClient() {
            val baseUrl = "https://empty.url";
            val restConf = RestClientConfiguration.withUrl(baseUrl).build();
            return restClientFactory.instantiate(restConf, CalculatorApiClient.java);
        }
    }
    
    @RestApi
    public class MyApi {
    
        final CalculatorApiClient calculator;
    
        public MyApi(CalculatorApiClient calculator) {
            this.calculator = calculator;
        }
    
        @GET("/calc/2/plus/2")
        Future<Integer> calculateTwoPlusTwo() {
            return calculator.plus(2, 2);
        }
    }
    ```

### Using plain code
=== "Kotlin"
    ```kotlin
    import kos.core.client.RestClientConfiguration
    
    class CalculatorApiClientConfiguration {
    
        // Note: for optimal performance please consider to use Kos.implementationLoader to instantiate it
        val restClientFactory = RestClientFactory();
    
        // Note: for optimal performance you might consider lazy load the client
        fun produceClient(): CalculatorApiClient {
            val baseUrl = "https://empty.url"
            val restConf = RestClientConfiguration.withUrl(baseUrl).build()
            return restClientFactory.instantiate(restConf, CalculatorApiClient::class.java)
        }
    }
    
    
    @RestApi
    class MyApi {
    
        // Note: for optimal performance please consider to use Kos.implementationLoader to instantiate it
        val calculator = CalculatorApiClientConfiguration().produceClient()
    
        @GET("/calc/2/plus/2")
        fun calculateTwoPlusTwo() = calculator.plus(2, 2)
    }
    ```
=== "Java"
    ```java
    import io.vertx.core.Future;
    import kos.core.client.RestClientConfiguration;
    
    public class CalculatorApiClientConfiguration {
    
        // Note: for optimal performance please consider to use Kos.implementationLoader to instantiate it
        final RestClientFactory restClientFactory = new RestClientFactory();
    
        // Note: for optimal performance you might consider lazy load the client
        private CalculatorApiClient produceClient() {
            val baseUrl = "https://empty.url";
            val restConf = RestClientConfiguration.withUrl(baseUrl).build();
            return restClientFactory.instantiate(restConf, CalculatorApiClient.java);
        }
    }
    
    @RestApi
    public class MyApi {
    
        final CalculatorApiClient calculator = new CalculatorApiClientConfiguration().produceClient();
    
        @GET("/calc/2/plus/2")
        public Future<Integer> calculateTwoPlusTwo() {
            return calculator.plus(2, 2);
        }
    }
    ```
