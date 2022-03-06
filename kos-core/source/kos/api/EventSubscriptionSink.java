package kos.api;

/**
 * Defines how to listen message from externally managed brokers (e.g. AWS' SQS/SNS, Google PubSub,
 * Apache Kafka topics, RabbitMQ queues, etc), allowing library maintainers to use Vert.x's EventBus
 * as a bridge with the application layer.
 */
public interface EventSubscriptionSink extends EventBusSink {

}