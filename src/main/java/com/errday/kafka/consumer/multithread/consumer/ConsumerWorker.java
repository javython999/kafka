package com.errday.kafka.consumer.multithread.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ConsumerWorker implements Runnable {

    private Properties props;
    private String topic;
    private String threadName;
    private KafkaConsumer<String, String> consumer;


    /**
     *  KafkaConsumer 인스턴스를 생성하기 위해 필요한 변수를 컨슈머 스레드 생성자 변수로 받는다.
     */
    public ConsumerWorker(Properties props, String topic, int number) {
        this.props = props;
        this.topic = topic;
        this.threadName = "consumer-thread-" + number ;
    }

    @Override
    public void run() {
        /**
         * KafkaConsumer 클래스는 스레드 세이프하지 않다.
         * 이 때문에 스레드별로 KafkaConsumer 인스턴스를 별개로 만들어 운영한다.
         * KafkaConsumer 인스턴스를 여러 스레드에서 실행하면 ConcurrentModificationException 예외가 발생한다.
         */
        consumer = new KafkaConsumer<>(props);

        /**
         * 생성자에서 받은 토픽을 명시적으로 구독한다.
         */
        consumer.subscribe(List.of(topic));

        /**
         * poll 메서드를 통해 리턴받은 레코드들을 처리한다.
         */
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1));
            for (ConsumerRecord<String, String> record : records) {
                log.info("{} - {}", threadName, record.toString());
            }
        }
    }
}
