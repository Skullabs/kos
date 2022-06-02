# Rest Clients
Write down rest clients is usually a task most people don't like to do,
especially in the JVM ecosystem. We have several Http abstractions and
implementations, each of them with different dependency stack. Vert.x team
introduced a web client API on their `vertx-web` module, which is managed
by `vertx-core` connection pool for optimal resource utilization.

By borrowing a few ideas from [Netflix Feign](https://github.com/OpenFeign/feign)
and other well known open source frameworks, Kos provides a simple API to create
Rest Clients that takes advantage of the well crafted `vertx-web` module. It is
behaves similarly to the Rest API:

- it uses the [same annotations](../rest-apis/#exposing-methods-as-rest-endpoints) as the Rest Api to map endpoints
  to methods
- the same applies when mapping [path parameters](../rest-apis/#path-parameters),
  [query parameters](../rest-apis/#query-parameters)
  or [headers](../rest-apis/#http-headers) to method parameters
- it also uses the same [Body annotation](../rest-apis/#capturing-the-request-payload)
  to serialize and send request payloads

## Interfaces as API entrypoint
The biggest difference lies on the fact that you don't write concrete classes to define your Rest Clients,
you write interfaces. You also have to annotate them with `kos.rest.RestClient` instead of the `kos.core.RestApi` one.

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
Kos Rest Client's will infer the response from the return type defined on your functions:

- `void`: methods that doesn't return a value will make the call to the desired API in
  a fire-and-forget fashion, completely ignoring the response received by the server.
- `io.vertx.core.Future<?>`: Http Clients will hold the received response in a Future object
  allowing you to handle the response asynchronously on your first convenience.

As you probably notice, Http Clients never blocks the event loop. This comes with a few
restrictions though:

- You have no automated mechanism to map failures into objects
- In case of Http responses other than `2xx` an Exception (`UnexpectedRestClientResponse`)
  will be set as future Response allowing you to manually handle the unexpected behavior.

## Instantiating your Rest Client
As the Rest Client API only takes care of mapping endpoints it doesn't know anything
else about your client. By using the `RestClientFactory.instantiate(RestClientConfiguration, T)`
method, you can define how it will work, which URL should be used to reach the server,
how authentication might work, etc.

=== "Kotlin"
    ```kotlin
    import injector.*
    import kos.core.client.RestClientConfiguration
    
    @Singleton
    class CalculatorApiClientConfiguration(
        val restClientFactory: RestClientFactory
    ){
        @Producer
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
        CalculatorApiClient produceClient() {
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

The example above will use Injector to produce an instance of our client
whenever one needs to be injected in our controller. As our controller is
a `@Singleton`, only one instance will be created.

!!! info
    For optimal resource usage, you might want to cache the created client.

## RestClientConfiguration
Here are the main methods of the `RestClientConfiguration` class:

- `withUrl`: Set the base URL
- `withHeaders`: Set custom headers
- `withRestClientSerializer`: Define how successful responses will be serialized
- `withStringConverter`: Allow customizing how parameters and headers will be
    converted from Objects to `String`
- `withClient`: Allow defining a custom Vert.x `WebClient` instance - useful when
  we need to perform an advanced client configuration.
