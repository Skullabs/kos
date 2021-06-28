# Configuring your application
As any other Kos component, _Kos configuration_ is an automated mechanism that
relies on Vert.x's core api to read Yaml configuration files. This means that once
the configuration is read you will have a JsonObject which you can interact with
and read the desired configuration property. 

By default, Kos will for a file called `application.yml` in the classpath. In case
more than one is found, all `application.yml` found in the classpath will be merged
before being used.

To access your configuration you must listen to any [triggered WebServer events](../12-internal-deployment-events/#web-server-events).
