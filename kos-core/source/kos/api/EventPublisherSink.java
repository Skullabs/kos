package kos.api;

/**
 * Defines an EventBus publisher. It allows library maintainers to
 * use Vert.x's EventBus as a bridge between the application layer and
 * externally managed message brokers (e.g. AWS' SQS/SNS, Google PubSub,
 * Apache Kafka topics, RabbitMQ queues, etc).
 */
@SuppressWarnings("rawtypes")
public interface EventPublisherSink extends EventBusSink {

}