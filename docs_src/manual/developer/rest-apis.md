# Rest API
Write down rest API's with Kos is dead simple and, if you've been using
Java ecosystem in the last few years you're probably at home.

First, be sure you've included `kos-annotations` in your compilation Class Path,
and double-check if you have enabled APT compilation as well (enabled by default
only in Java projects).

=== "Gradle (kts)"
    ```kotlin
    compileOnly("io.skullabs.kos:kos-annotations")
    ```
=== "Maven (pom.kts)"
    ```kotlin
    provided("io.skullabs.kos:kos-annotations")
    ```
=== "Maven (pom.xml)"
    ```xml
    <dependency>
        <groupId>io.skullabs.kos</groupId>
        <artifactId>kos-annotations</artifactId>
        <scope>provided</scope>
    </dependency>
    ```

## Classes as API entrypoints
Kos relies on JVM classes as entrypoint for your APIs. That said, you
should identify your class with the `kos.rest.RestApi` annotation. It will
allow Kos to monitor this class for routes.

=== "Kotlin"
    ```kotlin
    import kos.rest.*
    
    @RestApi
    class MyApi {
    }
    ```
=== "Java"
    ```java
    import kos.rest.*;
    
    @RestApi
    class MyApi {
    }
    ```

If your API contains a common root path that could be shared by two or more
endpoints, you can set a root path for you API using the `RestApi` annotation.

=== "Kotlin"
    ```kotlin
    import kos.rest.*
    
    @RestApi("/money")
    class MyApi {
    }
    ```
=== "Java"
    ```java
    import kos.rest.*;
    
    @RestApi("/money")
    class MyApi {
    }
    ```

## Exposing Methods as Rest endpoints
Out-of-box you can expose any `public` or `package default` method as
Rest endpoint by using one of the following annotations:

- `kos.rest.GET`
- `kos.rest.POST`
- `kos.rest.PUT`
- `kos.rest.DELETE`
- `kos.rest.PATCH`

## Defining URIs for your endpoints
Just as `kos.rest.RestApi` can be used to map a (root) path to an endpoint, the above
mentioned annotations can also be used to define a more specific endpoint URI for your
methods.

In the below example we have two methods exposed as Rest endpoints:

- `MyApi.getMoney` will be exposed as `GET` operation mapped to `/money` URI.
- `MyApi.getMoreMoney` will also be exposed as `GET` operation, but mapped to `/money/double` URI.

=== "Kotlin"
    ```kotlin
    import kos.rest.*
    
    @RestApi("/money")
    class MyApi {
    
        @GET
        fun getMoney() = 10
    
        @GET("/double")
        fun getMoreMoney() = getMoney() * 2
    }
    ```
=== "Java"
    ```java
    import kos.rest.*;
    
    @RestApi("/money")
    class MyApi {
    
        @GET
        Integer getMoney(){
            return 10;
        }
    
        @GET("/double")
        Integer getMoreMoney(){
            return getMoney() * 2;
        }
    }
    ```

## Sending responses to the Http Client
Kos Rest API's will infer the response from the return type defined on your functions:

- `kos.api.Response`: this is a special response type bundled in Kos so developers can easily define
  custom responses to Http Clients. It is particularly useful when you method needs to return different
  types of Response objects, or different types of Http Status Responses. 
- `void`: methods that doesn't return a value will make your method API receive the request, perform
  all the synchronous tasks defined within it and immediately return `204` as default answer. 
- `io.vertx.core.Future<?>`: by returning Vert.x's Future your Http Clients will wait for the
  the `Future` to be completed to receive a response. In case of a success result, the object by the
  Future instance will be serialized and a successful response will be sent. On the other hand,
  if your Future holds a failure result, an error message will be sent.
- Plain Objects: API's that returns plain object will have it's returned object serialized via Default
  Serializer mechanism and sent as response to your Http Client.
  
??? info "How serialization work?"
    Successful responses are automatically serialized through the Default Serializer mechanism. However,
    in case an exception has been thrown or returned as Future response, the default Exception Handler mechanism
    will be in charge of converting it into an Http Response.

=== "Kotlin"
    ```kotlin
    import kos.rest.*
    import io.vertx.core.*
    import java.util.concurrent.ThreadLocalRandom
    
    @RestApi("/money")
    class MyApi {
    
        @GET
        fun getMoney(): Future<Integer> {
            val money = Promise.promise()
            /*
             * Compute my money. It can be done using any asynchronous operation
             * as long as you don't block Vert.x's Event Loop.
             */
            return money.future()
        }
    
        @GET("/random")
        fun getMoreMoney() =
            return ThreadLocalRandom.current().nextInt();
    
        @POST
        fun generateMoney(){
            println("This might increase the inflation!")
    
            val generated = ThreadLocalRandom.current().nextInt()
            println("Generated money: $$generated.00");
        }
    }
    ```
=== "Java"
    ```java
    import kos.rest.*;
    import io.vertx.core.*;
    import java.util.concurrent.ThreadLocalRandom;
    
    @RestApi("/money")
    class MyApi {
    
        @GET
        Future<Integer> getMoney(){
            Promise<Integer> money = Promise.promise();
            /*
             * Compute my money. It can be done using any asynchronous operation
             * as long as you don't block Vert.x's Event Loop.
             */
            return money.future();
        }
    
        @GET("/random")
        Integer getMoreMoney(){
            return ThreadLocalRandom.current().nextInt();
        }
    
        @POST
        void generateMoney(){
            System.err.println("This might increase the inflation!");
    
            int generated = ThreadLocalRandom.current().nextInt();
            System.out.println("Generated money: $" + generated + ".00");
        }
    }
    ```

## URI Mapping Conventions
As Kos don't replace Vert.x's routing mechanism, but only automates its creation,
you still can take full advantage of it when using Kos' annotation. You can use:

- [fixed defined paths](https://vertx.io/docs/vertx-web/js/#_routing_by_exact_path) -
  with no placeholders or wildcard marks. e.g. `/some/path/`
- [paths that begins with something](https://vertx.io/docs/vertx-web/js/#_routing_by_paths_that_begin_with_something) -
  e.g. `/some/path/*`
- [paths with variable placeholders](https://vertx.io/docs/vertx-web/js/#_capturing_path_parameters) -
  e.g. `/catalogue/products/:producttype/:productid/`

It is worth notice though that it doesn't support RegEx mapping though.

## Capturing parameters
The following sub-topics will describe how to capture parameters received from the
Http Client (e.g. path parameters) in your Rest endpoint method. Vert.x core expects
parameters to be either `CharSequence` or `String`. To automatically they will be converted
into the desired type by the _String Converter Mechanism_ beforehand, making these
values available for developers transparently.

A few rules apply though:

- You have to define the placeholder name you are willing to use
- If you haven't defined the parameter name, the parameter variable name will be used instead
- If the defined name doesn't match any existing placeholder, an Exception might be thrown
- If you have defined a complex object for you captured parameter, and no converter has been
defined to that given type, an Exception might be thrown.

### Path Parameters
All the matched path placeholders can be easily mapped as parameters in your just
created method by using the `kos.rest.Path` annotation.

=== "Kotlin"
    ```kotlin
    import kos.rest.*
    
    @RestApi
    class MyApi {
    
        @GET("/calc/number/:a")
        fun getMoney( @Param a: String ) = a
    
        @POST("/calc/plus/:a/:b")
        fun generateMoney(
            @Param("a") first: Int,
            @Param("b") second: Int
        ) = first + second
    }
    ```
=== "Java"
    ```java
    import kos.rest.*;
    
    @RestApi
    class MyApi {
    
        @GET("/calc/number/:a")
        Integer getMoney( @Param Integer a ){
            return a;
        }
    
        @POST("/calc/plus/:a/:b")
        Integer generateMoney(
            @Param("a") Integer first,
            @Param("b") Integer second
        ) {
            return first + second;
        }
    }
    ```

### Query parameters
As Vert.x treats query parameters and paths similarly (both being accessible via
`io.vertx.core.http.HttpServerRequest.getParam(String)`), you can use the same
`kos.rest.Path` to capture query strings as well.

### Http Headers
Similarly to path and query parameters, we can capture headers send by the Http Client
by using the `kos.rest.Header` annotation.

=== "Kotlin"
    ```kotlin
    import kos.rest.*
    
    @RestApi
    class MyApi {
    
        @GET("/what-is-my-user-agent")
        fun whatIsMyUserAgent(
            @Param("Content-Type") contentType: String
        ) = contentType
    }
    ```
=== "Java"
    ```java
    import kos.rest.*;
    
    @RestApi
    class MyApi {
    
        @GET("/what-is-my-user-agent")
        String whatIsMyUserAgent(
            @Param("Content-Type") String contentType
        ) {
            return contentType;
        }
    }
    ```

## Capturing the request payload
As you might imagine, similarly to capturing other params you can capture the request
payload sent in the Http Request's body using an annotation. In this case, it will be
`kos.rest.Body`. Unlike other parameters though, payloads will be converted into an
object by the default _Serializer Mechanism_ configured on your application.

=== "Kotlin"
    ```kotlin
    import kos.rest.*
    
    @RestApi
    class MyApi {
    
        @POST("/money")
        fun sendMoney(
            @Body request:SendMoneyRequest
        ) = "${request.to} has received $${request.amount}"
    }
    
    data class SendMoneyRequest(
        val to: String,
        val amount: Double
    )
    ```
=== "Java"
    ```java
    import kos.rest.*;
    
    @RestApi
    class MyApi {
    
        @POST("/money")
        String sendMoney(
            @Body SendMoneyRequest request
        ) {
            return request.to " has received $" + request.amount;
        }
    }
    
    class SendMoneyRequest {
        String to;
        Double amount;
    
        // getters and setters
    }
    ```
