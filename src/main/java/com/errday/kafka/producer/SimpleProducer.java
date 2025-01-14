package com.errday.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

@Slf4j
public class SimpleProducer {

    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        // 메시지 키, 메시지 값을 직렬화하기 위한 직렬화 클래스를 선언한다.
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);

        String messageValue = "testMessage";
        // 카프카 브로커로 데이터를 보내기 위해 ProducerRecord를 생성한다.
        // 메시지 키는 따로 설정하지 않아 null로 전송된다.
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, messageValue);

        // 생성한 ProducerRecord를 전송하기 위해 record를 파라미터로 가지는 send() 메서드를 호출한다.
        // send()는 즉각적인 전송을 뜻하는 것이 아니라, 파라미터로 들어간 record를 프로듀서 내부에 가지고 있다가 배치 형태로 묶어서 브로커에 전송한다.
        // 이러한 전송방식을 '배치 전송'이라고 부른다.
        producer.send(record);
        log.info("{}", record);

        // flush()를 통해 프로듀서 내부 버퍼에 가지고 있는 레코드 배치를 브로커로 전송한다.
        producer.flush();

        // 애플리케이션을 종료하기 전에 리소스를 반환한다.
        producer.close();
    }
}
