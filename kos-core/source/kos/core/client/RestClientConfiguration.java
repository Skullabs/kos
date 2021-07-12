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

import io.vertx.core.MultiMap;
import io.vertx.ext.web.client.WebClient;
import kos.api.KosContext;
import kos.core.exception.KosException;
import kos.api.StringConverter;
import lombok.*;

import java.net.MalformedURLException;
import java.net.URL;

import static kos.core.Lang.firstNotNull;

@Value
@Builder(
    builderMethodName = "defaults",
    builderClassName = "MutableRestClientConfiguration"
)
public class RestClientConfiguration {

    public static final RestClientConfiguration EMPTY =
            RestClientConfiguration.withUrl("http://empty.url").build();

    @NonNull final URL url;
    @Builder.Default @NonNull final MultiMap headers = MultiMap.caseInsensitiveMultiMap();
    final RestClientSerializer restClientSerializer;
    final WebClient client;
    final StringConverter stringConverter;

    public boolean isEmpty() {
        return url == null
            || restClientSerializer == null
            || client == null
            || stringConverter == null;
    }

    public RestClientConfiguration useDefaultsForNullProperties(KosContext kosContext) {
        if (!isEmpty()) return this;

        return new RestClientConfiguration(
            url, headers,
            firstNotNull(restClientSerializer, kosContext.getDefaultRestClientSerializer()),
            firstNotNull(client, kosContext.getDefaultVertxWebClient()),
            firstNotNull(stringConverter, kosContext.getStringConverter())
        );
    }

    @Getter(lazy = true)
    private final boolean isHttps = getUrl().getProtocol().equals("https");

    @Getter(lazy = true)
    private final int port = loadPort();

    @Getter(lazy = true)
    private final String host = getUrl().getHost();

    @Getter(lazy = true)
    private final String baseUrl = loadBaseUrl();

    private int loadPort(){
        int port = url.getPort();
        if (port > 0)
            return port;
        else if (isHttps())
            return 443;
        else
            return 80;
    }

    private String loadBaseUrl(){
        val path = getUrl().getPath();
        if ("".equals(path))
            return "/";
        else if (path.startsWith("/"))
            return path;
        else
            return "/" + path;
    }

    public static MutableRestClientConfiguration withUrl(String url) {
        try {
            return defaults().url(new URL(url));
        } catch (MalformedURLException e) {
            throw new KosException(e);
        }
    }
}