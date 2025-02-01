package com.errday.kafka.springkafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@SpringBootApplication
public class CustomConsumerListenerContainer {

    private static final String TOPIC_NAME = "test";


    public static void main(String[] args) {
        SpringApplication.run(CustomConsumerListenerContainer.class, args);
    }

    /**
     * 빈 객체로 등록한 이름인 customContainerFactory를 옵션값으로 설정하면 커스텀 컨테이너 팩토리로 생성된 커스텀 리스너 컨테이너를 사용할 수 있다.
     */
    @KafkaListener(topics = TOPIC_NAME, groupId = "test-group", containerFactory =  "customContainerFactory")
    public void listen(ConsumerRecord<String, String> record) {
        log.info("customContainerFactory - {}", record);
    }

}
