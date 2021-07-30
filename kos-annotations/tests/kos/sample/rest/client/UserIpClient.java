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

package kos.sample.rest.client;

import io.vertx.core.Future;
import kos.rest.*;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestClient
public interface UserIpClient {

    @GET("/users/:id")
    Future<Map<String, Object>> retrieveUserAsMapById(
        @Param("id") int id
    );

    @GET("/users/:id")
    Future<User> retrieveUserById(
        @Param("id") int id
    );

    @GET("/users")
    Future<List<User>> retrieveUsers(
        @Header("If-Modified-Since") long timestamp
    );

    @POST("/users")
    Future<Void> createUser(
        @Header("X-Custom-Signature") long hash,
        @Body User user
    );

    @PUT("/users/:id")
    Future<Void> updateUser(
        @Param("id") int id,
        @Header("X-Custom-Signature") long hash,
        @Body User user
    );

    @PATCH("/users/:id")
    Future<Void> updateUser(
        @Param("id") int id,
        @Body User user
    );

    @DELETE("/users/:id")
    Future<Void> deleteUser(
        @Param("id") int id
    );
}

@Value class User {
    UUID id;
}



