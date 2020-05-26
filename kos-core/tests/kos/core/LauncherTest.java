package kos.core;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import kos.api.Application;
import kos.api.ImplementationLoader;
import kos.api.ImplementationLoader.Result;
import kos.api.MutableKosConfiguration;
import kos.api.Plugin;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit: Launcher")
class LauncherTest {

    final ImplementationLoader implLoader = spy(new ImplementationLoader.SPIImplementationLoader());
    final MutableKosConfiguration kosConf = spy(new MutableKosConfiguration(implLoader));
    final Launcher launcher = spy(new Launcher(kosConf));
    final Launcher.DeploymentContext deploymentContext = mock(Launcher.DeploymentContext.class);
    
    @BeforeEach void configLog(){
        val logger = mock(Logger.class);
        doReturn(logger).when(kosConf).createLoggerFor(any());
        launcher.loadLogger();
    }

    @DisplayName("Should configure Kos and call plugins")
    @Test void scenario1(){
        val plugin = mock(Plugin.class);
        doReturn(singletonList(plugin)).when(implLoader).instancesExposedAs(eq(Plugin.class));

        launcher.run();

        verify(plugin).configure( eq(kosConf) );
    }

    @DisplayName("Should configure custom application")
    @Test void scenario2(){
        val application = mock(Application.class);
        doReturn(Result.of(application)).when(deploymentContext).instanceOf(eq(Application.class));

        launcher.deployCustomApplication(deploymentContext);

        verify(application).configure( eq(deploymentContext) );
    }

    @DisplayName("Should deploy found verticles")
    @Test void scenario3(){
        val verticles = new ArrayList<Verticle>();
        doReturn(verticles).when(deploymentContext).instancesExposedAs(eq(Verticle.class));

        launcher.deployVerticles(deploymentContext);

        verify(deploymentContext).deploy(eq(verticles));
    }

    @DisplayName("Scenario: WebServer deployment")
    @Nested class WebServerDeployment {

        final JsonObject appConf = spy(new JsonObject());

        @BeforeEach
        void setUpApplicationConfig(){
            doReturn(appConf).when(deploymentContext).getApplicationConfig();
        }

        @DisplayName("Should deploy web server WHEN kos.auto flag is true")
        @Test void scenario1(){
            doReturn(true).when(appConf).getBoolean(eq("kos.auto"), anyBoolean());

            launcher.deployWebServer(deploymentContext);

            verify(deploymentContext).deploy(any(VertxWebServer.class));
        }

        @DisplayName("Should not deploy web server WHEN kos.auto flag is false")
        @Test void scenario2(){
            doReturn(false).when(appConf).getBoolean(eq("kos.auto"), anyBoolean());

            launcher.deployWebServer(deploymentContext);

            verify(deploymentContext, never()).deploy(any(VertxWebServer.class));
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
                assertEquals(kosConf, deploymentContext.kosConfiguration);
                assertEquals(confResult.result(), deploymentContext.applicationConfig);
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