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

package kos.rest.sample;

import io.vertx.core.Future;
import io.vertx.core.http.*;
import kos.api.Response;
import kos.rest.*;
import lombok.*;

@RestApi({"/api/simple",""})
public interface SimpleApi {

    @GET({"/","all"})
    Future<User> retrieveUser();

    @POST void saveUser(
        @Body User user
    );

    @PUT(":id")
    void updateUser(
        @Param String id
    );

    @DELETE(":user_id")
    void deleteUser(
        @Param("user_id") String id,
        @Context HttpServerRequest httpServerRequest
    );

    @PATCH(":user_id")
    Response patchUser(
        @Param("user_id") String id,
        @Header("Authorization") String authorization,
        @Body User user
    );
}

@Value class User {}
