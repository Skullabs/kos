package kos.core;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import kos.api.ConfigurationLoadedEventListener.ConfigurationLoadedEvent;
import kos.api.ImplementationLoader;
import kos.api.MutableKosContext;
import kos.api.Plugin;
import kos.core.exception.KosException;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Launcher")
class LauncherTest {

    final ImplementationLoader implLoader = spy(new ImplementationLoader.SPIImplementationLoader());
    final Vertx vertx = spy(Vertx.vertx());
    final MutableKosContext kosConf = spy(new MutableKosContext(implLoader).setDefaultVertx(vertx));
    final Launcher launcher = spy(new Launcher(kosConf));

    @DisplayName("Should configure Kos and call plugins")
    @Test void scenario1(){
        val plugin = mock(Plugin.class);
        doReturn(singletonList(plugin)).when(implLoader).instancesExposedAs(eq(Plugin.class));

        launcher.run();

        verify(plugin).configure( eq(kosConf) );
    }

    @DisplayName("Should configure Kos and call plugins sorted by its priority")
    @Test void scenario1b(){
        val plugin = mock(Plugin.class);
        doReturn(0).when(plugin).priority();

        val plugin2 = mock(Plugin.class);
        doReturn(1).when(plugin2).priority();

        doReturn(asList(plugin, plugin2)).when(implLoader).instancesExposedAs(eq(Plugin.class));

        launcher.run();

        val ordered = inOrder(plugin2, plugin);

        ordered.verify(plugin2).configure( eq(kosConf) );
        ordered.verify(plugin).configure( eq(kosConf) );
    }

    @DisplayName("Scenario: WebServer deployment")
    @Nested class WebServerDeployment {

        final JsonObject appConf = spy(new JsonObject());
        final ConfigurationLoadedEvent event = new ConfigurationLoadedEvent(kosConf, appConf);

        @DisplayName("Should deploy web server WHEN auto-config flag is true")
        @Test void scenario1(){
            doReturn(true).when(appConf).getBoolean(eq("auto-config"), anyBoolean());

            launcher.deployWebServer(event);

            verify(vertx).deployVerticle(any(VertxWebServer.class), Mockito.<DeploymentOptions>any());
        }

        @DisplayName("Should not deploy web server WHEN auto-config flag is false")
        @Test void scenario2(){
            doReturn(false).when(appConf).getBoolean(eq("auto-config"), anyBoolean());

            launcher.deployWebServer(event);

            verify(vertx, never()).deployVerticle(any(VertxWebServer.class), Mockito.<DeploymentOptions>any());
        }
    }

    @DisplayName("Scenario: Read application configuration")
    @Nested class ReadApplicationConfiguration {

        @DisplayName("Should read application configuration and wrap it in LauncherDeploymentContext")
        @Test void scenario1(){
            val confResult = createJsonObjectResult(true);
            val confRetriever = createConfigurationRetrieverThatReturns(confResult);
            
            kosConf.setConfigRetriever(confRetriever);

            launcher.readDeploymentConfig(deploymentContext -> {
                assertEquals(kosConf, deploymentContext.getKosContext());
                assertEquals(confResult.result(), deploymentContext.getApplicationConfig());
            });
        }

        @DisplayName("Should throw exception when failed to read conf")
        @Test void scenario2(){
            val confResult = createJsonObjectResult(false);
            val confRetriever = createConfigurationRetrieverThatReturns(confResult);
            
            kosConf.setConfigRetriever(confRetriever);

            assertThrows(KosException.class, () -> {
                launcher.readDeploymentConfig(deploymentContext -> {});
            });
        }

        ConfigRetriever createConfigurationRetrieverThatReturns(AsyncResult<JsonObject> confResult){
            val confRetriever = mock(ConfigRetriever.class);
            doAnswer(ctx -> {
                Handler<AsyncResult<JsonObject>> callback = ctx.getArgument(0);
                callback.handle(confResult);
                return null;
            }).when(confRetriever).getConfig(any());
            return confRetriever;
        }

        AsyncResult<JsonObject> createJsonObjectResult(boolean hasSucceeded){
            val jsonObject = new JsonObject();
            val failure = new NullPointerException("NPE");
            
            val result = mock(AsyncResult.class);
            doReturn(jsonObject).when(result).result();
            doReturn(failure).when(result).cause();
            doReturn(hasSucceeded).when(result).succeeded();
            return result;
        }
    }
}