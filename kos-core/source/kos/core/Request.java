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

import io.vertx.core.buffer.*;
import io.vertx.ext.web.*;
import kos.api.KosContext;
import lombok.*;
import lombok.experimental.*;

/**
 * Methods to read and parse data from the incoming request.
 *
 * <b>Note</b>: As these methods have been mainly created for internal API use, we strongly
 * discourage developers to use these methods as it may change without further notice.
 */
@UtilityClass
@SuppressWarnings({"unchecked", "unused"})
public class Request {

    public <T> T readParam(KosContext kosContext, RoutingContext context, String name, Class<T> type) {
        val param = context.request().getParam(name);
        return kosContext.getStringConverter().convertTo(type, param);
    }

    public <T> T readHeader(KosContext kosContext, RoutingContext context, String name, Class<T> type) {
        val header = context.request().getHeader(name);
        return kosContext.getStringConverter().convertTo(type, header);
    }

    public <T> T readBody(KosContext kosContext, RoutingContext context, String name, Class<T> type) {
        val buffer = context.getBody();
        if (Buffer.class.equals(type))
            return (T) buffer;
        val serializer = kosContext.getPayloadSerializationStrategy().serializerFor(context.request());
        return serializer.deserialize(buffer, type);
    }

    public <T> T readContext(KosContext kosContext, RoutingContext context, String name, Class<T> type) {
        return (T) context.get(name);
    }
}
