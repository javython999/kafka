package com.errday.kafka.connector;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SingleFileSourceConnector extends SourceConnector {

    Map<String, String> configProperties;

    /**
     * 커넥트에서 SingleFileSourceConnector 커넥터를 생성할 때 받은 설정값들을 초기화 한다.
     * 설정을 초기화 할 때 필수 설정값이 빠져있다면 ConnectException을 발생시켜 커넥터를 종료한다.
     */
    @Override
    public void start(Map<String, String> map) {
        this.configProperties = map;

        try {
            new SingleFileSourceConnectorConfig(map);
        } catch (ConfigException e) {
            throw new ConnectException(e.getMessage(), e);
        }
    }

    /**
     * SingleFileSourceConnector가 사용할 태스크의 클래스 이름을 지정한다.
     */
    @Override
    public Class<? extends Task> taskClass() {
        return SingleFileSourceTask.class;
    }

    /**
     * 태스크가 2개 이상인 경우에 태스크마다 다른 설정값을 줄 수 있다.
     * 여기서는 태스크가 2개 이상이더라도 동일한 설정값을 받도록 하였다.
     */
    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> taskConfigs = new ArrayList<>();
        Map<String, String> taskProps = new HashMap<>(configProperties);
        for (int i = 0; i < maxTasks; i++) {
            taskConfigs.add(taskProps);
        }
        return taskConfigs;
    }

    @Override
    public void stop() {

    }

    /**
     * 커넥터에서 사용할 설정값을 지정한다. 여기서는 SingleFileSourceConnectorConfig의 멤버 변수로 정의된 CONFIG 인스턴스를 리턴한다.
     */
    @Override
    public ConfigDef config() {
        return SingleFileSourceConnectorConfig.CONFIG;
    }

    @Override
    public String version() {
        return "1.0";
    }
}
