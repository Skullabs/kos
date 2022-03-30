---
template: single-topic.html
title: Getting Started
---

# Getting Started

!!! info
    Don't want to follow the following steps by yourself? What about downloading
    a blueprint project that you can change according to your needs?

    - **Gradle+Kotlin Project**
    [[Download](https://github.com/Skullabs/kos-sample-gradle/archive/refs/tags/v0.8.1.zip)]
    [[Source Code](https://github.com/Skullabs/kos-sample-gradle/)]

Kos is mostly written in Java and carefully designed to be easily integrated
other JVM languages like Kotlin and Scala. To import Kos you should include
the following libraries on your project.
=== "Gradle (kts)"
    ```kotlin
    dependencies {
        // Import the Bill of Materials
        implementation(platform("io.skullabs.kos:kos-bom:${version_kos}"))

        implementation("io.skullabs.kos:kos-core")
        compileOnly("io.skullabs.kos:kos-annotations")
    }
    ```
=== "Maven (pom.kts)"
    ```kotlin
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
=== "Maven (pom.xml)"
    ```xml 
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

Now let's create a simple HelloWorld API.
=== "Kotlin"
    ```kotlin
    @Path("hello")
    class HelloWorldApi {
    
      @GET("world")
      fun sayHello() = "Hello World"
    }
    ```
=== "Java"
    ```java
    @Path("hello")
    class HelloWorldApi {
    
      @GET("world")
      String sayHello() {
        return "Hello World";
      }
    }
    ```

Finally, we have to bundle all dependencies together and generate a runnable jar with them.
=== "Gradle (kts)"
    ```kotlin
    // You can use either Shadow or VertX plugin to generate a fat jar
    // We've picked VertX in this example
    plugins {
        id("io.vertx.vertx-plugin") version "1.2.0"
    }
    
    vertx {
        launcher = launcherClass
    }
    ```
=== "Maven (pom.kts)"
    ```kotlin
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
=== "Maven (pom.xml)"
    ```xml
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

_Voila_! Our first Kos software is ready. Let's run it?
=== "Gradle"
    ```shell
    $ java -jar build/libs/my-kos-app-all.jar
    ```
=== "Maven"
    ```shell
    $ java -jar target/my-kos-app.jar
    ```
