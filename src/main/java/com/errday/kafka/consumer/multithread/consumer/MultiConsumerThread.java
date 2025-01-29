package com.errday.kafka.consumer.multithread.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class MultiConsumerThread {

    private final static String BOOTSTRAP_SERVER = "my-kafka:9092";
    private final static String TOPIC_NAME = "test";
    private final static String GROUP_ID = "test-group";
    private final static int CONSUMER_COUNT = 3;

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            ConsumerWorker consumerWorker = new ConsumerWorker(props, TOPIC_NAME, i);
            executorService.execute(consumerWorker);
        }
    }
}
