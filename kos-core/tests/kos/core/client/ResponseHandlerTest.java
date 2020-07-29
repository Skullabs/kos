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

package kos.core.client;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ResponseHandlerTest {

    Buffer buffer = readResourceFile("tests-resources/response-handler-scenario-01.json");
    RestClientSerializer restClientSerializer = new RestClientSerializer.JsonRestClientSerializer();
    UUID expectedUserId = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Mock HttpResponse<Buffer> response;

    @BeforeEach
    void setupMocks(){
        doReturn(buffer).when(response).body();
    }

    @DisplayName("SHOULD be able to deserialize into Map")
    @Test void mapSerialization(){
        val mapType = new TypeReference<Map<String, Object>>(){};
        val mapResponseHandler = ResponseHandler.create(restClientSerializer, mapType);

        val user = mapResponseHandler.deserialize(response);
        assertEquals(expectedUserId.toString(), user.get("id"));
    }

    @DisplayName("SHOULD be able to deserialize into User class")
    @Test void classSerialization(){
        val classResponseHandler = ResponseHandler.create(restClientSerializer, User.class);
        val user = classResponseHandler.deserialize(response);
        assertEquals(expectedUserId, user.getId());
    }

    @SneakyThrows
    Buffer readResourceFile(String fileName) {
        val path = Paths.get(fileName);
        val bytes = Files.readAllBytes(path);
        return Buffer.buffer(bytes);
    }
}

@Value class User {
    UUID id;
}