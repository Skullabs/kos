# Reading Configuration File
As stated by its [documentation](https://vertx.io/docs/vertx-config/java/), Vert.x provides the
`vertx-config` module to efficiently interact with configuration files. Internally,
this module relies on the **Config Retriever** and **Configuration store** concepts, defining
"a location from where the configuration data is read and also a format (JSON by default)."

As an attempt to simplify this process, Kos made the following design choices:

- it only looks after files named `application.yml` in the classpath. In case
  more than one is found, they will be merged before being used.
- it fully executes the above by default, but allows one to change the default behaviour (e.g. using a
  different _Configuration Retriever_)
- Once the configuration is read, you will have access to an `JsonObject` - just like you'd have on a
  typical Vert.x application.

## Reading the configuration object
The easiest way to interaction with the read configuration would be through Dependency Injection.
You will have full access to [Kos Context](../../architecture/kos-context/), which will expose the
read configuration file (Vert.x's `JsonObject`).

=== "Kotlin"
    ```kotlin
    @Singleton
    class MyServerConfiguration(
        private val kosContext: KosContext
    ) {

        val dbHost = kosContext.applicationConfig.getString("db.host", "localhost")
        val dbPort = kosContext.applicationConfig.getString("db.port", "5432")
        val dbUser = kosContext.applicationConfig.getString("db.user", "postgres")
        val dbPass = kosContext.applicationConfig.getString("db.pass", "postgres")
    }
    ```

=== "Java"
    ```java
    @Singleton
    class MyServerConfiguration {

        private final KosContext kosContext;

        public MyServerConfiguration(KosContext kosContext){
            this.kosContext = kosContext;
        }

        public String getDbHost() {
            return kosContext.getApplicationConfig().getString("db.host", "localhost");
        }

        public String getDbPort() {
            return kosContext.getApplicationConfig().getString("db.port", "5432");
        }

        public String getDbUser() {
            return kosContext.getApplicationConfig().getString("db.user", "postgres");
        }

        public String getDbPass() {
            return kosContext.getApplicationConfig().getString("db.pass", "postgres");
        }
    }
    ```

Another option would be listening to Kos' [internal events](../architecture/internal-events/).