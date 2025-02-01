package com.errday.kafka.springkafka.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ListenerContainerConfiguration {

    /**
     * KafkaListenerContainerFactory 빈 객체를 리턴하는 메서드를 생성한다.
     * 이 메서드 이름은 커스텀 리스너 컨테이너 팩토리로 선언할 때 사용한다.
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> customContainerFactory() {

        /**
         * 카프카 컨슈머를 실행할 때 필요한 옵션값들을 선언한다.
         */
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "my-kafka:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        /**
         * 컨슈머 옵션값을 파라미터로 받는 DefaultKafkaConsumerFactory 인스턴스를 생성한다.
         * DefaultConsumerFactory는 리스너 컨테이너 팩토리를 생성할 때 컨슈머 기본 옵션을 설정하는 용도로 사용된다.
         */
        DefaultKafkaConsumerFactory<Object, Object> cf = new DefaultKafkaConsumerFactory<>(props);

        /**
         * ConcurrentKafkaListenerContainerFactory는 리스너 컨테이너를 만들기 위해 사용된다.
         * 이름에서 알 수 있다시피 2개 이상의 컨슈머 리스너를 만들 때 사용되며 concurrency를 1로 설정할 경우 1개 컨슈머 스테드로 실행된다.
         */
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();

        /**
         * 리밸런스 리스너를 선언하기 위해 setConsumerRebalanceListener 메서드를 호출한다.
         * setConsumerRebalanceListener는 스프링 카프카에서 제공하는 메서드로 기존에 사용되는 카프카 컨슈머 리밸런스 리스너에 2개의 메서드를 호출한다.
         * onPartitionsRevokeBeforeCommit은 커밋이 전에 리밸런스가 발생했을 때,
         * onPartitionsRevokeAfterCommit은 커밋이 일어난 이후에 리밸런스가 발생했을 때 호출된다.
         */
        factory.getContainerProperties().setConsumerRebalanceListener(new ConsumerAwareRebalanceListener() {

            @Override
            public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {

            }

            @Override
            public void onPartitionsRevokedAfterCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {

            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

            }

            @Override
            public void onPartitionsLost(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {

            }
        });

        /**
         * 레코드 리스너를 사용함을 명시하기 위해 setBatchListener() 메서드에 false를 파라미터로 넣는다.
         * 만약 배치 리스너를 사용하고 싶다면 true를 설정하면 된다.
         */
        factory.setBatchListener(false);

        /**
         * AckMode를 설정한다.
         */
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        /**
         * 컨슈머 설정값을 가지고 있는 DefaultKafkaConsumerFactory 인스턴스를 ConcurrentKafkaListenerContainerFactory의 컨슈머 팩토리에 설정한다.
         */
        factory.setConsumerFactory(cf);
        return factory;
    }
}
