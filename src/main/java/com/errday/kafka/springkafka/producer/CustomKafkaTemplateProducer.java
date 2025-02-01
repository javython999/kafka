package com.errday.kafka.springkafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@RequiredArgsConstructor
public class CustomKafkaTemplateProducer implements CommandLineRunner {

    private static String TOPIC_NAME = "test";

    /**
     *
     */
    private final KafkaTemplate<String, String> customKafkaTemplate;

    public static void main(String[] args) {
        SpringApplication.run(CustomKafkaTemplateProducer.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        CompletableFuture<SendResult<String, String>> future = customKafkaTemplate.send(TOPIC_NAME, "customKafkaTemplate - " + LocalDateTime.now().format(formatter));

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("Message sent successfully: " + result.getProducerRecord().value());
            } else {
                System.err.println("Message failed to send: " + ex.getMessage());
            }
        }).join();
    }
}
