# Event Listeners
The [event bus](https://vertx.io/docs/vertx-core/java/#event_bus) is the nervous system of Vert.x.
It acts as broker, where **messages** are sent on the event bus to an **address**. Listeners on
these addresses can react to the incoming messages and perform bespoke code (the so-called **handlers**).

Vert.x supports the following messaging patterns:

- [Publish/Subscribe](https://vertx.io/docs/vertx-core/java/#_publish_subscribe_messaging) -
  This notification pattern allows one to have multiple listeners for each subscription address.
  The communication happens in uni-directionally from the publisher to all the listeners.
- [Point-to-point](https://vertx.io/docs/vertx-core/java/#_point_to_point_and_request_response_messaging) -
  Here messages will be delivered to only one subscriber per address. If multiple listeners are registered,
  _"one will be chosen to use a non-strict round-robin algorithm. When a message is received by a
  recipient, and has been handled, the recipient can optionally decide to reply to the message.
  If they do so, the reply handler will be called"._

Kos abstracts the Vertx' EventBus API providing a simple annotation based convention. The simplicity
of its design comes at a cost: it only supports the Publish/Subscribe pattern.

## How Kos Listeners work?
Kos abstracts this workflow using the `@Listener` annotation on a listener method. Here are the restrictions
imposed on these methods:

- it must return either the JVM's **void** or `io.vertx.core.Future<Void>`. `Future` is preferred when
  performing I/O calls.
- it must expect exactly one parameter
- it cannot be a constructor
- the address defined in the `@Listener` annotation must not be empty
- it must not block the Event Loop 

Here is an example of a listener that listens and consumes for the event `UserDeletedEvent`.

=== "Kotlin"
    ```kotlin
    @Singleton
    class UserEventListener {
        
        @Listener("user::deleted")
        fun on(event: UserDeletedEvent) {
            println("User ${event.userId} has been deleted.")
        }
    }
    ```
=== "Java"
    ```java
    @Singleton
    class UserEventListener {

        @Listener("user::deleted")
        void on(UserDeletedEvent event) {
            System.out.println("User ${event.userId} has been deleted.")
        }
    }
    ```

## Publishing Events
Unlike listener methods, publisher methods must only be defined on interface methods. Defined by 
annotating a method with the `@Publisher` annotation, Kos will generate a concrete class for your
interface. Restrictions:

- it must return `io.vertx.core.Future<Void>`, as it will perform an I/O operation
- it must expect exactly one parameter
- the address defined in the `@Publisher` annotation must not be empty
- it must not block the Event Loop


=== "Kotlin"
    ```kotlin
    interface UserEventPublisher {
        
        @Publisher("user::deleted")
        fun trigger(event: UserDeletedEvent)
    }
    ```
=== "Java"
    ```java
    class UserEventPublisher {

        @Publisher("user::deleted")
        void trigger(UserDeletedEvent event);
    }
    ```

## Clustering the EventBus
Vert.x provides EventBus' clustering capabilities out-of-box. Kos will respect any clustering
configuration if you manually define it - this can be achievable by writing a custom
[Plugin](../kos-plugins/).

## Bridging EventBus to a remote broker
Kos encourage developers to use the Event Listener/Publisher API to communicate with a remote
broker (e.g. ApacheMQ, Apache Kafka, AWS SQS, GCP PubSub, etc.). To leverage such capability,
Kos provides a `Sink` mechanism, allowing one to relay the messages to a remote broker and vice-versa.
It's comprised of two interfaces - `EventPublisherSink` and `EventSubscriberSink`. Here are a few
guidelines:

1. Both interfaces will expect `EventBusSink.Result` as return type.
2. It is expected that `Sink` classes will choose which `address` will be handled and which will be ignored.
3. To ignore a given address, one must return `EventBusSink.Result.NOT_ATTEMPTED`
4. In case of failure, please do not throw an exception. Return `EventBusSink.Result.failure(Throwable)` instead.
5. If your `Sink` decides to handle this particular address, make sure to rewrite the original address, creating a dedicated one for each case - one for the _listener_ and one for the _producer_.
6. The `EventSubscriberSink` must ensure that any message received from the remote broker will be sent to the newly defined _listener address_.
7. The `EventPublisherSink` must ensure that message sent to the newly created _producer address_ will be relayed to the remote broker.
8. The newly created address can be defined by returning `EventBusSink.Result.succeededAtAddress(String)`.

If everything was implemented as expected, `@Listener`s and `@Publisher`s methods will transparently communicate
with the remote broker.