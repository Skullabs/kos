package kos.tests;

import io.vertx.core.Future;
import kos.api.Response;
import kos.rest.*;
import kos.validation.Valid;

import java.util.concurrent.CompletableFuture;

@RestApi("/api/customer/:customerId/")
public interface ApiWithAllAnnotations {

    @GET void doNothing();

    @GET("/search")
    Response doSomethingElse(
        @Param("q") String searchString
    );

    @PUT(":id")
    void updateSomething(
        @Param("q") String searchString,
        @Valid @Body Something something
    );

    @POST
    void persistSomething(
        @Header("X-Secure-Creds") String credentials,
        @Valid @Body Something something
    );

    @PATCH
    Future<Response> amendMySystem();

    @DELETE
    Future<Response> deleteData(
        @Context SomethingElse somethingElse
    );
}

class Something {}
class SomethingElse {}