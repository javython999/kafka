package com.errday.kafka.connector;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.List;
import java.util.Map;

/**
 * SinkConnector를 상속받은 사용자 정의 클래스를 선언한다.
 * 사용자가 지정한 이 클래스 이름은 최종적으로 커넥트에서 호출할 때 사용되므로 명확하게 어떻게 사용되는지 적으면 좋다. 예) MongoDbSinkConnector
 *
 */
public class TestSinkConnector extends SinkConnector {

    /**
     * 사용자가 JSON 또는 config 파일 형태로 입력한 설정값을 초기화하는 메서드다.
     * 만약 올바른 값이 아니라면 여기서 ConnectException()을 호출하여 커넥터를 종료할 수 있다.
     */
    @Override
    public void start(Map<String, String> map) {

    }

    /**
     * 이 커넥터가 사용할 태스크 클래스를 지정한다.
     */
    @Override
    public Class<? extends Task> taskClass() {
        return null;
    }

    /**
     * 태스크 개수가 2개 이상인 경우 태스크마다 각기 다른 옵션을 설정할 때 사용한다.
     */
    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        return List.of();
    }

    /**
     * 커넥터가 종료될 때 필요한 로직을 작성한다.
     */
    @Override
    public void stop() {

    }

    /**
     *  커넥터가 사용할 설정값에 대한 정보를 받는다.
     *  커넥터의 설정값은 ConfigDef 클래스를 통해 각 설정의 이름, 기본값, 중요도, 설명을 정의할 수 있다.
     */
    @Override
    public ConfigDef config() {
        return null;
    }

    /**
     * 커넥터의 버전을 리턴한다.
     */
    @Override
    public String version() {
        return "";
    }
}
