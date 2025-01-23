package com.errday.kafka.connector;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleFileSinkConnector extends SinkConnector {

    private Map<String, String> configProperties;

    /**
     * 커넥트에서 SingleFileSinkConnector 커넥터를 생성할 때 받은 설정값들을 초기화 한다.
     * 초기화할 때 필수 설정값이 빠져있다면 ConnectException을 발생시켜 커넥터를 종료 한다.
     */
    @Override
    public void start(Map<String, String> props) {
        this.configProperties = props;
        try {
            new SingleFileSinkConnectorConfig(props);
        } catch (ConfigException e) {
            throw new ConnectException(e.getMessage(), e);
        }
    }

    /**
     * SingleFileConnector가 사용할 태스크의 클래스 이름을 지정한다.
     */
    @Override
    public Class<? extends Task> taskClass() {
        return SingleFileSinkTask.class;
    }

    /**
     * 태스크가 2개 이상인 경우에 태스크마다 다른 설정값을 줄 때 사용한다.
     */
    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> taskConfigs = new ArrayList<>(maxTasks);
        Map<String, String> taskProps = new HashMap<>();
        for (int i = 0; i < maxTasks; i++) {
            taskConfigs.add(taskProps);
        }
        return taskConfigs;
    }

    @Override
    public void stop() {

    }

    /**
     * 커넥터에서 사용할 설정값을 지정한다.
     */
    @Override
    public ConfigDef config() {
        return SingleFileSinkConnectorConfig.CONFIG;
    }

    @Override
    public String version() {
        return "1.0";
    }
}
