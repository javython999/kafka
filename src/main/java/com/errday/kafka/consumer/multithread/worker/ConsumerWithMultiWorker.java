package com.errday.kafka.consumer.multithread.worker;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;


@Slf4j
public class ConsumerWithMultiWorker {

    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private final static String TOPIC_NAME = "test";
    private final static String GROUP_ID = "test-group";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(GROUP_ID_CONFIG, GROUP_ID);
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, 10000);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of(TOPIC_NAME));
            ExecutorService executorService = Executors.newCachedThreadPool();

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
                for (ConsumerRecord<String, String> record : records) {
                    ConsumerWorker worker = new ConsumerWorker(record.value());
                    executorService.execute(worker);
                }
            }
        }

    }
}
