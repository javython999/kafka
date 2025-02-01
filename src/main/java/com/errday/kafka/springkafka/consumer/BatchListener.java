package com.errday.kafka.springkafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.boot.SpringApplication;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;

@Slf4j
//@SpringBootApplication
public class BatchListener {

    public static final String TOPIC_NAME = "test";

    public static void main(String[] args) {
        SpringApplication.run(BatchListener.class, args);
    }

    /**
     *  컨슈머 레코드 묶음을 파라미터로 받는다.
     *  카프카 클라이언트 라이브러리에서 poll() 메서드로 리턴 받은 ConsumerRecords를 리턴받아 사용하는 것과 동일하다.
     */
    @KafkaListener(topics = TOPIC_NAME, groupId = "test-group-01")
    public void batchListener(ConsumerRecords<String, String> records) {
        records.forEach(record -> {
            log.info("batchListener(ConsumerRecords) - {}", record);
        });
    }

    /**
     *  메시지 값들을 List 자료구조로 받아서 처리한다.
     */
    @KafkaListener(topics = TOPIC_NAME, groupId = "test-group-02")
    public void batchListener(List<String> records) {
        records.forEach(recordValue -> {
            log.info("batchListener(List) - {}", recordValue);
        });
    }

    /**
     *  2개 이상의 컨슈머 스레드로 배치 리스너를 운영할 경우에는 concurrency 옵션을 함께 선언하여 사용한다.
     */
    @KafkaListener(topics = TOPIC_NAME, groupId = "test-group-03", concurrency = "3")
    public void concurrentBatchListener(ConsumerRecords<String, String> records) {
        records.forEach(record -> {

           log.info("concurrentBatchListener(ConsumerRecords) - thread - {} - {}", Thread.currentThread().getName(), record);
        });
    }
}
