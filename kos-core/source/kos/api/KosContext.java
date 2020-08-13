package kos.api;

import io.vertx.core.Future;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.ext.web.client.WebClient;
import lombok.NonNull;

/**
 * While Vert.x provides Json-based configuration out-of-box, it is not suitable
 * to configure Vert.x internals. This object is read-only representation of all
 * sensible configuration that have been previously defined at the Kos's launch.
 */
public interface KosContext
{

    @NonNull ImplementationLoader getImplementationLoader();

    @NonNull PayloadSerializationStrategy getPayloadSerializationStrategy();

    int getDefaultStatusForEmptyResponses();

    @NonNull io.vertx.core.spi.logging.LogDelegateFactory getLogDelegateFactory();

    @NonNull Serializer getDefaultSerializer();

    @NonNull kos.core.client.RestClientSerializer getDefaultRestClientSerializer();

    @NonNull io.vertx.core.Vertx getDefaultVertx();

    @NonNull WebClient getDefaultVertxWebClient();

    @NonNull StringConverter getStringConverter();

    @NonNull ExceptionHandler getExceptionHandler();

    @NonNull io.vertx.config.ConfigRetriever getConfigRetriever();

    @NonNull Logger createLoggerFor(Class type);

    @NonNull JsonObject readApplicationConfig();

    @NonNull Serializer getSerializerForContentType(String contentType);

    /**
     * The default server configuration. It will be triggered
     * when the auto-discovery mechanism failed to find one in the classpath.
     */
    @NonNull HttpServerOptions getHttpServerOptions();

    @NonNull ImplementationLoader getSpi();
}
