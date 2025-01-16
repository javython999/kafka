package com.errday.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class AsyncCommitCallbackConsumer {


    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private final static String GROUP_ID = "test-group";

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // 컨슈머 그룹 이름을 선언한다. 컨슈머 그룹을 통해 컨슈머의 목적을 구분할 수 있다.
        // 컨슈머 그룹을 기준으로 컨슈머 오프셋을 관리하기 때문에 subscribe() 메서드를 사용하여 토픽을 구독하는 경우에는 컨슈머 그룹을 선언해야 한다.
        // 컨슈머가 중단되거나 재시작되더라도 컨슈머 그룹의 컨슈머 오프셋을 기준으로 이후 데이터를 처리하기 때문이다.
        // 컨슈머 그룹을 선언하지 않으면 어떤 그룹에도 속하지 않는 컨슈머로 동작하게 된다.
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);

        // 프로듀서가 직렬화하여 전송한 데이터를 역직렬화하기 위해 역직렬화 클래스를 지정한다.
        // 프로듀서에서 직렬화한 타입으로 역직렬화해야 한다.
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // 명시적으로 오프셋 커밋을 수행할 때는 ENABLE_AUTO_COMMIT_CONFIG을 false로 설정한다.
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs)) {
            // 컨슈머에게 토픽을 할당하기 위해 subscribe() 메서드를 사용한다.
            // Collection 타입의 String 값들을 인자로 받는다.
            consumer.subscribe(List.of(TOPIC_NAME));

            while (true) {
                // 컨슈머는 poll() 메서드를 호출하여 데이터를 가져와서 처리한다.
                // 지속적으로 데이터를 처리하기 위해 반복 호출을 해야 한다.
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                // 컨슈머는 poll() 메서드를 통해 ConsumerRecord 리스트를 반환한다. poll() 메서드는 Duration 타입을 인자로 받는다.
                // 이 인자값은 브로커로부터 데이터를 가져올 때 컨슈머 버퍼에 데이터를 기다리기 위한 타임아웃 간격을 의미한다.
                for (ConsumerRecord<String, String> record : records) {
                    log.info("record:{}", record);
                }
                // poll() 메서드로 받은 가장 마지막 레코드의 오프셋을 기준으로 커밋한다.
                consumer.commitAsync(
                        (map, e) -> {
                            if (e != null) {
                                log.error("Commit failed");
                                log.error(e.getMessage(), e);
                            } else {
                                log.info("commit success");
                                log.info(map.toString());
                            }
                        }
                );
            }
        }
    }
}
