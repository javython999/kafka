package com.errday.kafka.streams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Properties;

@Slf4j
public class KstreamKtableJoin {

    private static String APPLICATION_NAME = "order-join-application";
    private static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private static String ADDRESS_TABLE = "address";
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

        // KTable과 KStream을 생성한다.
        KTable<String, String> addressTable = builder.table(ADDRESS_TABLE);
        KStream<String, String> orderStream = builder.stream(ORDER_STREAM);

        // 조인을 위해 KStream 인스턴스에 정의되어 있는 join() 메서드를 사용한다. 첫번째 파라미터로 조인을 수행할 KTable 인스턴스를 넣는다.
        // KStream과 KTable에서 동일한 메시지 키를 가진 데이터를 찾았을 경우 각각의 메시지 값을 조합해서 어떤 데이터를 만들지 결정한다.
        // 조인을 통해 성생된 데이터를 order_join 토픽에 저장하기 위해 to() 싱크 프로세서를 사용 한다.
        orderStream.join(addressTable, (orderValue, addressValue) -> orderValue + " send to " + addressValue)
                .to(ORDER_JOIN_STREAM);

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }
}
