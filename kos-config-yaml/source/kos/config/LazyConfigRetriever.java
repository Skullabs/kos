package kos.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import kos.api.KosConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.val;

@RequiredArgsConstructor
class LazyConfigRetriever implements ConfigRetriever {

    final KosConfiguration kosConfiguration;

    @Delegate
    private ConfigRetriever createConfigRetriever(){
        val retrieverOptions = new ConfigRetrieverOptions();
        retrieverOptions.addStore(createStoreForProduction());
        retrieverOptions.addStore(createStoreForTest());

        return ConfigRetriever.create(kosConfiguration.getDefaultVertx(), retrieverOptions);
    }

    ConfigStoreOptions createStoreForProduction() {
        return new ConfigStoreOptions()
            .setOptional(true)
            .setType("file")
            .setFormat("yaml")
            .setConfig(new JsonObject().put("path", "conf/application.yml"));
    }

    ConfigStoreOptions createStoreForTest() {
        return new ConfigStoreOptions()
            .setOptional(true)
            .setType("file")
            .setFormat("yaml")
            .setConfig(new JsonObject().put("path", "conf/application-test.yml"));
    }
}