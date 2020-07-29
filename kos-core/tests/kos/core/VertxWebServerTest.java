/*
 * Copyright 2019 Skullabs Contributors (https://github.com/skullabs)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kos.core;

import io.vertx.core.Context;
import io.vertx.core.json.JsonObject;
import kos.api.MutableKosConfiguration;
import kos.api.PayloadSerializationStrategy;
import kos.api.Response;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static io.vertx.core.http.HttpMethod.GET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@DisplayName("VertxWebServer: serve requests")
class VertxWebServerTest {

    final MutableKosConfiguration kosConfiguration = new MutableKosConfiguration();
    final VertxWebServer server = new VertxWebServer(kosConfiguration);

    @Mock Context verticleContext;
    @Mock JsonObject config;

    @BeforeEach void simulateVerticleDeployment(){
        kosConfiguration.getHttpServerOptions().setPort(9001);
        kosConfiguration.getAvailablePayloadStrategies().useSerializerForContentType("text/plain");
        MockitoAnnotations.initMocks(this);
        doReturn(config).when(verticleContext).config();
        server.init(kosConfiguration.getDefaultVertx(), verticleContext);
    }

    @SneakyThrows
    @Test void canReceiveRequests(){
        server.router().route(GET, "/hello", ctx -> Response.send(kosConfiguration, ctx, "World"));

        server.start();
        Thread.sleep(500);

        val response = sendGET("http://localhost:9001/hello");
        assertEquals("World", response);
    }

    @SneakyThrows
    @AfterEach void stopServer(){
        server.stop();
    }

    @SneakyThrows
    static String sendGET(String url) {
        val conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");

        val buffer = new StringBuilder();
        try (val in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
        }

        return buffer.toString();
    }
}