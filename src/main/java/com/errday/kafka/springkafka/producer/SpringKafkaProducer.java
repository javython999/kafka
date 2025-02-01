package com.errday.kafka.springkafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

//@SpringBootApplication
@RequiredArgsConstructor
public class SpringKafkaProducer implements CommandLineRunner {

    private static String TOPIC_NAME = "test";

    /**
     * 스프링 카프카에서 제공하는 기본 KafkaTemplate 객체로 주입된다.
     * application.yaml에 선언한 옵션값은 자동으로 주입된다.
     */
    private final KafkaTemplate<Integer, String> template;

    public static void main(String[] args) {
        SpringApplication.run(SpringKafkaProducer.class, args);
    }

    /**
     * send() 메서드를 사용해 토픽 이름과 메시지 값을 넣어 전송한다.
     * 카프카 프로듀서의 send() 메서드와 유사한 것을 확인할 수 있다.
     */
    @Override
    public void run(String... args) throws Exception {
        for (int i = 0; i < 10; i++) {
            template.send(TOPIC_NAME, "spring-kafka" + i);
        }
        System.exit(0);
    }
}
