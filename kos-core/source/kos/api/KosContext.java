package kos.api;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import kos.core.client.RestClientSerializer;
import lombok.NonNull;

/**
 * This object is read-only representation of all sensible configuration
 * that have been previously defined at the Kos' application initialization.
 */
public interface KosContext
{

    /**
     * @return the defined {@link ImplementationLoader}
     */
    @NonNull ImplementationLoader getImplementationLoader();

    /**
     * @return the defined {@link PayloadSerializationStrategy}
     */
    @NonNull PayloadSerializationStrategy getPayloadSerializationStrategy();

    /**
     * @return the default HTTP Status code for empty responses. By default,
     *  it returns 204.
     */
    int getDefaultStatusForEmptyResponses();

    /**
     * @return the default {@link Serializer} used in case the expected {@code Content-Type}
     *  (defined by the {@code Accept} header) was not provided in the request.
     */
    @NonNull Serializer getDefaultSerializer();

    /**
     * @return the default {@link RestClientSerializer} used to serialize Request Payloads
     *  used by Kos' Rest Clients.
     */
    @NonNull RestClientSerializer getDefaultRestClientSerializer();

    /**
     * @return the default {@link RestClientSerializer} used to serialize Request Payloads
     *  used by Kos' Rest Clients.
     */
    @NonNull EventBusMessageCodecFactory getDefaultEventBusCodecFactory();

    /**
     * @return the {@link Vertx} instance used by the whole application.
     */
    @NonNull Vertx getDefaultVertx();

    /**
     * @return the {@link WebClient} instance used by the Rest Clients.
     */
    @NonNull WebClient getDefaultVertxWebClient();

    /**
     * @return the {@link StringConverter} used to convert String into Objects.
     */
    @NonNull StringConverter getStringConverter();

    /**
     * @return the {@link ExceptionHandler} used to handle unhandled exceptions.
     */
    @NonNull ExceptionHandler getExceptionHandler();

    /**
     * @return the default {@link Validation} used to assess classes subject
     * to validation.
     */
    @NonNull Validation getDefaultValidation();

    /**
     * @return the default {@link ConfigRetriever} used to read the Vert.x configuration.
     */
    @NonNull ConfigRetriever getConfigRetriever();

    /**
     * @return the read Vert.x configuration object.
     */
    @NonNull JsonObject getApplicationConfig();

    /**
     * @return the {@link Serializer} configured for a given {@code contentType}.
     */
    @NonNull Serializer getSerializerForContentType(String contentType);

    /**
     * The default server configuration. It will be triggered
     * when the auto-discovery mechanism failed to find one in the classpath.
     */
    @NonNull HttpServerOptions getHttpServerOptions();

    @NonNull ImplementationLoader getSpi();

    Future<Void> runBlocking(RunnerThatMightFail runner);

    <T> Future<T> computeBlocking(SupplierThatMightFail<T> supplier);

    @FunctionalInterface
    interface SupplierThatMightFail<T> { T get() throws Exception; }

    @FunctionalInterface
    interface RunnerThatMightFail { void run() throws Exception; }
}
