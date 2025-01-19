package com.errday.kafka.streams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

@Slf4j
public class FilterStream {

    private static String APPLICATION_NAME = "streams-application";
    private static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private static String STREAM_LOG = "stream_log";
    private static String STREAM_LOG_FILTER = "stream_log_filter";

    public static void main(String[] args) {
        Properties props = new Properties();

        // 스트림즈 애플리케이션은 애플리케이션 아이디를 지정해야 한다.
        // 애플리케이션 아이디 값을 기준으로 병렬처리하기 때문이다.
        // 기존에 사용하지 않은 이름을 아이디로 사용해야 한다.
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);

        // 스트림즈 애플리케이션과 연동할 카프카 클러스터 정보를 입력한다.
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // 스트림 처리를 위한 메시지 키와 값의 역직렬화, 직렬화 방식을 지정한다.
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // StreamBuilder는 스트림 토폴로지를 정의하기 위한 용도로 사용된다.
        StreamsBuilder builder = new StreamsBuilder();

        // stream_log 토픽으로부터 KStream 객체를 만들기 위해 StreamBuilder의 stream() 메서드를 사용했다.
        // StreamBuilder는 stream() 외에 KTable을 만드는 table(), GlobalKTable을 만드는 globalTable() 메서드를 지원한다.
        // stream(), table(), globalTable() 메서드들은 최초의 토픽 데이터를 가져오는 소스 프로세서 이다.
        KStream<String, String> streamLog = builder.stream(STREAM_LOG);

        // 데이터를 필터링하는 filter() 메서드는 자바의 함수형 인터페이스인 Predicate를 파라미터로 받는다.
        // Predicate는 함수형 인터페이스로 특정 조건을 표현할 때 사용할 수 있는데, 여기서는 메시지 키와 메시지 값에 대한 조건을 나타낸다.
        KStream<String, String> filteredStream = streamLog.filter((key, value) -> value.length() > 5);

        // 필터링된 KStream을 stream_log_filter 토픽에 저장하도록 소스프로세스를 작성하였다.
        filteredStream.to(STREAM_LOG_FILTER);

        // StreamBuilder로 정의한 토폴로지에 대한 정보와 스트림즈 실행을 위한 기본 옵션을 파라미터로 KafkaStreams 인스턴스를 생성한다.
        // KafkaStreams 인스턴스를 실행하려면 start() 메서드를 사용하면된다.
        // 이 스트림 애플리케이션은 stream_log 토픽의 데이터를 stream_log_copy 토픽으로 전달한다.
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }
}
