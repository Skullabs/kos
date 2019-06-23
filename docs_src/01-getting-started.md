# Getting Started

!!! info
    Don't want to follow the following steps by yourself? What about downloading
    a blueprint project that you can change according to your needs?

    - **Gradle+Kotlin Project**
    [[Download](https://github.com/Skullabs/kos-sample-gradle/archive/v1.0.0.zip)]
    [[Source Code](https://github.com/Skullabs/kos-sample-gradle)]

    - **Maven+Kotlin Project**
    [[Download](https://github.com/Skullabs/kos-sample-maven/archive/v1.0.0.zip)]
    [[Source Code](https://github.com/Skullabs/kos-sample-maven)]

Kos is mostly written in Java and carefully designed to be easily integrated
other JVM languages like Kotlin and Scala. To import Kos you should include
the following libraries on your project.

```kotlin tab="Maven (pom.kts)"
dependencyManagement {
  dependencies {
    // Import the Bill of Materials
    import("io.skullabs.kos:kos-bom:${kosVersion}")
  }
}

dependencies {
  // Kos Dependencies
  compile("io.skullabs.kos:kos-core")
  compile("io.skullabs.kos:kos-injector")
  provided("io.skullabs.kos:kos-annotations")
}
```

```xml tab="Maven (pom.xml)"
<dependencyManagement>
    <dependencies>
        <!-- Import the Bill of Materials -->
        <dependency>
            <groupId>io.skullabs.kos</groupId>
            <artifactId>kos-bom</artifactId>
            <version>${version_kos}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>io.skullabs.kos</groupId>
        <artifactId>kos-core</artifactId>
    </dependency>
    <dependency>
        <groupId>io.skullabs.kos</groupId>
        <artifactId>kos-annotations</artifactId>
    </dependency>
</dependency>
```

```kotlin tab="Gradle (kts)"
dependencies {
    // Import the Bill of Materials
    implementation(platform("io.skullabs.kos:kos-bom:${version_kos}"))

    implementation("io.skullabs.kos:kos-core")
    compileOnly("io.skullabs.kos:kos-annotations")
}
```

Now let's create a simple HelloWorld API.

```kotlin tab="Kotlin"
@Path("hello")
class HelloWorldApi {

  @GET("world")
  fun sayHello() = "Hello World"
}
```

```java tab="Java"
@Path("hello")
class HelloWorldApi {

  @GET("world")
  String sayHello() {
    return "Hello World";
  }
}
```

Finally, we have to bundle all dependencies together and generate a runnable jar with them.

```kotlin tab="Maven (pom.kts)"
plugins {
  plugin("org.apache.maven.plugins:maven-shade-plugin:3.2.0") {
    executions {
      execution(id = "default-package", phase = "package", goals = listOf("shade"))
    }
    configuration {
      "createDependencyReducedPom" to true
      "dependencyReducedPomLocation" to "\${project.build.directory}/pom-reduced.xml"
      "transformers" {
        "org.apache.maven.plugins.shade.resource.ServicesResourceTransformer" {}
        "org.apache.maven.plugins.shade.resource.ManifestResourceTransformer" {
          "manifestEntries" {
            "Main-Class" to launcherClass
          }
        }
      }
    }
  }
}
```

```xml tab="Maven (pom.xml)"
<plugin>
  <artifactId>maven-shade-plugin</artifactId>
  <version>3.2.0</version>
  <executions>
    <execution>
      <id>default-package</id>
      <phase>package</phase>
      <goals>
        <goal>shade</goal>
      </goals>
      <configuration>
        <createDependencyReducedPom>true</createDependencyReducedPom>
        <dependencyReducedPomLocation>${project.build.directory}/pom-reduced.xml</dependencyReducedPomLocation>
        <transformers>
          <org.apache.maven.plugins.shade.resource.ServicesResourceTransformer />
          <org.apache.maven.plugins.shade.resource.ManifestResourceTransformer>
            <manifestEntries>
              <Main-Class>kos.core.Launcher</Main-Class>
            </manifestEntries>
          </org.apache.maven.plugins.shade.resource.ManifestResourceTransformer>
        </transformers>
      </configuration>
    </execution>
  </executions>
</plugin>
```

```kotlin tab="Gradle (kts)"
// You can use either Shadow or VertX plugin to generate a fat jar
// We've picked VertX in this example
plugins {
    id("io.vertx.vertx-plugin") version "1.0.1"
}

vertx {
    launcher = launcherClass
}
```

_Voila_! Our first Kos software is ready. Let's run it?
```shell
$ java -jar my-kos-app.jar
```
