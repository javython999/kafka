package com.errday.kafka.connector;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class SingleFileSinkTask extends SinkTask {

    private SingleFileSinkConnectorConfig config;
    private File file;
    private FileWriter fileWriter;

    @Override
    public String version() {
        return "1.0";
    }

    /**
     * 커넥터를 실행할 때 설정한 옵션을 토대로 리소스를 초기화한다.
     * 여기서는 파일에 데이터를 지정하므로 1개의 파일 인스턴스를 생성한다.
     * 파일 이름과 위치는 커넥터를 실행할 때 설정한 옵션값을 기준으로 설정한다.
     */
    @Override
    public void start(Map<String, String> props) {
        try {
            config = new SingleFileSinkConnectorConfig(props);
            file = new File(config.getString(config.DIR_FILE_NAME));
            fileWriter = new FileWriter(file, true);
        } catch (Exception e) {
            throw new ConnectException(e.getMessage(), e);
        }
    }

    /**
     * 일정 주기로 토픽의 데이터를 가져오는 put() 메서드에는 데이터를 저장하는 코드를 작성한다.
     * SinkRecord는 토픽의 레코드이며 토픽, 파티션, 타임스탬프 정보를 포함한다.
     * 여기서 저장하는 데이터는 레코드의 메시지 값으로 value() 메서드로 리턴 받은 객체를 String 포맷으로 저장한다.
     */
    @Override
    public void put(Collection<SinkRecord> collection) {
        try {
            for (SinkRecord record : collection) {
                fileWriter.write(record.value().toString() + "\n");
            }
        } catch (IOException e) {
            throw new ConnectException(e.getMessage(), e);
        }
    }

    /**
     * put() 메서드에서 파일 데이터를 저장하는 것은 버퍼에 데이터를 지정하는 것이다.
     * 실질적으로 파일 시스템에 데이터를 저장하려면 FileWriter 클래스의 flush() 메서드를 호출해야 한다.
     */
    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> currentOffsets) {
        try {
            fileWriter.flush();
        } catch (IOException e) {
            throw new ConnectException(e.getMessage(), e);
        }

    }

    /**
     * 태스크를 종료할 경우  열었던 파일을 안전하게 닫는다.
     */
    @Override
    public void stop() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            throw new ConnectException(e.getMessage(), e);
        }
    }
}
