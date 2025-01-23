package com.errday.kafka.connector;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Type;

import java.util.Map;

public class SingleFileSinkConnectorConfig extends AbstractConfig {

    /**
     * 토픽의 데이터를 저장할 파일 이름을 옵션값으로 받기 위해 선언한다.
     * 토픽이 옵션값에 없는 이유는 커넥트를 통해 커넥터를 실행 시 기본값으로 받아야하기 때문이다.
     */
    public static final String DIR_FILE_NAME = "file";
    private static final String DIR_FILE_NAME_DEFAULT_VALUE = "/tmp/kafka.txt";
    private static final String DIR_FILE_NAME_DOC = "저장할 디렉토리와 파일 이름";

    /**
     * ConfigDef는 커넥터에서 사용할 옵션값들에 대한 정의를 표현하는데 사용된다.
     * ConfigDef 클래스와 define() 메서드로 각 옵션값의 이름, 설명, 기본값, 중요도를 지정할 수 있다.
     */
    public static ConfigDef CONFIG = new ConfigDef().define(DIR_FILE_NAME, Type.STRING, DIR_FILE_NAME_DEFAULT_VALUE, ConfigDef.Importance.HIGH, DIR_FILE_NAME_DOC);

    public SingleFileSinkConnectorConfig(Map<String, String> props) {
        super(CONFIG, props);
    }

}
