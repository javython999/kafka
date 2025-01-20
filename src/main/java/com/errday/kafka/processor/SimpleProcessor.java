package com.errday.kafka.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.util.Properties;

@Slf4j
public class SimpleProcessor {

    private static String APPLICATION_NAME = "processor-application";
    private static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private static String STREAM_LOG = "stream_log";
    private static String STREAM_LOG_FILTER = "stream_log_filter";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // Topology 클래스는 프로세서 API를 사용한 토폴로지를 구성하기 위해 사용된다.
        Topology topology = new Topology();

        // stream_log 토픽을 소스 프로세서로 가져오기 위해 addSource() 메서드를 사용했다.
        // addSource() 메서드의 첫 번째 파라미터에는 소스 프로세서의 이름을 입력하고 두 번째 파라미터는 대상 토픽 이름을 입력한다.
        // 스트림 프로세서를 사용하기 위해 addProcessor() 메서드를 사용했다.
        // addProcessor() 메서드의 첫 번째 파라미터에는 스트림 프로세서의 이름을 입력한다. 두 번째 파라미터는 사용자가 정의한 프로세서 인스턴스를 입력한다. 세 번째 파라미터는 부모 노드를 입력해야 하는데 여기서 부모 노드는 Source이다.
        // STREAM_LOG_FILTER를 싱크 프로세서로 사용하여 데이터를 저장하기 위해 addSink() 메서드를 사용했다.
        // 첫 번째 파라미터는 싱크 프로세서의 이름을 입력한다. 두 번째 파라미터는 저장할 토픽의 일므을 입력한다. 세 번째 파라미터는 부모 노드를 입력하는데 필터링 처리가 완료된 데이터를 저장해야 하므로 부모 노드는 Processor이다.
        topology.addSource("Source", STREAM_LOG)
                .addProcessor("Process", FilterProcessor::new, "Source")
                .addSink("Sink", STREAM_LOG_FILTER, "Process");


        // 작성 완료한 topology 인스턴스를 kafkaStreams 인스턴스의 파라미터로 넣어서 스트림을 생성하고 실행할 수 있다.
        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();
    }
}
