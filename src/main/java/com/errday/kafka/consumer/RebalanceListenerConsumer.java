package com.errday.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@Slf4j
public class RebalanceListenerConsumer {


    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private final static String GROUP_ID = "test-group";
    private static KafkaConsumer<String, String> consumer;
    private static HashMap<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();

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

        consumer = new KafkaConsumer<>(configs);
        // 컨슈머에게 토픽을 할당하기 위해 subscribe() 메서드를 사용한다.
        // Collection 타입의 String 값들을 인자로 받는다.
        // RebalanceListner를 오버라이드 변수로 포함시킨다.
        consumer.subscribe(List.of(TOPIC_NAME), new RebalanceListener());

        while (true) {
            // 컨슈머는 poll() 메서드를 호출하여 데이터를 가져와서 처리한다.
            // 지속적으로 데이터를 처리하기 위해 반복 호출을 해야 한다.
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

            // 현재 처리한 오프셋을 매번 커밋하기 위해 commitSync() 메서드가 파라미터로 받을 HashMap 타입을 선언한다.
            // 키는 토픽과 파티션 정보가 담긴 TopicPartition 클래스가 되고
            // 값은 오프셋 정보가 담긴 OffsetAndMetadata 클래스가 된다.


            // 컨슈머는 poll() 메서드를 통해 ConsumerRecord 리스트를 반환한다. poll() 메서드는 Duration 타입을 인자로 받는다.
            // 이 인자값은 브로커로부터 데이터를 가져올 때 컨슈머 버퍼에 데이터를 기다리기 위한 타임아웃 간격을 의미한다.
            for (ConsumerRecord<String, String> record : records) {
                log.info("record:{}", record);

                // 처리를 완료한 레코드의 정보를 토대로 키, 값을 설정한다.
                // 이때 주의할 점은 현재 처리한 오프셋에 1을 더한 값을 커밋해야 한다는 점이다.
                // 이후에 컨슈머가 poll()을 수행할 때 마지막으로 커밋한 오프셋부터 레코드를 리턴하기 때문이다.
                currentOffset.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
                consumer.commitSync(currentOffset);
            }
        }

    }

    private static class RebalanceListener  implements ConsumerRebalanceListener {
        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> collection) {
            log.warn("onPartitionsRevoked");
            consumer.commitSync(currentOffset);
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> collection) {
            log.warn("onPartitionsAssigned");
        }
    }

}
