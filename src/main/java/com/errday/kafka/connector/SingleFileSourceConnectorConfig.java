package com.errday.kafka.connector;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

import static org.apache.kafka.common.config.ConfigDef.*;

public class SingleFileSourceConnectorConfig extends AbstractConfig {

    /**
     * 파일 소스 커넥터는 어떤 파일을 읽을 것인지 지정해야 하므로 파일의 위치와 파일 이름에 대한 정보가 포함되어 있어야 한다.
     * 옵션 명은 file로 파일 위치와 이름을 값으로 받는다.
     */
    public static final String DIR_FILE_NAME = "file";
    public static final String DIR_FILE_NAME_DEFAULT_VALUE = "/tmp/kafka.txt";
    private static final String DIR_FILE_NAME_DOC = "읽을 파일 경로와 이름";

    /**
     * 읽은 파일을 어느 토픽으로 보낼 것인지 지정하기 위해 옵션을 topic으로 1개의 값을 받는다.
     */
    public static final String TOPIC_NAME = "topic";
    private static final String TOPIC_DEFAULT_VALUE = "test";
    private static final String TOPIC_DOC = "보낼 토픽 이름";

    /**
     * ConfigDef 커넥터에서 사용할 옵션값들에 대한 정의를 표현하는 데에 사용된다.
     * ConfigDef 클래스는 define() 메서드를 플루언트 스타일로 옵션값을 지정할 수 있는데, 각 옵션값의 이름, 설명, 기본값, 중요도를 지정할 수 있다.
     */
    public static ConfigDef CONFIG = new ConfigDef().define(DIR_FILE_NAME, Type.STRING, DIR_FILE_NAME_DEFAULT_VALUE, Importance.HIGH, DIR_FILE_NAME_DOC)
                                                    .define(TOPIC_NAME, Type.STRING, TOPIC_DEFAULT_VALUE, Importance.HIGH, TOPIC_DOC);

    public SingleFileSourceConnectorConfig(Map<String, String> props) {
        super(CONFIG, props);
    }

}
