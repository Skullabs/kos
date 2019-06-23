# Kos Architecture Overview
In the following topics we're going to introduce you the main points that Kos may come in handy on your project,
how it reduces boilerplate codes but keeps the performance close to a pure Vert.x solution.

## Differences between VertX and Kos
To understand the benefits of using Kos, lets create a _trivial_
[CRUD](https://en.wikipedia.org/wiki/Create%2C_read%2C_update_and_delete) of `User`. Below, we have
a basic source code that covers the creation, removal and retrieval of user. We'll use it as persistence
layer for two experiments: one using Kos and another one using pure Vert.x.

```java tab="User.java"
import lombok.*;
import java.util.*;

@Data
public class User {

    final UUID id = UUID.randomUUID();
    String name;
    ZonedDateTime creationDate;
}
```

```java tab="UserRepository.java"
import io.vertx.core.*;
import java.time.*;
import java.util.*;

interface UserRepository {

    Future<User> retrieveUserById(UUID id);

    Future<List<User>> retrieveUsersCreatedBetween(
        ZonedDateTime initialDate, ZonedDateTime endDate);

    Future<UUID> createUser(User user);

    Future<Void> removeUser(UUID id);
}
```

It worth mention that:

- All the example files bellow are written in Java, although it may look similar if written in Kotlin or Scala.
- `User.java` is the entity to be stored in the database - later we'll also use it as a _model_ in our Rest API.
For brevity, it uses [Lombok](http://projectlombok.org) to make example simpler.
- `UserRepository.java` the concrete implementation of our repository. For brevity, we've designed it as an interface.
Lets assume that we have a class `DefaultUserRepository` else where that properly implements the required methods.

### Vanilla Vert.x Webapp
```java tab="App.java"
import io.vertx.core.*;
import io.vertx.core.http.*;
import io.vertx.ext.web.*;
import java.util.*;
import lombok.*;

public class App { // 1

    private final Vertx vertx = Vertx.create();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    void start() {
        val configRetriever = loadConfigRetriever(); // 2
        configRetriever.getConfig(res -> {
            if (res.failed()) {
                handleFatalFailure("Failed to load configuration", res.cause());
            } else {
                Router router = Router.router(vertx); // 3
                // Ensure any POST message will have its body payload buffered before the
                // expected request handler method is called.
                router.post().handler( BodyHandler.create() );

                val config = res.result();
                deployVerticlesWithConfig(router, config);
                startVertxHttpServer(router, config);
            }
        });
    }

    private ConfigRetriever loadConfigRetriever() {
        val store = new ConfigStoreOptions()
            .setOptional(true)
            .setType("file")
            .setFormat("yaml")
            .setConfig(new JsonObject().put("path", "conf/application.yml"));

        val retrieverOptions = new ConfigRetrieverOptions();
        retrieverOptions.addStore(store);

        return ConfigRetriever.create(vertx, retrieverOptions);
    }

    private void deployVerticlesWithConfig(Router router, JsonObject config){
        val options = new DeploymentOptions().setConfig(res.result());
        val repository = new DefaultUserRepository();
        val userVerticle = UserApi.with(router, repository);
        vertx.deployVerticle(verticle, options); // 4
    }

    private void startVertxHttpServer(Router router, JsonObject config){
        val httpOptions = new HttpServerOptions(config);

        vertx.createHttpServer(httpServerOptions)
            .requestHandler(router)
            .listen( res -> {
                if ( res.failed() ) {
                    handleFatalFailure("Could not start server", as.cause() );
                } else {
                    val server = res.result();
                    Runtime.getRuntime().addShutdownHook(new Thread(server::close));
                    logger.info("Application started and listening for requests");
                }
            });
    }

    private void handleFatalFailure(String message, Throwable cause) {
        log.fatal(message, cause);
        System.exit(1);
    }

    // Starts the application from the command line.
    public static void main(String[] args){
        System.setProperty( // 5
            "vertx.logger-delegate-factory-class-name",
            SLF4JLogDelegateFactory.class.getCanonicalName()
        );

        new App().start();
    }
}
```

```java tab="UserApi.java"
import io.vertx.core.http.*;
import io.vertx.core.buffer.*;
import io.vertx.core.logging.*;
import io.vertx.core.json.*;
import io.vertx.ext.web.*;
import java.util.*;
import java.time.*;
import lombok.*;

class UserApi { // 6

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    final UserRepository repository;

    UserApi(UserRepository repository) {
        this.repository = repository;
    }

    void retrieveUserById(RoutingContext event) {
        val idAsString = event.request().params().get("id");
        val id = isAsString == null ? null : UUID.fromString(isAsString); // 7

        repository.retrieveUserById(id)
            .setHandler(UserApi::defaultResponseHandler); // 8
    }

    void retrieveUsersCreatedBetween(RoutingContext event) {
        val initialDateAsString = event.request().params().get("initDate");
        val initialDate = ZonedDateTime.parse(initialDateAsString);
        val endDateAsString = event.request().params().get("endDate");
        val endDate = ZonedDateTime.parse(endDateAsString);

        retrieveUsersCreatedBetween(initialDate, endDate)
            .setHandler(UserApi::defaultResponseHandler);
    }

    void createUser(RoutingContext event) {
        val user = Json.decodeValue(context.getBody(), User.class);
        createUser(user)
            .setHandler(UserApi::defaultResponseHandler);
    }

    void removeUser(RoutingContext event) {
        val idAsString = event.request().params().get("id");
        val id = isAsString == null ? null : UUID.fromString(isAsString);

        removeUser(id)
            .setHandler(UserApi::defaultResponseHandler);
    }

    <T> static void defaultResponseHandler(AsyncResult<T> res) { // 9
        if (res.failed()) {
            sendInternalServerError(event, res.cause());
        } else {
            sendOkResponseAsJson(event, res.result());
        }
    }

    static void sendInternalServerError(RoutingContext event, Throwable cause) { // 10
        val req = event.request();
        logger.error("Failed to handle request: " + req..method() + " - " + .path(), cause);
        event.response().setStatusCode(500).end();
    }

    static void sendOkResponseAsJson(RoutingContext event, Object object) {
        val buffer = Json.encodeToBuffer(object); // 11
        event.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .setStatusCode(200)
            .end(buffer);
    }

    static UserApi create(Router router, UserRepository repository) {
        val api = UserApi(repository);
        router.get("/users/:id", api::retrieveUserById); // 12
        router.get("/users/:initDate/:endDate", api::retrieveUsersCreatedBetween);
        router.post("/users", api::createUser);
        router.delete("/users/:id", api::removeUser);
        return api;
    }
}
```

Although the above application is perfectly efficient and fast as it is reliant on Vert.x core,
the code itself is far from simple. Let's dive into the source code and look into the numbered
lines to understand what it's actually doing.

1. The `App` class is responsible for starting the web server, providing everything is absolutely
necessary to deploy our verticles. Usually the pieces here configured doesn't change that often.
2. It will read the configuration file as the first thing will happen when the application start,
exiting the application once it fails.
3. The router will be used as argument for our verticles. Following the
[instructions](https://vertx.io/docs/vertx-web/java/#_request_body_handling) of the
official documentation, here it has been configured to eagerly read body payloads received by
any POST request.
4. Manually deploying Verticles is not a hard task although it comes with its own challenges.
Due to the flexible nature of Vertx Web API, there's not standard way to organize the source
code and deploy our web verticles. A simple look into the available
[vertx examples](https://github.com/vert-x3/vertx-examples) and you'll easily notice that. Here
we've decided to delegate the endpoint mapping to the verticle itself, centralizing the routing
strategy in a single place.
5. The JVM ecosystem has several mature and reliable log frameworks which diverges in contract
and configuration. Thankfully Vertx Core has it's logging mechanism configurable. Here we forced
to use popular SLF4J as logger implementation. This strategy also allows us to provide a self-contained
package that doesn't expects command line parameters to spin the application.
6. The `UserApi` class acts bridge between our Rest API and the `UserRepository`. Depending on how
big are the changes in requirements, this class may have to be changed frequently by the developers.
7. Extracting parameters from the request is a trivial task too. It can be tricky though to parse
strings values, check for nullable values and pass the values to the expected business logic (in our
case, our repository).
8. Underneath Vert.x makes heavily usage of its netty-based Event Loop implementation. Smart developers
will never block the EL and will always rely on non-blocking or asynchronous APIs to perform calls to
a downstream service. The caveat though is that we should properly compose `Future` and `Promise`
objects in order to provide a consistent and integer API to our users.
9. Here we have our centralized handler that is used any time we need to analyze `Future` objects and
send a response to the client of our Rest API.
10. Our naive error handler implementation here is straightforward, sending a simple 500 response to
the Http client.
11. Response serialization is something that should be manually handled here and if properly written
will not impact that much on developers productivity.
12. As described before, the Route Mapping algorithm has been moved to the UserApi class, as it makes
sense to have the mapping and the bridge methods in the same place.

### Kos webapp
The above steps indeed makes sense and covers a fairly amount of features that is expected from a development
platform, although one may expect to have less work to bridge their business logic from the network. Kos comes
as tool to significantly reduce this layer.

```java tab="UserApi.java"
import java.util.*;
import java.time.*;
import kos.rest.*;
import lombok.extern.slf4j.Slf4j;
import injector.Singleton;

@Slf4j
@Path("users")
@Singleton class UserApi {

    final UserRepository repository;

    UserApi(UserRepository repository) {
        this.repository = repository;
    }

    @GET(":id")
    Future<User> retrieveUserById(@Param UUID id) {
        return repository.retrieveUserById(id);
    }

    @GET(":initDate/:endDate")
    Future<List<User>> retrieveUsersCreatedBetween(
        @Param ZonedDateTime initDate,
        @Param ZonedDateTime endDate
    ) {
        return repository.retrieveUsersCreatedBetween(initDate,endDate);
    }

    @POST
    Future<UUID> createUser(User user) {
        return repository.createUser(user);
    }

    @DELETE(":id")
    Future<Void> removeUser(UUID id) {
        return repository.removeUser(id);
    }
}
```

It was rather simplified. Let's walk through the biggest differences we have between both examples.

1. The first thing that draws our attention once we read the source code is the introduction of Kos annotations
that are used as mapping entry points. It is mainly used to mark routing methods and automatically bind them
to a Rest endpoint.
2. Our controller is not being manually bond to the Vert.x `Router` or event the `Vertx` instance. In fact,
we don't even had to configure a Vertx application at all. Underneath a new `Vertx` and `Router`
instance will be spun up and use it whenever you use the Kos annotations.
3. The introduction of the `injector.Singleton` annotation. Kos is very flexible and provides a versatile
dependency injection where you can optionally use your favorite Dependency Injection framework. Out-of-box
though it only supports [Injector](https://skullabs.github.io/injector/) and
[SPI classes](https://www.baeldung.com/java-spi) - more on that below.

## Dependency Injection: Kos' Backbone
Dependency injection is the literally Kos' backbone. As you can imagine from the previous examples,
in order to simplify the way you design a Rest API a few conventions has been adopted to automatically
discovery your endpoints, to deploy verticles and even read the configuration file from the file system.

Looking to the steps we usually do in order to have a Vert.x application up and running, we figured out
that Kos should perform the following during the boot:

1. Load all classes implementing `io.vertx.core.Verticle`: We need a way to discovery _verticles_ and
deploy them automatically.
2. Load [configuration](https://vertx.io/docs/vertx-config/java/) automatically: It's also convenient to
have the configuration file loaded automatically once the application is started.
3. Discovery Rest endpoints automatically - or at least provide a way for developers to access Vert.x
[Router](https://vertx.io/docs/vertx-web/java/#_basic_vert_x_web_concepts).
4. Figure out which (supported) log API the developer is using and make Vert.x use it automatically -
instead of manually set a parameter in command line every time you spin up your server.

There are multiple ways to achieve these goals, and few libraries can easily provide that for us. There are
though a few assumptions that we had to assess them:

- Everything should be optional.
- Any automation should be consciously triggered by the developers will.
- It should have little to no cost during the application startup.
- It should impose no-overhead in our application runtime.

Taking the previously mentioned item 1 as an example, the optimal scenario should deploy all the classes which
implements `Verticle` interface, but only the ones intentionally marked as "discoverable". After a few days
of research and experiments we can with the idea of using [Java 6 Service Provider Interfaces](https://www.baeldung.com/java-spi)(SPI)
to discover those implementations. It obviously fits our requirements, and requires no external dependency.

The major limitation of SPI lies on the fact that your classes should have a default (no-args) constructor.
This can impose severe restrictions on how developers will bridge their business logic from the Rest API. It
forced us to make [Dependency Injection customizable](dependency-injection). [Injector](https://skullabs.github.io/injector/)
is an optional dependency injector that can be used to surpass such limitations, as it also fits into our requirements,
makes no-use of reflection and class loader, and is standalone - no external dependency. A list of alternative
supported DI frameworks can be found [here](dependency-injection).

## WebService Verticle
`kos.core.VertxWebServer` is an special `Verticle` implementation that comes with the `kos-core` module.
It is responsible for:

- Spinning up Vert.x [Router](https://vertx.io/docs/vertx-web/java/#_basic_vert_x_web_concepts) and
the [HttpServer](https://vertx.io/docs/vertx-web/java/#_basic_vert_x_web_concepts).
- Discovering Rest endpoints mapped via `kos-annotations`
- Trigger all implementations of `WebServerEventListener`

## Kos Launcher
Kos applications are started by the `kos.core.Launcher` class. Its main job is:

- Lookup for all classes that implements `Verticle` interface - exposed as SPI - and automatically deploy them.
- Lookup for the first `ConfigRetriever` implementations - exposed as SPI - and automatically deploy it.

