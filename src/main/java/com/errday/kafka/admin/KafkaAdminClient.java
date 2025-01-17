package com.errday.kafka.admin;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.config.ConfigResource;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
public class KafkaAdminClient {

    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 프로듀서  API 또는 컨슈머 API와 다르게 추가 설정 없이 클러스터 정보에 대한 설정만 하면 된다.
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // create()로 생성한다.
        try (AdminClient adminClient = AdminClient.create(configs)) {

            // 브로커 정보 조회
            log.info("== Get broker information ==");
            for (Node node : adminClient.describeCluster().nodes().get()) {
                log.info("node: {}", node);

                ConfigResource configResource = new ConfigResource(ConfigResource.Type.BROKER, node.idString());
                DescribeConfigsResult describeConfigs = adminClient.describeConfigs(Collections.singleton(configResource));
                describeConfigs.all()
                        .get()
                        .forEach((broker, config) -> {
                            config.entries().forEach(entry -> {
                                log.info("{} = {}", entry.name(), entry.value());
                            });
                        });
            }

            // 토픽 정보 조회
            Map<String, TopicDescription> topicInformation = adminClient.describeTopics(Collections.singleton("test")).all().get();
            log.info("== Topic information ==");
            log.info("{}", topicInformation);
        }

    }
}
