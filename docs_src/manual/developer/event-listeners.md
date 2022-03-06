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
  _"one will be chosen using a non-strict round-robin algorithm. When a message is received by a
  recipient, and has been handled, the recipient can optionally decide to reply to the message.
  If they do so, the reply handler will be called._

It is clear from the description above that the communication coordinated by the topic producer. Listeners
can send replies to producers, even though they will only receive the reply 

## How Kos Listeners work?
Kos abstracts this workflow using the `@Listener` annotation on a listener method. The method signature
will define how the communication will be performed between 