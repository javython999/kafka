package com.errday.kafka.connector;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SingleFileSourceTask extends SourceTask {

    /**
     * 파일 이름과 해당 파일을 읽은 지점을 오프셋 스토리지에 저장하기 위해 filename과 position 값을 정의 한다.
     * 2개의 키를 기준으로 오프셋 스토리지에 읽은 위치를 저장한다.
     */
    public final String FILENAME_FIELD = "filename";
    public final String POSITION_FIELD = "position";

    /**
     * 오프셋 스토리지에 데이터를 저장하고 읽을 때는 Map 자료구조에 담은 데이터를 사용한다.
     * filename이 키, 커넥터가 읽는 파일 이름이 값으로 저장되어 사용된다.
     */
    private Map<String, String> fileNamePartition;
    private Map<String, Object> offset;

    private String topic;
    private String file;

    /**
     * 읽은 파일의 위치를 커넥터 멤버 변수로 지정하여 사용한다. 커넥터가 최초로 실행될 때 오프셋 스토리지에 마지막으로 읽은 파일의 위치를 position 변수에 선언하여 중복 적재되지 않도록 할 수 있다.
     * 만약 처음 읽는 파일이라면 오프셋 스토리지에 해당 파일을 읽은 기록이 없으므로 position은 0으로 설정하여 처음부터 읽도록 한다.
     */
    private long position = -1;

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> props) {
        try {
            // Init variable
            SingleFileSourceConnectorConfig config = new SingleFileSourceConnectorConfig(props);

            /**
             * 커넥터 실행 시 받은 설정값을 SingleFileSourceConnectorConfig로 선언하여 사용한다.
             * 여기서는 토픽 이름과 읽은 파일 이름 설정값을 사용한다.
             * 토픽 이름과 파일 이름은 SingleFileSourceTask의 멤버 변수로 선언되었기 때문에 start() 메서드에서 초기화 이후에 다른 메서드에서 사용할 수 있다.
             */
            topic = config.getString(SingleFileSourceConnectorConfig.TOPIC_NAME);
            file = config.getString(SingleFileSourceConnectorConfig.DIR_FILE_NAME);
            fileNamePartition = Collections.singletonMap(FILENAME_FIELD, file);

            /**
             * 오프셋 스토리지에서 현재 읽고자 하는 파일 정보를 가져온다.
             * 오프셋 스토리지는 실제로 데이터가 저장되는 곳으로 단일 모드 커넥트는 로컬 파일로 저장하고, 분산 모드 커넥트는 내부 토픽에 저장한다.
             * 만약 오프셋 스토리지에서 데이터를 읽었을 때 null이 반환되면 읽고자 하는 데이터가 없다는 뜻이다.
             * null이 아닌 경우는 한 번이라도 커넥터를 통해 해당 파일을 처리했다는 뜻이다.
             * 해당 파일에 대한 정보가 있을 경우에는 파일의 마지막 읽은 위치를 get() 메서드로 가져온다.
             */
            offset = context.offsetStorageReader().offset(fileNamePartition);

            // Get file offset from offsetStorageReader
            if (offset != null) {
                Object lastReadOffset = offset.get(POSITION_FIELD);

                /**
                 * 오프셋 스토리지에서 가져온 마지막으로 처리한 지점을 position 변수에 할당한다.
                 * 이 작업을 통해 커넥터가 재시작되더라도 데이터의 중복, 유실 처리를 막을 수 있다
                 */
                if (lastReadOffset != null) {
                    position = Long.parseLong(lastReadOffset.toString());
                }
            } else {
                /**
                 * 반면, 오프셋 스토리지에서 가져온 데이터가 null 이라면  파일을 처리한적없으니 position 변수에 0을 할당한다.
                 */
                position = 0;
            }
        } catch (Exception e) {
            throw new ConnectException(e.getMessage(), e);
        }
    }

    /**
     * poll() 메서드는 태스크가 시작한 이후 지속적으로 데이터를 가져오기 위해 반복적으로 호출되는 메서드이다.
     * 이 메서드는 내부에서 소스 파일의 데이터를 읽어서 토픽으로 데이터를 보내야 한다.
     * 토픽으로 데이터를 내보내는 방법은 List<SourceRecord>를 리턴하는 것이다.
     * SourceRecord는 토픽으로 보낼 데이터를 담는 클래스이다.
     */
    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> results = new ArrayList<>();

        try {
            Thread.sleep(1000);
            List<String> lines = getLines(position);

            if (!lines.isEmpty()) {
                lines.forEach(line -> {
                    Map<String, Long> sourceOffset = Collections.singletonMap(FILENAME_FIELD, ++position);
                    SourceRecord sourceRecord = new SourceRecord(fileNamePartition, sourceOffset, topic, Schema.STRING_SCHEMA, line);
                    results.add(sourceRecord);
                });
            }
            return results;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConnectException(e.getMessage(), e);
        }
    }

    private List<String> getLines(long readLine) throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get(file));
        return reader.lines().skip(readLine).collect(Collectors.toList());
    }

    @Override
    public void stop() {

    }
}
