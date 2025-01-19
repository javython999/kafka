package com.errday.kafka.streams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

@Slf4j
public class KstreamGlobalKtableJoin {

    private static String APPLICATION_NAME = "order-join-application";
    private static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private static String ADDRESS_TABLE = "address_v2";
    private static String ORDER_STREAM = "order";
    private static String ORDER_JOIN_STREAM = "order_join";

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

        // GlobalKTable과 KStream을 생성한다.
        GlobalKTable<String, String> addressGlobalTable = builder.globalTable(ADDRESS_TABLE);
        KStream<String, String> orderStream = builder.stream(ORDER_STREAM);

        // 첫번째 파라미터에는 GlobalKTable 인스턴스를 입력한다.
        // GlobalKTable은 KTable의 조인과 다르게 레코드를 매칭할 때 KStream의 메시지 키와 메시지 값 둘다 사용할 수 있다.
        // 여기서는 KStream의 메시지 키를 GlobalKTable의 메시지 키와 매칭하도록 설정했다.
        orderStream.join(addressGlobalTable, (orderKey, orderValue) -> orderKey, (orderValue, addressValue) -> orderValue + " send to " + addressValue)
                .to(ORDER_JOIN_STREAM);


        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }
}
