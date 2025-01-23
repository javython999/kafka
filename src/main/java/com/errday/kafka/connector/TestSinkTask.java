package com.errday.kafka.connector;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.util.Collection;
import java.util.Map;

public class TestSinkTask extends SinkTask {


    /**
     * 태스크의 버전을 지정한다.
     * 보통 커넥터의 version() 메서드에서 지정한 버전과 동일한 버전으로 작성하는 것이 일반적이다.
     */
    @Override
    public String version() {
        return "";
    }

    /**
     * 태스크가 시작할 때 필요한 로직을 작성한다.
     * 태스크는 실질적으로 데이터를 처리하는 역할을 하므로 데이터 처리에 필요한 리소스를 여기서 초기화 한다.
     */
    @Override
    public void start(Map<String, String> map) {

    }

    /**
     * 싱크 애플리케이션 또는 싱크 파일에 저장할 데이터를 토픽에서 주기적으로 가져오는 메서드이다.
     * 토픽의 데이터들은 여러 개 의 SinkRecord로 묶어 파라미터로 사용할 수 있다.
     * SinkRecord는 토픽의 한 개 레코드이며 토픽, 파티션, 타임스탬프 등의 정보를 담고 있다.
     */
    @Override
    public void put(Collection<SinkRecord> collection) {

    }

    /**
     * put() 메서드를 통해 가져온 데이터를 일정 주기로 싱크 애플리케이션 또는 싱크 파일에 저장할때 사용하는 로직이다.
     */
    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> currentOffsets) {

    }

    /**
     * 태스크가 종료될 때 필요한 로직을 작성한다.
     * 태스크에서 사용한 리소스를 종료해야할 때 여기에 종료 코드를 구현한다.
     */
    @Override
    public void stop() {

    }
}
