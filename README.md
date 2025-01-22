# 1. 들어가며
## 1.1 카프카의 탄생
링크드인에서는 파편화된 데이터 수집 및 분석 아키텍처를 운영하는 데에 큰 어려움을 겪었다.
데이터를 생성하고 적재하기 위해서는 데이터를 생성하는 소스 애플리케이션과 데이터가 최종 적재되는 타깃 애플리케이션을 연결해야 한다.
초기 운영 시에는 소스 애플리케이션에서 타깃 애플리케이션으로 연동하는 소스 코드를 작성했고
아키텍처가 복잡하지 않아 운영이 힘들지 않았다.

시간이 지날 수록 아키텍처는 거대해졌고 소스 애플리케이션과 타깃 애플리케이션의 개수가 많아지면서 문제가 생겼다.
데이터를 전송하는 라인이 기하급수적으로 복잡해졌다.

링크드인의 데이터팀은 신규 시스템을 만들기로 결정했고 그 결과물이 바로 아파치 카프카다.
카프카는 각각의 애플리케이션끼리 연결하여 데이터를 처리하는 것이 아니라
한 곳에 모아 처리할 수 있도록 중앙집중화했다.

## 1.2 빅데이터 파이프라인에서 카프카의 역할
> 높은 처리량

카프카는 프로듀서가 브로커로 데이터를 보낼 때와 컨슈머가 프로커로부터 데이터를 받을 때 모두 묶어서 전송한다.
많은 양의 데이터를 묶음 단위로 빠르게 배치 처리한다.
때문에 대용량의 실시간 로그데이터를 처리하는 데에 적합하다.
또한 파티션 단위를 통해 동일 목적의 데이터를 여러 파티션에 분배하고 데이터를 병렬 처리할 수 있다.
파티션 개수만큼 컨슈머 개수를 늘려서 동일 시간당 데이터 처리량을 늘리는 것이다.

> 확장성

데이터 파이프라인에서 데이터를 모을 때 데이터가 얼마나 들어올지는 예측하기 어렵다.
카프카는 가변적인 환경에서 안정적으로 확장 가능하도록 설계되었다.
데이터가 적을 때는 카프카 클러스터의 브로커를 최소한의 개수로 운영하다가 데이터가 많아지면
클러스터의 브로커 개수를 자연스럽게 늘려 스케일 아웃할 수 있다.
반대로 데이터 개수가 적어지고 추가 서버들이 더는 필요 없어지면 브로커 개수를 줄여 스케일 인 할 수 있다.

> 영속성

카프카는 다른 메시징 플랫폼과 다르게 전송받은 데이터를 메모리에 저장하지 않고 파일 시스템에 저장한다.
파일 시스템에 데이터를 적재하고 사용하는 것은 보편적으로 느리다고 생각하겠지만,
카프카는 운영체제 레벨에서 파일 시스템을 최대한 활요하는 방법을 적용하였다.
운영체제에서는 파일 I/O 성킁 향상을 위해 페이지 캐시 영역을 메모리에 따로 생성하여 사용한다.
페이지 캐시 메모리 영역을 사용하여 한번 읽은 파일 내용은 메모리에 저장시켰다가 다시 사용하는 방식이기 때문에
카프카가 파일 시스템에 저장하고 데이터를 저장, 전송하더라도 처리량이 높은 것이다.
디스크 기반의 파일 시스템을 활용한 덕분에 브로커 애플리케이션이 장애 방생으로 인해 급작스럽게 종료되더라도
프로세스를 재시작하여 안전하게 데이터를 처리할 수 있다.

> 고가용성

3개 이상의 서버들로 운영되는 카프카 클러스터는 일부 서버에 장애가 발생하더라도 무중단으로 안전하고 지속적으로 데이터를 처리할 수 있다.
클러스터로 이루어진 카프카는 데이터의 복제를 통해 고가용성의 특징을 가지게 되었다.

## 1.3 데이터 레이크 아키텍처와 카프카의 미래
카프카의 미래에 대해 설명하려면 데이터 레이크를 구성하는 아키텍처의 역사를 알아야 한다.
데이터 레이크 아키텍처의 종류는 2가지가 있다.

1. 람다 아키텍처
2. 카파 아키텍처

람다 아키텍처는 레거시 데이터 수집 플랫폼을 개선하기 위해 구성한 아키텍처이다.
초기 플랫폼은 엔드 투 엔드 각 서비스 애플리케이션으로부터 데이터를 배치로 모았다.
데이터를 배치로 모으는 구조는 유연하지 못했으며, 실시간으로 생성되는 데이터들에 대한 인사이트를
서비스 애플리케이션에 빠르게 전달하지 못하는 단점이 있었다.
또한 원천 데이터로부터 파생된 데이터의 히스토리를 파악하기 어려웠거 계속되는 데이터의 가공으로 인해
데이터가 파편화되면서 데이터 거버넌스(데이터 표준 및 정책)를 지키기 어려웠다.

이를 해결하기 위해 기존 배치 데이터를 처리하는 부분 외에 스피드 레이어라고 불리는 실시간 데이터 ETL 작업 영역을 정의한 아키텍처를 만들었는데
이것이 람다 아키텍처이다. 람다 아키텍처는 3가지 레이어로 나뉜다.
1. 배치레이어: 배치 데이터를 모아 특정 시간, 아이밍 마다 일괄 처리한다.
2. 서빙레이어: 가공된데이터를 데이터 사용자, 서비스 애플리케이션이 사용할 수 있도록 데이터가 저장된 공간이다.
3. 스피드레이어: 서비스에서 생성되는 원천 데이터를 실시간으로 분석하는 용도로 사용한다. 배치 데이터에 비해 낮은 지연으로 분석이 필요한 경우 스피드레이어를 통해 분석한다.

데이터를 배치처리하는 레이어와 실시간 처리하는 레이어로 분리한 람다 아키텍처는 데이터 처리방식을 명확히 나눌 수 있었지만
레이어가 2개로 나뉘기 때문에 생기는 단점이 있다. 

1. 데이터를 분석, 처리하는데 필요한 로직이 2벌로 각각의 레이어에 따로 존재해야한다는 점
2. 배치 데이터와 실시간 데이터를 융합하여 처리할 때는 다소 유연하지 못한 파이프라인을 생성해야 한다는 점

이러한 람다 아키텍처의 단점을 해소하기 위해 카파 아키텍처가 제안됐다.
카파 아키텍처는 람다 아키텍처와 유사하지만 배치 레이어를 제거하고 모든 데이터를 스피드 레이어에 넣어서 처리한다는 점이 다르다.
스피드 레이어에서 데이터를 모두 처리할 수 있게 되었다. 

## 1.4 정리
카프카를 활용할 수 있는 방안은 아키텍처를 어떻게 구성하느냐에 따라 무궁무진하게 많아진다.
아키텍처 구성을 정하려면 카프카의 특징과 카프카의 동작 방식에 대해 면밀하게 알아야만한다.

--- 

# 2. 카프카 빠르게 시작해보기
## 2.1 실습용 카프카 브로커 설치

> 카프카 브로커 실행 옵셜 설정
```shell
wget https://archive.apache.org/dist/kafka/2.5.0/kafka_2.12-2.5.0.tgz
tar xvf kafka_2.12-2.5.0.tgz
```
```shell
vi config/server.properties
```
config 폴더에 있는 server.properties 파일에는 카프카 브로커가 클러스터 운영에 필요한 옵션들을 지정할 수 있다.
여기서 실습용 카프카 브로커를 실행할 것이므로 `advertised.listener`만 설정하면된다.
`advertised.listener`는 카프카 클라이언트 또는 커맨드 라인 툴을 브로커와 연결할 때 사용된다.
현재 접속하고 있는 인스턴트의 퍼블릭 IP와 카프카 기본 포트인 9092를 `PLAINTEXT://`와 함께 붙여넣고 `advertised.listener`의 주석을 해제한다.

> 주키퍼 실행

카프카 바이너리가 포함된 폴더에는 브로커와 같이 실행할 주키퍼가 준비되어 있다.
분산 코디네이션 서비스를 제공하는 주키퍼는 카프카의 클러스터 설정 리더 정보, 컨트롤러 정보를 담고 있어 카프카를 실행하는 데에 필요한 애플리케이션이다.
주키퍼를 상용환경에서 안전하게 운영하그 위해서는 3대 이상의 서버로 구성하여 사용하지만 실습에는 동일한 서버에 카프카와 동시에 1대만 실행시켜 사용할 수도 있다.

```shell
bin/zookeeper-server.start.sh -daemon config/zookeeper.properties
jps -vm
```

> 카프카 브로커 실행 및 로그 확인

이제 카프카 브로커를 실행할 마지막 단계이다.
-daemon 옵션과 함께 카프카 브로커를 백그라운드 모드로 실행할 수 있다. kafka-server-start.sh 명령어를 통해 카프카 브로커를 실행한 뒤
jps 명령어를 통해 주키퍼와 브로커 프로세스의 동작 여부를 알 수 있다.

```shell
bin/kafka-server-start.sh -daemon config/server.properties
jps -m
```

## 2.2 카프카 커맨드 라인 툴
카프카에서 제공하는 카프카 커맨드 라인 툴들은 카프카를 운영할 때 가장 많이 접하는 도구다.
커맨드 라인 툴을 통해 카프카 브로커 운영에 필요한 다양한 명령을 내릴 수 있다.
카프카 클라이언트 애플리케이션을 운영할 때는 카프카 클러스터와 연동하여 데이터를 주고 받는 것도 중요하지만
토픽이나 파티션 개수 변경과 같은 명령을 실행해야하는 경우도 자주 발생한다. 그렇기 때문에 카프카 커맨드 라인 툴과 각 툴별 옵션에 대해 알고 있어야 한다.

### 2.2.1 kafka-topics.sh
이 커맨드 라인 툴을 통해 토픽과 관련된 명령을 실행할 수 있다.
토픽이란 카프카에서 데이터를 구분하는 가장 기본적인 개념이다.
RDBMS에서 사용하는 테이블과 유사하다고 볼 수 있다.
카프카 클러스터에 토픽은 여러 개 존재할 수 있다.
토픽에는 파티션이 존재하는데 파티션의 개수는 최소 1개부터 시작한다.
파티션은 카프카에서 토픽을 구성하는 데에 아주 중요한 요소이다.
파티션을 통해 한 번에 처리할 수 있는 데이터 양을 늘릴 수 있고 토픽내부에서도 파티션을 통해 데이터의 종류를 나누어 처리할 수 있기 때문이다.

> 토픽 생성

`kafka-topics.sh`를 통해 토픽 관련 명령을 실행할 수 있다.
`--create` 옵션을 사용하요 hello.kafka라는 이름을 가진 토픽을 생성할 수 있다.
각 옵션이 어떤 역할을 하는지 알아보자.

```shell
bin/kafka-topics.sh --create --bootstrap-server my-kafka:9092 --topic hello.kafka
```
```shell
Created topic hello.kafka.
```

1. `--create`: 토픽을 생성하는 명령어라는 것을 명시한다.
2. `--bootstrap-server`: 토픽을 생성할 카프카 클러스터를 구성하는 브로커들의 IP와 port를 적는다.
3. `--topic`: 토픽의 이름을 작성한다.

토픽은 파티션 개수, 복제 개수 등과 같이 다양한 옵션이 포함되어 있지만 명시하지 않으면 모두 브로커에 설정된 기본값으로 생성된다.
만약 파티션 개수, 복제 개수, 토픽 데이터 유지 기간 옵션들을 지정하여 토픽을 생성하고 싶다면 다음과 같이 명령하면 된다.

```shell
bin/kafka-topics.sh --create --bootstrap-server my-kafka:9092 --partitions 3 --replication-factor 1 --config retention.ms=172800000 --topic hello.kafka.2
```
1. `--partitions`: 파티션 개수를 지정할 수 있다. 파티션 최소 개수는 1개이다. 이 옵션을 사용하지 않으면 카프카 브로커 설정 파일(config/sever.properties)에 설정된 `num.partitions` 옵션 값에 따라 생성된다.
2. `--replication-factor`: 토픽의 파티션을 복제할 복제 개수를 지정한다. 1은 복제를 하지 않고 2는 1개의 복제본을 사용하겠다는 의미이다. 파티션 데이터는 각 브로커마다 저장된다.
한 개의 브로커에 장애가 발생하더라도 나머지 한 개 브로커에 저장된 데이터를 사용하여 안전하게 데이터를 처리할 수 있다. 이 옵션을 사용하지 않으면 카프카 브로커 설정에 있는 default.replication.factor 옵션값에 따라 생성된다.
3. `--config`: kafka-topics.sh 명령에 포함되지 않은 추가적인 설정을 할 수도 있다. retention.ms는 토픽의 데이터를 유지하는 기간을 뜻한다. 

> 토픽 리스트 조회

```shell
bin/kafka-topics.sh --bootstrap-server my-kafka:9092 --list
```
```shell
hello.kafka
hello.kafka.2
```

> 토픽 상세 조회

```shell
bin/kafka-topics.sh --bootstrap-server my-kafka:9092 --describe --topic hello.kafka.2
```
```shell
Topic: hello.kafka.2    PartitionCount: 3       ReplicationFactor: 1    Configs: segment.bytes=1073741824,retention.ms=172800000
        Topic: hello.kafka.2    Partition: 0    Leader: 0       Replicas: 0     Isr: 0
        Topic: hello.kafka.2    Partition: 1    Leader: 0       Replicas: 0     Isr: 0
        Topic: hello.kafka.2    Partition: 2    Leader: 0       Replicas: 0     Isr: 0
```
이미 생성된 토픽의 상태를 `--describe` 옵션을 사용하여 확인할 수 있다.
파티션 개수가 몇개인지, 복제된 파티션이 위치한 브로커의 번호, 기타 토픽을 구성하는 설정들을 출력한다.
토픽이 가진 리더가 현재 어느 브로커에 존재하는지도 같이 확인할 수 있다.

> 토픽 옵션 수정

토픽에 설정된 옵션을 변경하기 위해서는 `kafka-topics.sh` 또는 `kafka-configs.sh` 두 개를 사용해야 한다.
파티션 개수를 변경하려면 `kafka-topics.sh`를 사용해야 하고 토픽 삭제 정책인 리텐션 기간을 변경하려면 `kafka-configs.sh`를 사용해야 한다. 
이와 같이 파편화 된 이유는 토픽에 대한 정보를 관리하는 일부 로직이 다른 명령어로 넘어갔기 때문이다.
카프카 2.5까지는 `kafka-topics.sh`와 `--alter` 옵션을 사용하여 리텐션 기간을 변경할 수 있지만 추후 삭제될 예정이라고 경고 메세지가 발생하므로
`kafka-configs.sh`를 사용하자.
토픽 옵션중 `다이나믹 토픽 옵션`이라고 정의되는 일부 옵션들(log.segment.bytes, log.retention.ms 등)은 `kafka-configs.sh`를 통해 수정할 수 있다.
파티션 개수를 3개에서 4개로 늘리고, 리텍션 기간을 17280000ms에서 86400000ms로 변경해보자.

```shell
bin/kafka-topics.sh --bootstrap-server my-kafka:9092 --topic hello.kafka --alter --partitions 4
bin/kafka-topics.sh --bootstrap-server my-kafka:9092 --topic hello.kafka --describe
```
```shell
Topic: hello.kafka      PartitionCount: 4       ReplicationFactor: 1    Configs: segment.bytes=1073741824
        Topic: hello.kafka      Partition: 0    Leader: 0       Replicas: 0     Isr: 0
        Topic: hello.kafka      Partition: 1    Leader: 0       Replicas: 0     Isr: 0
        Topic: hello.kafka      Partition: 2    Leader: 0       Replicas: 0     Isr: 0
        Topic: hello.kafka      Partition: 3    Leader: 0       Replicas: 0     Isr: 0
```
* `--alter` 옵션과 `--partitions` 옵션을 함께 사용하여 파티션 개수를 변경할 수 있다. 토픽의 프티션을 늘릴 수 있지만 줄일 수는 없다. 그러므로 파티션 개수를 늘리 때는 반드시 늘려야 하는 상황인지 판단하는 것이 중요하다.


```shell
bin/kafka-configs.sh --bootstrap-server my-kafka:9092 --entity-type topics --entity-name hello.kafka --alter --add-config retention.ms=86400000
```
```shell
Completed updating config for topic hello.kafka.
```
```shell
bin/kafka-configs.sh --bootstrap-server my-kafka:9092 --entity-type topics --entity-name hello.kafka --describe
```
```shell
Dynamic configs for topic hello.kafka are:
  retention.ms=86400000 sensitive=false synonyms={DYNAMIC_TOPIC_CONFIG:retention.ms=86400000}
```
* `retention.ms`를 수정하기 위해 `kafka-configs.sh`와 `--alter`, `--add-config` 옵션을 사용했다. `--add-config` 옵션을 사용하면 이미 존재하는 설정값은 변경하고
존재하지 않는 설정값은 신규로 추가한다.


### 2.2.2 kafka-console-producer.sh
생성된 hello.kafka 토픽에 데이터를 넣을 수 있는 kafka-console-producer.sh 명령어를 실행해 보자.
토픽에 넣는 데이터는 `레코드`라고 부르며 `메시지 키(key)`와 `메시지 값(value)`으로 이루어져 있다.
이번에는 메사지 키 없이 메시지 값만 보내도록 하자.
메시지 키는 자바의 null로 기본 설정되어 브로커로 전송된다.

```shell
bin/kafka-console-producer.sh --bootstrap-server my-kafka:9092 --topic hello.kafka
>hello
>kafka
>0
>1
>2
>3
>4
>5
```
키보드로 문자를 작성하고 엔터 키를 누르면 별다른 응답 없이 메시지 값이 전송된다.
여기서 주의할 점은 `kafka-console-producer.sh`로 전송되는 레코드 값은 UTF-8을 기반으로 Byte로 변환되고 ByteArraySerializer로만 직렬화 된다는 점이다.
즉 String이 아닌 타입으로는 직렬화하여 전송할 수 없다. 
그러므로 텍스트 목적으로 문자열을 전송할 수 있고 다른 타입으로 직렬화하여 데이터를 브로커로 전송하고 싶다면 카프카 프로듀서 애플리케이션을 직접 개발해야 한다.

이제 메시지 키를 가지는 레코드를 전송해보자.
메시지 키를 가지는 레코드를 전송하기 위해서는 몇가지 옵션을 작성해야 한다.

```shell
bin/kafka-console-producer.sh --bootstrap-server my-kafka:9092 --topic hello.kafka --property "parse.key=true" --property "key.separator=:"
>key1:no1
>key2:no2
>key3:no3
```
* `"parse.key=true"`를 true로 두면 레코드를 전송할 때 메시지 키를 추가할 수 있다.
* `"key.separator=:"` 메시지 키와 메시지 값을 구분하는 구분자를 선언한다. `key.separator`를 선언하지 않으면 기본 설정은 Tab delimiter(\t)이다. 그러므로 key.separator를 선언하지 않고 메시지를 보내려면 메시지 키를 작성하고 탭 키를 누른 뒤 메시지 값을 작성하고 엔터를 누른다.

메시지 키와 메시지 값을 함께 전송한 레코드는 토픽의 파티션에 저장된다.
메시지 키가 null인 경우에는 프로듀서가 파티션으로 전성할 때 레코드 배치 단위로 라운드 로빈으로 전송한다.
메시지 키가 존재하는 경우에는 키의 해시값을 작성하여 존재하는 파티션 중 한 개에 할당된다.
이로 인해 메시지 키가 동일한 경우에는 동일한 파티션으로 전송된다.
다만, 이런 메시지 키와 파티션 할당은 프로듀서에서 설정된 파티셔너에 의해 결졍되는데, 
기본 파티셔너의 경우 이와 같은 동작을 보장한다.
커스텀 파티셔너를 사요알 경우에는 메시지 키에 따른 파티션 할당이 다르게 동작할 수도 있으니 참고하자.

### 2.2.3 kafka-console-consumer.sh
hello.kafka 토픽으로 전송한 데이터는 `kafka-console-consumer.sh` 명령어로 확인할 수 있다.
이때 필수 옵션으로 `--bootstrap-server`에 카프카 클러스터 정보, `--topic`에 토픽 이름이 필요하다. 추가로 `--from-beginning` 옵션을 주면 토픽에 저장된 가장 처음 데이터부터 출력한다.

```shell
bin/kafka-console-consumer.sh --bootstrap-server my-kafka:9092 --topic hello.kafka --from-beginning
```
```shell
kafka
4
5
no2
3
hello
no3
0
1
2
no1
```

만약 데이터의 메시지 키와 메시지 값을 확인하고 싶다면 `--property` 옵션을 사용하면 된다.
```shell
bin/kafka-console-consumer.sh --bootstrap-server my-kafka:9092 --topic hello.kafka --property print.key=true --property key.separator="-" --group hello-group --from-beginning
```
```shell
null-kafka
null-4
null-5
key2-no2
null-3
null-hello
key3-no3
null-0
null-1
null-2
key1-no1
```
* `--property print.key=true`: 메시지 키를 확인하기 위해 `print.key`를 true로 설정했다.
* `--property key.separator="-"`: 메시지 키 값을 구분하기 위해 `key.separator`를 설정했다. 설정하지 않으면 tab delimiter(\t)가 기본값으로 사용된다.
* `--group hello-group`: 신규 컨슈머 그룹을 생성했다. 컨슈머 그룹은 1개 이상의 컨슈머로 이루어져 있다. 이 컨슈머 그룹을 통해 가져간 토픽의 메시지는 가져간 메시지에 대해 커밋을 한다.
커밋이란 컨슈머가 특정 레코드까지 처리를 완료했다고 레코드의 오프셋 번호를 카프카 브로커에 저장하는 것이다.
* 커밋 정보는 __consumer_offsets 이름의 내부 토픽에 저장된다.

`kafka-console-producer.sh`로 전송했던 데이터의 순서가 현재 출력되는 순서와 다르다.
이는 카프카의 핵심인 파티션 개념 때문에 생기는 현상이다.
`kafka-console-consumer.sh`를 통해 데이터를 가져가게 되면 토픽의 모든 파티션으로부터 동일한 중요도로 데이터를 가져간다.
이로 인해 프로듀서가 토픽에 넣은 데이터의 순서와 컨슈머가 토픽에서 가져간 데이터의 순서가 달라지게 된다.
만약 토픽에 넣은 데이터의 순서를 보장하고 싶다면 가장 좋은 방법은 파티션 1개로 구성된 토픽을 만드는 것이다.
한 개의 파티션에서는 데이터의 순서를 보장하기 때문이다.

### 2.2.4 kafka-consumer-groups.sh
hello-group 이름의 컨슈머 그룹으로 생성된 컨슈머로 hello.kafka 토픽의 데이터를 가져갔다.
컨슈머 그룹은 따로 생성하는 명령을 날리지 않고 컨슈머를 동작할 때 컨슈머 그룹 이름을 지정하면 생성된다.
생성된 컨슈머 그룹의 리스트는 kafka-consumer-group.sh 명령어로 확인할 수 있다.

```shell
bin/kafka-consumer-groups.sh --bootstrap-server my-kafka:9092 --list
```
```shell
hello-group
```

`--list`는 컨슈머 그룹의 리스트를 확인하는 옵션이다. 컨슈머 그룹을 통해 현재 컨슈머 그룹이 몇개나 생성되었는지, 어떤 이름의 컨슈머 그룹이 존재하는지 확인할 수 있다.
이렇게 확인한 컨슈머 그룹 이름을 토대로 컨슈머 그룹이 어떤 토픽의 데이터를 가져가는지 확인할 때 쓰인다.

```shell
bin/kafka-consumer-groups.sh --bootstrap-server my-kafka:9092 --group hello-group --describe
```
```shell

Consumer group 'hello-group' has no active members.

GROUP           TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
hello-group     hello.kafka     3          4               4               0               -               -               -
hello-group     hello.kafka     2          1               1               0               -               -               -
hello-group     hello.kafka     1          2               2               0               -               -               -
hello-group     hello.kafka     0          4               4               0               -               -               -
```
컨슈머 그룹의 상세 정보를 확인하는 것은 컨슈머를 개발할 때, 카프카를 운영할 때 둘다 중요하게 활용된다.
컨슈머 그룹이 중복되지는 않는지 확인하거나 운영하고 있는 컨슈머가 랙이 얼마인지 확인하여 컨슈머의 상태를 최적화 하는 데에 사용한다.

### 2.2.5 kafka-verifiable-producer, consumer.sh
`kafka-verifiable`로 시작하는 2개의 스크립트를 사용하면 String 타입 메시지 값을 코드 없이 주고 받을 수 있다.
카프카 클러스터 설치가 완료된 이후에 토픽에 데이터를 전송하여 간단한 네트워크 통신 테스트를 할 때 유용하다.

```shell
bin/kafka-verifiable-producer.sh --bootstrap-server my-kafka:9092 --max-message 10 --topic verify-test
```
* `--max-message`: kafka-verifiable-producer.sh로 내보내는 데이터 개수를 지정한다. 만약 -1을 지정하면 kafka-verifiable-producer.sh가 종료될 때까지 계쏙 데이터를 토픽으로 보낸다.
* `--topic`: 데이터를 받을 토픽을 지정한다.

```shell
{"timestamp":1736513238441,"name":"startup_complete"}
{"timestamp":1736513238553,"name":"producer_send_success","key":null,"value":"0","offset":10,"topic":"verify-test","partition":0}
{"timestamp":1736513238554,"name":"producer_send_success","key":null,"value":"1","offset":11,"topic":"verify-test","partition":0}
{"timestamp":1736513238555,"name":"producer_send_success","key":null,"value":"2","offset":12,"topic":"verify-test","partition":0}
{"timestamp":1736513238555,"name":"producer_send_success","key":null,"value":"3","offset":13,"topic":"verify-test","partition":0}
{"timestamp":1736513238555,"name":"producer_send_success","key":null,"value":"4","offset":14,"topic":"verify-test","partition":0}
{"timestamp":1736513238555,"name":"producer_send_success","key":null,"value":"5","offset":15,"topic":"verify-test","partition":0}
{"timestamp":1736513238555,"name":"producer_send_success","key":null,"value":"6","offset":16,"topic":"verify-test","partition":0}
{"timestamp":1736513238555,"name":"producer_send_success","key":null,"value":"7","offset":17,"topic":"verify-test","partition":0}
{"timestamp":1736513238555,"name":"producer_send_success","key":null,"value":"8","offset":18,"topic":"verify-test","partition":0}
{"timestamp":1736513238555,"name":"producer_send_success","key":null,"value":"9","offset":19,"topic":"verify-test","partition":0}
{"timestamp":1736513238559,"name":"shutdown_complete"}
{"timestamp":1736513238560,"name":"tool_data","sent":10,"acked":10,"target_throughput":-1,"avg_throughput":83.33333333333333}
```
* 최초 실행시점이 startup_complete와 함께 출력된다.
* 메시지 별로 보낸 시간과 메시지 키, 메시지 값, 토픽, 저장된 파티션, 저장된 오프셋 번호가 출력된다.
* 10개 데이터가 모두 전송된 이후 통계값이 출력된다. 평균 처리량을 알 수 있다.

전송한 데이터는 `kafka-verifiable-consumer.sh`로 확인할 수 있다.
```shell
bin/kafka-verifiable-consumer.sh --bootstrap-server my-kafka:9092 --topic verify-test --group-id test-group
```
* `--topic`: 가지고 오고자 하는 토픽을 지정한다.
* `--group-id`: 컨슈머 그룹을 지정한다.

```shell
{"timestamp":1736513685811,"name":"startup_complete"}
{"timestamp":1736513686054,"name":"partitions_assigned","partitions":[{"topic":"verify-test","partition":0}]}
{"timestamp":1736513686129,"name":"records_consumed","count":20,"partitions":[{"topic":"verify-test","partition":0,"count":20,"minOffset":0,"maxOffset":19}]}
{"timestamp":1736513686145,"name":"offsets_committed","offsets":[{"topic":"verify-test","partition":0,"offset":20}],"success":true}
```
* 컨슈머가 실행되면 startup_complete 문자열과 시작 시간이 timestamp와 함께 출력된다.
* 토픽에서 데이터를 가져오기 위해 할당되는 파티션을 확인할 수 있따.
* 컨슈머는 한 번에 다수의 메시지를 가져와서 처리하므로 한 번에 20개의 메시지를 정상적으로 받았음을 알 수 있다.
* 메시지 수신 이후 20번 오프셋 커밋 여부도 확인할 수 있다.

### 2.2.6 kafka-delete-records.sh
이미 적재된 토픽의 데이터를 지우는 방법으로 `kafka-delete-records.sh`를 사용할 수 있다.
이미 적재된 토픽의 데이터중 가장 오래된 데이터부터 특정 시점의 오프셋까지 삭제할 수 있다.

```shell
vi delete-topic.json
{"partitions": ["topic": "test", "partition": 0, "offset": 50], "version": 1}
```
* 삭제하고자 하는 데이터에 대한 정보를 파일로 저장해서 사용해야 한다.
* 해당 파일에는 삭제하고자 하는 토픽, 파티션, 오프셋 저보가 들어가야 한다.

```shell
bin/kafka-delete-records.sh --bootstrap-server my-kafka:9092 --offset-json-file delete-topic.json
```
여기서 주의해야 할 점은 토픽의 특정 레코드 하나만 삭제되는 것이 아닐 ㅏ파티션에 존재하는 가장 오래된 오프셋부터 지정한 오프셋까지 삭제된다는 점이다.
카프카에서는 토픽의 파티션에 저장된 특정 데이터만 삭제할 수 없다는 점을 명심해야 한다.


## 2.3 정리
토픽을 생성, 수정하고 데이터를 전송(프로듀서)하고 받는(컨슈머) 실습을 진행했다.
카프카에서 제공하는 다양한 명령어로 핵심 기능을 사용할 수 있따는 것을 알게 되었다.
이 명령어들은 카프카 운영시에 자주 사용되므로 손에 익히는 것이 좋다.

---

# 3. 카프카 기본 개념 설명
## 3.1 카프카 브로커, 클러스터, 주키퍼
카프카 브로커는 카프카 클라이언트와 데이터를 주고 받기 위해 사용하는 주체이자, 데이터를 분산 저장하여 장애가 발생하더라도 안전하게 사용할 수 있도록
도와주는 애플리케이션이다.

하나의 서버에는 한 개의 카프카 브로커 프로세스가 실행된다.
카프카 브로커 서버 1대로도 기본 기능이 실행되지만 데이터를 안전하게 처리하기 위해 3대 이상의 브로커 서버를 1개의 클러스터로 묶어서 운영한다.
카프카 클러스터로 묶인 브로커들은 프로듀서가 보낸 데이터를 안전하게 분산 저장하고 복제하는 역할을 수행한다.

> 데이터 저장, 전송

프로듀서로부터 데이터를 전달받으면 카프카 브로커는 프로듀서가 요청한 토픽의 파티션에 데이터를 저장하나다.  
컨슈머가 데이터를 요청하면 파티션에 저장된 데이터를 전달한다.
프로듀서로부터 전달된 데이터는 파일 시스템에 저장된다. 실습용으로 진행한 카프카에서 저장된 파일 시스템을 직접 확인할 수 있다.

```shell
ls /tmp/kafka-logs
```
```shell
__consumer_offsets-0   __consumer_offsets-16  __consumer_offsets-23  __consumer_offsets-30  __consumer_offsets-38  __consumer_offsets-45  __consumer_offsets-8       hello.kafka.2-1
__consumer_offsets-1   __consumer_offsets-17  __consumer_offsets-24  __consumer_offsets-31  __consumer_offsets-39  __consumer_offsets-46  __consumer_offsets-9       hello.kafka.2-2
__consumer_offsets-10  __consumer_offsets-18  __consumer_offsets-25  __consumer_offsets-32  __consumer_offsets-4   __consumer_offsets-47  cleaner-offset-checkpoint  log-start-offset-checkpoint
__consumer_offsets-11  __consumer_offsets-19  __consumer_offsets-26  __consumer_offsets-33  __consumer_offsets-40  __consumer_offsets-48  hello.kafka-0              meta.properties
__consumer_offsets-12  __consumer_offsets-2   __consumer_offsets-27  __consumer_offsets-34  __consumer_offsets-41  __consumer_offsets-49  hello.kafka-1              recovery-point-offset-checkpoint
__consumer_offsets-13  __consumer_offsets-20  __consumer_offsets-28  __consumer_offsets-35  __consumer_offsets-42  __consumer_offsets-5   hello.kafka-2              replication-offset-checkpoint
__consumer_offsets-14  __consumer_offsets-21  __consumer_offsets-29  __consumer_offsets-36  __consumer_offsets-43  __consumer_offsets-6   hello.kafka-3              verify-test-0
__consumer_offsets-15  __consumer_offsets-22  __consumer_offsets-3   __consumer_offsets-37  __consumer_offsets-44  __consumer_offsets-7   hello.kafka.2-0
```
* 실습용 카프카를 실행 할때 `config/server.properties`의 `log.dir` 옵션에 정의한 디렉토리에 데이터를 저장한다.
토픽 이름과 파티션 번호의 조합으로 하위 디렉토리를 생성하여 데이터를 저장한다.


```shell
ls /tmp/kafka-logs/hello.kafka-0
```
```shell
00000000000000000004.index  00000000000000000004.log  00000000000000000004.timeindex  leader-epoch-checkpoint
```
* hello.kafka 토픽의 0번 파티션에 존재하는 데이터를 확인 할 수 있다. log에는 메시지와 메타데이터를 저장한다. 
* index는 메시지의 오프셋을 인덱싱한 정보를 담은 파일이다.
* timeindex 파일에는 메시지에 포함된 timestamp 값을 기준으로 인덱싱한 정보가 담겨 있다.

카프카는 데이터를 메모리나 데이터베이스에 저장하지 않는다.
캐시 메모리를 구현하여 사용하지도 않는다.
파일 시스템에 저장하기 때문에 파일 I/O로 인해 속도 이슈가 발생하지 않을까 의문을 가질 수 있다.
그러나 카프카는 페이지 캐시를  사용하여 디스크 입출력 속도를 높여 이 문제를 해결했다.

페이지 캐시란 OS에서 파일 I/O의 성능 향상을 위해 만들어 놓은 메모리 영역을 뜻한다.
한 번 읽은 파일의 내용은 메모리의 페이지 캐시 영역에 저장 시킨다.
추후 동일한 파일의 접근이 일어나면 디스크에서 읽지 않고 메모리에서 직접 읽는 방식이다.
JVM 위에서 동작하는 카프카 브로커가 페이지 캐시를 사용하지 않는다면 지금과 같이 빠른 동작을 기대할 수 없다.

> 데이터 싱크, 복제

데이터 복제는 카프카를 장애 허용 시스템으로 동작하도록 하는 원동력이다.
복제의 이유는 클러스터로 묶인 브로커 중 일부에 장애가 발생하더라도 데이터를 유실하지 않고 안전하게 사용하기 위함이다.

카프카의 데이터 복제는 파티션 단위로 이루어진다.
토픽을 생성할 때 파티션의 복제 개수도 같이 성정되는 직접 옵션을 선택하지 않으면 브로커에 설정된 옵션 값을 따라 간다.
복제 개수의 최소값은 1(복제 없음)이고 최댓값은 브로커 개수만큼 설정하여 사용할 수 있다.

복제된 파티션은 리더와 팔로워로 구성된다.
프로듀서 또는 컨슈머와 직접 통신하는 파티션은 리더,
나머지 복제 데이터를 가지고 있는 파티션을 팔로워라고 부른다.

팔로워들은 리더의 오프셋을 확인하여
자신이 가지고 있는 오프셋과 차이가 나는 경우 리더 파티션으로부터 데이터를 가져와서
자신의 파티션에 저장하는데, 이 과정을 복제라고 부른다.

복제 개수만큼 저장 용량이 증가한다는 단점이 있다.
그러나 복제를 통해 데이터를 더 안전하게 사용할 수있다는 강력한 장점 때문에
카프카를 운영할 때 2 이상의 복제 개수를 정하는 것이좋다.

리더가 장애로 인해 사용할 수 없게 됐을 때
팔로워중 하나가 리더 지위를 넘겨 받는다. 이를 통해 데이터가 유실되지 않고 
컨슈머나 프로듀서와 데이터를 주고받도록 동작할 수 있다.

> 컨트롤러

클러스터의 다수 브로커 중 한 대가 컨트롤러의 역할을 한다.
컨트롤러는 다른 브로커들의 상태를 체크하고 
브로커가 클러스터에서 빠지는 경우 해당 브로커에 존재하는 리더를 재분배한다.
카프카는 지속적으로 데이터를 처리해야 하므로 브로커의 상태가 비정상이라면
빠르게 클러스터에서 빼내는 것이 중요하다.
만약 컨트롤러 역할을 하는 브로커에 장애가 생기면 다른 브로커가 컨트롤러 역할을 한다.

> 데이터 삭제

카프카는 다른 메시징 플랫폼과 다르게 컨슈머가 데이터를 가져가더라도 토픽의 데이터는 삭제되지 않는다.
또한 컨슈머나 프로듀서가 데이터 삭제를 요청할 수도 없다.

오직 브로커만이 데이터를 삭제할 수 있다.
데이터 삭제는 파일 단위로 이루어지는데 이 단위를 `로그 세그먼트`라고 부른다.
이 세그먼트에는 다수의 데이터가 들어 있기 때문에 특정 데이터를 선별해서 삭제할 수 없다.
세그먼트는 데이터가 쌓이는 동안 파일 시스템으로 열려있으며 카프카 브로커에 `log.segment.bytes` 또는 `log.segment.ms` 옵션 값이 설정되면 세그먼트 파일이 닫힌다.
세그먼트 파일이 닫히게 되는 기본값은 1GB용량에 도달했을 때인데 간격을 더 줄이고 싶다면 작은 용량으로 설정하면 된다.
너무 작은 용량으로 설정하면 데이터를 저장하는 동안 세그먼트 파일을 자주 여닫음으로써 부하가 발생할 수 있어 주의해야 한다.
닫힌 세그먼트 파일은 `log.segment.bytes` 또는 `log.segment.ms` 옵션에 설정값이 넘으면 삭제된다.
닫힌 세그먼트 파일을 체크하는 간격은 카프카 브로커의 옵션에 설정된 `log.retention.check.interval.ms`에 따른다.

> 컨슈머 오프셋 저장

컨슈머 그룹은 토픽이 특정 파티션으로부터 데이터를 가져가서 처리하고 이 파티션의 어느 레코드까지 가져갔는지 확인하기 위해 오프셋을 커밋한다.
커밋한 오프셋은 `__consumer_offsets` 토픽에 저장한다.
여기에 저장된 오프셋을 토대로 컨슈머 그룹은 다음 레코드를 가져가서 처리한다.

> 코디네이터

클러스터의 다수 브로커 중 한 대는 코디네이터의 역할을 수행한다.
코디네이터는 컨슈머 그룹의 상태를 체크하고 파티션을 컨슈머와 매칭되도록 분배하는 역할을 한다.

컨슈머가 컨슈머 그룹에서 빠지면 매칭되지 않은 파티션을 정상 동작하는 컨슈머로 할당하여 끊임없이 데이터가 처리되도록 도와준다.
이렇게 파티션을 컨슈머로 재할당하는 과정을 리밸런스라고 부른다.

> 주키퍼

주키퍼는 카프카의 메타데이터를 관리하는 데에 사용된다.
카프카 서버에서 직접 주키퍼에 붙으려면 카프카 서버에서 실행되고 있는 주키퍼에 연결해야 한다.
동일 환경에 접속하므로 localhost로 접속하며, 기본 포트는 2181이다.

```shell
bin/zookeeper-shell.sh my-kafka:2181
```
```shell
Connecting to my-kafka:2181
Welcome to ZooKeeper!
JLine support is disabled

WATCHER::

WatchedEvent state:SyncConnected type:None path:null
```

## 3.2 토픽과 파티션
토픽은 카프카에서 데이터를 구분하기 위해 사용하는 단위이다.
토픽은 1개 이상의 파티션을 소유하고 있다.
파티션에는 프로듀서가 보낸 데이터들이 들어가 저장된다.
이 데이터를 레코드라고 부른다.

파티션은 카프카의 병렬처리의 핵심이다.
그룹으로 묶인 컨슈머들이 레코드를 병렬로 처리할 수 있도록 매칭된다.
컨슈머의 처리량이 한정된 상황에서 많은 레코드를 병렬로 처리하는 가장 좋은 방법은 컨슈머의 개수를 늘려 스케일 아웃하는 것이다.
컨슈머 개수를 늘림과 동시에 파티션 개수도 늘리면 처리량이 증가하는 효과가 있다.

파티션은 자료구조의 큐와 비슷한 구조라고 생각하면 쉽다.
먼저 들어간 레코드를 컨슈머가 먼저 가져가게 된다.
다만 큐에서는 데이터를 가져가면 레코드를 삭제하지만 카프카는 삭제하지 않는다.
이러한 특징 때문에 토픽의 레코드는 다양한 목적을 가진 여러 컨슈머 그룹들이 토픽의 데이터를 여러 번 가져갈 수 있다.

> 토픽 이름 제약 조건

* 빈 문자열 토픽 이름은 지원하지 않는다.
* 토픽 이름은 마침표 하나(.) 또는 마침표 둘(..)로 생성될 수 없다.
* 토픽 이름의 길이는 249자 미만으로 생성해야 한다.
* 토픽 이름은 영어 대소문자, 숫자 0~9 그리고 마침표(.) 언더스코어(_), 하이픈(-) 조합으로 생성할 수 있다. 이외의 문자열이 포함된 토픽 이름은 생성이 불가능하다.
* 카파카 내부 로직관리 목적으로 사용되는 2개 토픽(__consumer_offsets, __transaction_state)과 동일한 이름으로 생성이 불가능하다.
* 카프카 내부적으로 사용하는 로직 때문에 토픽 이름에 마침표(.)와 언더스코어(_)가 동시에 들어가면 안된다. 생성은 가능하지만 사용시 이슈가 발생한다.

## 3.3 레코드
레코드는 다음과 같이 구성되어 있다.
1. 타임스탬프
2. 메시지 키
3. 메시지 값
4. 오프셋
5. 헤더

프로듀서가 생성한 레코드가 브로커로 전송되면 오프셋과 타임스탬프가 지정되어 저장된다.
브로커에 한번 적재된 레코드는 수정할 수 없고 로그 리텐션 기간 또는 용량에 따라서만 삭제된다.

> 타임 스탬프

타임스탬프는 프로듀서에서 해당 레코드가 생성된 시점의 유닉스 타임이 설정된다.
컨슈머는 레코드의 타임스탬프를 토대로 레코드가 언제 생성되었는지 알 수 있다.
프로듀서가 레코드를 생성할 때 임의의 타임스탬프 값을 설정할 수 있고
토픽에 따라 브로커에 적재된 시간(LogAppendTime)으로 설정될 수 있다는 점을 유의해야 한다.

> 메시지 키

메시지 값을 순서대로 처리하거나 메시지 값의 종류를 나타내기 위해 사용한다.
메시지 키를 사용하면 프로듀서가 토픽에 레코드를 전송할 때 메시지 키의 해시값을 토대로 파티션을 지정하게 된다.
즉, 동일한 메시지 키라면 동일한 파티션에 들어가는 것이다.
다만 어느 파티션에 지정될지 알 수 없고 파티션 개수가 변경되면 메시지 키와 파티션 매칭이 달라지게 되므로 주의해야 한다.
메시지 키를 사용하지 않는다면 null로 설정된다. 메시지 키가 null로 설정된 레코드는 프로듀서 기본 설정 파티셔너에 따라 파티션에 분배되어 적재 된다.

> 메시지 값

메시지 값에는 실질적으로 처리할 데이터가 들어 있다.
메시지 키와 메시지 값은 직렬화 되어 브로커로 전송되기 떄문에 컨슈머가 이용할 때는 직렬화한 형태와 동일한 형태로 역직렬화를 수행해야 한다.
직렬화, 역직렬화할 때는 반드시 동일한 형태로 처리해야 한다.

> 오프셋

오프셋은 0 이상의 숫자로 이루어져있다.
레코드의 오프셋은 직접 지정할 수 없고 브로커에 저장될 때 이전에 전송된 레코드의 오프셋 + 1 값으로 생성된다.
오프셋은 카프카 컨슈머가 데이터를 가져갈 때 사용된다.
오프셋으로 사용하면 컨슈머 그룹으로 이루어진 카프카 컨슈머들이 파티션의 데이터를 어디까지 가져갔는지 명확히 지정할 수 있다.

> 헤더

레코드의 추가적인 정보를 담는 메타데이터 저장소 용도로 사용한다. 헤더는 키/값 형태로 데이터를 추가하여 레코드의 속성을 저장하여 컨슈머에서 참조할 수 있다.

## 3.4 카프카 클라이언트
카프카 클라이언트는 카프카 브로커와 상호작용하기 위한 다양한 API를 제공한다.

### 3.4.1 프로듀서 API
카프카에서 데이터의 시작점은 프로듀서이다.
프로듀서 애플리케이션은 카프카에 필요한 데이터를 선언하고 브로커의 특정 토픽의 파티션에 전송한다.
프로듀서는 데이터를 전송할 때 리더 파티션을 가지고 있는 카프카 브로커와 직접 통신한다.

프로듀서를 구현하는 가장 기초적인 방법은 카프카 클라이언트를 라이브러리로 추가하여 자바 기본 애플리케이션을 만드는 것이다.
프로듀서는 데이터를 직렬화하여 카프카 브로커로 보내기 때문에 자바에서 선언 가능한 모둔 형태를 브로커로 전송할 수 있다.
직렬화를 사용하면 프로듀서는 자바 기본형과 참조형뿐만 이니라, 동영상, 이미지 같은 바이너리 데이터도 프로듀서를 통해 전송할 수 있다.

> SimpleProducer
```java
@Slf4j
public class SimpleProducer {

    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        // 메시지 키, 메시지 값을 직렬화하기 위한 직렬화 클래스를 선언한다.
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);

        String messageValue = "testMessage";
        // 카프카 브로커로 데이터를 보내기 위해 ProducerRecord를 생성한다.
        // 메시지 키는 따로 설정하지 않아 null로 전송된다.
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, messageValue);

        // 생성한 ProducerRecord를 전송하기 위해 record를 파라미터로 가지는 send() 메서드를 호출한다.
        // send()는 즉각적인 전송을 뜻하는 것이 아니라, 파라미터로 들어간 record를 프로듀서 내부에 가지고 있다가 배치 형태로 묶어서 브로커에 전송한다.
        // 이러한 전송방식을 '배치 전송'이라고 부른다.
        producer.send(record);
        log.info("{}", record);

        // flush()를 통해 프로듀서 내부 버퍼에 가지고 있는 레코드 배치를 브로커로 전송한다.
        producer.flush();

        // 애플리케이션을 종료하기 전에 리소스를 반환한다.
        producer.close();
    }
}
```

카프카 프로듀서 애플리케이션을 실행하기 전에 전송될 토픽을 생성한다.
```shell
bin/kafka-topics.sh --bootstrap-server my-kafka:9092 --create --topic test --partitions 3
```
```shell
Created topic test.
```
```shell
[main] INFO org.apache.kafka.clients.producer.ProducerConfig - ProducerConfig values: 
	acks = 1
	batch.size = 16384
	bootstrap.servers = [my-kafka:9092]
	buffer.memory = 33554432
	client.dns.lookup = default
	client.id = producer-1
	compression.type = none
	connections.max.idle.ms = 540000
	delivery.timeout.ms = 120000
	enable.idempotence = false
	interceptor.classes = []
	key.serializer = class org.apache.kafka.common.serialization.StringSerializer
	linger.ms = 0
	max.block.ms = 60000
	max.in.flight.requests.per.connection = 5
	max.request.size = 1048576
	metadata.max.age.ms = 300000
	metadata.max.idle.ms = 300000
	metric.reporters = []
	metrics.num.samples = 2
	metrics.recording.level = INFO
	metrics.sample.window.ms = 30000
	partitioner.class = class org.apache.kafka.clients.producer.internals.DefaultPartitioner
	receive.buffer.bytes = 32768
	reconnect.backoff.max.ms = 1000
	reconnect.backoff.ms = 50
	request.timeout.ms = 30000
	retries = 2147483647
	retry.backoff.ms = 100
	sasl.client.callback.handler.class = null
	sasl.jaas.config = null
	sasl.kerberos.kinit.cmd = /usr/bin/kinit
	sasl.kerberos.min.time.before.relogin = 60000
	sasl.kerberos.service.name = null
	sasl.kerberos.ticket.renew.jitter = 0.05
	sasl.kerberos.ticket.renew.window.factor = 0.8
	sasl.login.callback.handler.class = null
	sasl.login.class = null
	sasl.login.refresh.buffer.seconds = 300
	sasl.login.refresh.min.period.seconds = 60
	sasl.login.refresh.window.factor = 0.8
	sasl.login.refresh.window.jitter = 0.05
	sasl.mechanism = GSSAPI
	security.protocol = PLAINTEXT
	security.providers = null
	send.buffer.bytes = 131072
	ssl.cipher.suites = null
	ssl.enabled.protocols = [TLSv1.2]
	ssl.endpoint.identification.algorithm = https
	ssl.key.password = null
	ssl.keymanager.algorithm = SunX509
	ssl.keystore.location = null
	ssl.keystore.password = null
	ssl.keystore.type = JKS
	ssl.protocol = TLSv1.2
	ssl.provider = null
	ssl.secure.random.implementation = null
	ssl.trustmanager.algorithm = PKIX
	ssl.truststore.location = null
	ssl.truststore.password = null
	ssl.truststore.type = JKS
	transaction.timeout.ms = 60000
	transactional.id = null
	value.serializer = class org.apache.kafka.common.serialization.StringSerializer

[main] INFO org.apache.kafka.common.utils.AppInfoParser - Kafka version: 2.5.0
[main] INFO org.apache.kafka.common.utils.AppInfoParser - Kafka commitId: 66563e712b0b9f84
[main] INFO org.apache.kafka.common.utils.AppInfoParser - Kafka startTimeMs: 1736768697044
[kafka-producer-network-thread | producer-1] INFO org.apache.kafka.clients.Metadata - [Producer clientId=producer-1] Cluster ID: w5GFP7SyQ8GsOQoHtU7aFw
[main] INFO com.errday.kafka.producer.SimpleProducer - ProducerRecord(topic=test, partition=null, headers=RecordHeaders(headers = [], isReadOnly = true), key=null, value=testMessage, timestamp=null)
[main] INFO org.apache.kafka.clients.producer.KafkaProducer - [Producer clientId=producer-1] Closing the Kafka producer with timeoutMillis = 9223372036854775807 ms.
```
* 카프카 프로듀서 구동 시 설정한 옵션들이 출력된다.
* 카프카 클라이언트의 버전이 출력된다.
* 전송한 ProducerRecord가 출력된다. ProducerRecord 인스턴스 생성 시 메시키 키를 설정하지 않아 null로 설정된 것을 확인할 수 있다.

토픽에 데이터가 전송되었는지 확인하기 위해 `kafka-console-consumer`명령으로 확인해보자.
```shell
bin/kafka-console-consumer.sh --bootstrap-server my-kafka:9092 --topic test --from-beginning
```
```shell
testMessage
```

> 프로듀서 중요 개념

프로듀서는 카프카 브로커로 데이터를 전송할 때 내부적으로 파티셔너, 배치 생성 단계를 거친다. 
KafkaProducer 인스턴스가 `send()` 메서드를 호출하면 ProducerRecord는 파티셔너에서 토픽의 어느 파티션으로 전송할 것인지 정해진다.
KafkaProducer 인스턴스 생성시 파티셔너를 따로 설정하지 않으면 기본값인 DefaultPartitioner로 설정된다.
파티셔너에 의해 구분된 레코드는 데이터를 전송하기 전에 어큐뮬레이터에 데이터를 버퍼로 쌓아놓고 발송한다.
버퍼에 쌓인 데이터는 배치로 묶어서 전송함으로써 카프카의 프로듀서 처리량을 향샹시키는 데에 상당한 도움을 준다.

프로듀서 API를 사용하면 `UniformStickyPartitioner`와 `RoundRobinPartitioner` 2개 파티션을 제공한다.
카프카 클라이언트 2.5.0 버전에서는 파티셔너를 지정하지 않은 경우 `UniformStickyPartitioner`가 기본 설정된다.

`UniformStickyPartitioner`, `RoundRobinPartitioner` 둘 다 메시지 키가 있을 때는 메시지 키의 해식밧과파티션을 매칭하여 데이터를 전송한다는 점이 동일하다.
메시지 키가 없을 때는 파티션에 최대한 동일하게 분배하는 로직이 들어있는데 `UniformStickyPartitioner`는 `RoundRobinPartitioner`의 단점을 개선하였다는 점이 다르다. 

`UniformStickyPartitioner`는 프로듀서 동작에 특화되어 높은 처리량과 낮은 리소스 사용률을 가지는 특징이 있다.
카프카 2.4.0 이전에는 `RoundRobinPartitioner`가 기본 파티셔너로 설정되어 있었다.
`RoundRobinPartitioner`는 ProducerRecord가 들어오는 대로 파티션을 순회하면서 전송하기 때문에 배치로 묶이는 빈도가 적다.
`UniformStickyPartitioner`는 어큐뮬레이터에서 데이터가 배치로 모두 묶일 때 까지 기다렸다가 배치로 묶인 데이터는 모두 동일한 파티션에 전송함으로써
향상된 성능을 가지게 되었다.

> 프로듀서 주요 옵션
* 필수 옵션:
  * `bootstrap-server`: 프로듀서가 데이터를 전송할 대상 카프카 클러스터에 속한 브로커의 호스트 이름:포트를 작성한다.
  2개 이상 브로커 정보를 입력하여 일부 부로커에 이슈가 발생하더라도 접속하는 데에 이슈가 없도록 설정 가능하다.
  * `key.serializer`: 레코드의 메시지 키를 직렬화하는 클래스를 지정한다.
  * `value.serializer`: 레코드의 메시지 값을 직렬화하는 클래스를 지정한다.
* 선택 옵션:
  * `acks`: 프로듀서가 전송한 데이터가 브로커에 정상적으로 저장되었는지 전송 성공 여부를 확인하는데 사용하는 옵션이다. 0, 1, -1(all)중 하나로 설정할 수 있다.
  설정값에 따라 데이터의 유실 가능성이 달라진다. 
    * 1: 기본값. 리더 파티션에 데이터가 저장되면 전송 성공으로 판단.
    * 0: 프로듀서가 전송한 즉시 브로커에 데이터 저장 여부와 상관없이 성송으로 판단.
    * -1(all): 토픽의 min.insync.replicas 개수에 해당하는 리더 파티션과 팔로워파티션에 데이터가 저장되면 성공하는 것으로 판단한다.
  * `buffer.memory`: 브로커로 전송할 데이터를 배치로 모으기 위해 설정할 버퍼 메모리양을 지정한다. 기본값은 33554432(32MB)이다.
  * `retries`: 프로듀서가 브로커로부터 에러를 받고 난 뒹 재전송을 시도하는 횟수를 지정한다.
  * `batch.size`: 배치로 전송할 레코드 최대 용량을 지정한다. 너무 작게 설정하면 프로듀서가 브로커로 더 자주 보내기 떄문에 네트워크 부담이 있고 너무 크게 설정하면 메모리를 더 많이 사용하게 되는 점을 주의해야 한다.
  * `linger.ms`: 배치를 전송하기 전까지 기다리는 최소 시간이다. 기본값은 0이다.
  * `partitionser.class`: 레코드를 파티션에 전송할 때 적용하는 파티셔너 클래스를 지정한다.
  * `enable.idempotence`: 멱등성 프로듀서로 동작할지 여부를 설정한다. 기본값은 false
  * `transactional.id`: 프로듀서가 레코드를 전송할 때 레코드를 트랙잭션 단위로 묶을지 여부를 설정한다. 프로듀서의 고유한 트랜잭션 아이디를 설정할 수 있다. 이 값을 설정하면 트랜잭션 프로듀서로 동작한다. 기본값은 null이다.

> 메시지 키를 가진 데이터를 전송하는 프로듀서

```shell
public class KeyValueProducer {

    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        // 메시지 키, 메시지 값을 직렬화하기 위한 직렬화 클래스를 선언한다.
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);

        String messageValue = "testMessage";
        // 카프카 브로커로 데이터를 보내기 위해 ProducerRecord를 생성한다.
        // 메시지 키는 따로 설정하지 않아 null로 전송된다.
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "kafka", "23");

        // 생성한 ProducerRecord를 전송하기 위해 record를 파라미터로 가지는 send() 메서드를 호출한다.
        // send()는 즉각적인 전송을 뜻하는 것이 아니라, 파라미터로 들어간 record를 프로듀서 내부에 가지고 있다가 배치 형태로 묶어서 브로커에 전송한다.
        // 이러한 전송방식을 '배치 전송'이라고 부른다.
        producer.send(record);
        log.info("{}", record);

        // flush()를 통해 프로듀서 내부 버퍼에 가지고 있는 레코드 배치를 브로커로 전송한다.
        producer.flush();

        // 애플리케이션을 종료하기 전에 리소스를 반환한다.
        producer.close();
    }
}
```
* 메시지 키가 포함된 레코드를 전송하고 싶다면 ProducerRecord 생성 시 파라미터로 추가해야 한다.
* 토픽 이름, 메시지 키, 메시지 값을 순서대로 파라미터로 넣고 생성하면 된다.

메시지 키가 지정된 데이터는 `kafka-console-consumer` 명령으로 확인할 수 있다.
property 옵션의 `print.key.separator` 설정값을 기준으로 나뉘어 한 줄로 출력된다.
```shell
bin/kafka-console-consumer.sh --bootstrap-server my-kafka:9092 --topic test --property print.key=true --property key.separator="-" --from-beginning
```
```shell
null-testMessage
kafka-23
```

> 커스텀 파티셔너를 가지는 프로듀서

프로듀서 사용환경에 따라 특정 데이터를 가지는 레코드를 특정 파티션으로 보내야할 때가 있다.
기본 설정 파티셔너를 사용할 경우 메시지 키의 해시값을 파티션에 매칭하여 데이터를 전송하므로 어느 파티션에 들어가는지 알 수 없다.
이때 Partitioner 인터페이스를 사용하여 사용자 정의 파티셔너를 생성하면 특정 키에 대해 특정 파티션으로 지정되도록 설정할 수 있다.
이렇게 지정할 경우 파티션의 개수가 변경 되더라도 특정 키를 가진 데이터는 특정 파티션에 적재되도록 할 수 있다.

```java
public class CustomPartitioner implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {

        // partition 메서드에는 레코드를 기반으로 파티션을 정하는 로직이 포함된다.
        // 리턴 값은 주어진 레코드가 들어갈 파티션 번호이다.
        
        // 레코드에 메시지 키를 지정하지 않을 경우에는 비정상적인 데이터로 간주하고 InvalidRecordException을 발생시킨다. 
        if (key == null) {
            throw new InvalidRecordException("Need message key");
        }

        // 메시지 키가 specialKey인 경우 파티션 0번이 지정되도록 0을 리턴한다.
        if ("specialKey".equals(key.toString())) {
            return 0;
        }

        // 그 외 키를 가진 레코드는 해시값을 지정하여 특정 파티션에 매칭되도록 한다.
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
```

> 브로커 정상 전송 여부를 확인하는 프로듀서

KafkaProducer의 `send()` 메서드는 Future 객체를 반환한다.
이 객체는 RecordMetadata의 비동기 결과를 표현하는 것으로 ProducerRecord가 카프카 브로커에 정상적으로 적재되었는지에 대한 데이터가 포함되어 있다.
다음 코드와 같이 `get()` 메서드를 사용하면 프로듀서로 보낸 데이터의 결과를 동기적으로 가져올 수 있다.

```java
@Slf4j
public class SyncCallbackProducer {

    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);

        String messageValue = "syncCallbackProducer";
       ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, messageValue);

        RecordMetadata metadata = producer.send(record).get();
        log.info("{}", metadata);

        producer.flush();
        producer.close();
    }
}
```
`send()`의 결과값은 카프카 브로커로부터 응답을 기다렸다가 브로커로부터 응답이 오면 RecordMetadata 인스턴스를 반환한다.
```shell
[main] INFO com.errday.kafka.producer.SyncCallbackProducer - test-1@2
```
레코드가 정상적으로 적재되었다면 토픽 이름과 파티션 번호, 오프셋 번호가 출력된다.
위 로그에 따르면 전송한 레코드는 test 토픽의 1번 파티션에 적재되었으며 레코드에 부여된 오프셋 번호는 2번이다.

그러나 동기로 프로듀서의 전송 결과를 확인하는 것은 빠른 전송에 허들이 될 수 있다.
프로듀서가 전송하고 난 뒹 브로커로부터 전송에 대한 응답 값을 받기 전까지 대기하기 때문이다.
따라서 이를 원하지 않을 경우를 위해 프로듀서는 비동기로 결과를 확인할 수 있도록 `Callback` 인터페이스를 제공하고 있다.
사용자는 사용자 정의 `Callback` 클래스를 생성하여 레코드의 전송 결과에 대응하는 로직을 만들 수 있다.

```shell
@Slf4j
public class ProducerCallback implements Callback {
    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        if (e != null) {
            log.error(e.getMessage());
        } else {
            log.info(recordMetadata.toString());
        }
    }
}
```
`onCompletion` 메서드는 레코드의 비동기 결과를 받기 위해 사용한다.

```shell
@Slf4j
public class AsyncCallbackProducer {

    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);

        String messageValue = "syncCallbackProducer";
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, messageValue);

        producer.send(record, new ProducerCallback());
        producer.flush();
        producer.close();
    }
}
```
KafkaProducer 인스턴스의 `send()` 메서드 호출 시 ProducerRecord 객체와 함께 Callback 클래스를 넣으면 된다.
비동기로 결과를 받을 경우 동기로 결과를 받는 경우보다 더 빠른 속도로 데이터를 추가 처리할 수 있다.
전송하는 데이터의 순서가 중요할 경우 사용하면 안된다. 
비동기로 결과를 기다리는 동안 다음으로 보낼 데이터의 전송이 성공하고 앞서 보낸 데이터의 결과가 실패할 경우
재전송으로 인해 데이터 순서가 역전될 수 있기 때문이다.

###  3.4.2 컨슈머  API
프로듀서로 전송한 데이터는 카프카 브로커에 적재된다.
컨슈머는 적재된 데이터를 사용하기 위해 브로커로부터 데이터를 가져와서 필요한 처리를 한다.

```shell
@Slf4j
public class SimpleConsumer {

    
    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private final static String GROUP_ID = "test-group";

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        
        // 컨슈머 그룹 이름을 선언한다. 컨슈머 그룹을 통해 컨슈머의 목적을 구분할 수 있다.
        // 컨슈머 그룹을 기준으로 컨슈머 오프셋을 관리하기 때문에 subscribe() 메서드를 사용하여 토픽을 구독하는 경우에는 컨슈머 그룹을 선언해야 한다.
        // 컨슈머가 중단되거나 재시작되더라도 컨슈머 그룹의 컨슈머 오프셋을 기준으로 이후 데이터를 처리하기 때문이다.
        // 컨슈머 그룹을 선언하지 않으면 어떤 그룹에도 속하지 않는 컨슈머로 동작하게 된다.
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        
        // 프로듀서가 직렬화하여 전송한 데이터를 역직렬화하기 위해 역직렬화 클래스를 지정한다.
        // 프로듀서에서 직렬화한 타입으로 역직렬화해야 한다.
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs)) {
            // 컨슈머에게 토픽을 할당하기 위해 subscribe() 메서드를 사용한다.
            // Collection 타입의 String 값들을 인자로 받는다.
            consumer.subscribe(List.of(TOPIC_NAME));
            
            while (true) {
                // 컨슈머는 poll() 메서드를 호출하여 데이터를 가져와서 처리한다.
                // 지속적으로 데이터를 처리하기 위해 반복 호출을 해야 한다.
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                // 컨슈머는 poll() 메서드를 통해 ConsumerRecord 리스트를 반환한다. poll() 메서드는 Duration 타입을 인자로 받는다.
                // 이 인자값은 브로커로부터 데이터를 가져올 때 컨슈머 버퍼에 데이터를 기다리기 위한 타임아웃 간격을 의미한다.
                for (ConsumerRecord<String, String> record : records) {
                    log.info("{}", record);
                }
            }
        }

    }
}
```
```shell
[main] INFO com.errday.kafka.consumer.SimpleConsumer - ConsumerRecord(topic = test, partition = 0, leaderEpoch = 0, offset = 0, CreateTime = 1736855392697, serialized key size = -1, serialized value size = 20, headers = RecordHeaders(headers = [], isReadOnly = false), key = null, value = syncCallbackProducer)
```
토픽으로부터 데이터를 polling 하여 로그를 출력했다.
가져온 레코드의 파티션 번호, 오프셋, 레코드가 브로커에 들어간 날짜, 메시지 키, 메시지 값을 확인할 수 있다.

> 컨슈머 중요 개념

토픽의 파티션으로부터 데이터를 가져가기 위해 컨슈머를 운영하는 방법은 크게 2가지가 있다.
1. 1개 이상의 컨슈머로 이루어진 컨슈머 그룹을 운영
2. 토픽의 특정 파티션만 구독하는 컨슈머를 운영

컨슈머 그룹으로 운영하는 방법은 컨슈머를 각 컨슈머 그룹으로부터 격리된 환경에서 안전하게 운영할 수 있도록 도와주는 카프카의 독특한 방식이다.
컨슈머 그룹으로 묶인 컨슈머들은 토픽의 1개 이상 파티션들에 할당되어 데이터를 가져갈 수 있다.
컨슈머 그룹으로 묶인 컨슈머가 토픽을 구독해서 데이터를 가져갈 때, 1개의 파티션은 1개의 컨슈머에 할당 가능하다.
그리고 1개 컨슈머는 여러 개의 파티션에 할당 될 수 있다.
이러한 특징으로 컨슈머 그룹의 컨슈머 개수는 가져가고자 하는 토픽의 파티션 개수보다 같거나 작아야 한다.

예를 들어, 3개의 파티션을 가진 토픽을 효과적으로 처리하기 위해서는 3개 이하의 컨슈머로 이루어진 컨슈머 그룹을 운영해야 한다.
만약 4개의 컨슈머로 이루어진 컨슈머 그룹을 운영할 경우 파티션을 할당 받지 못한 1개의 컨슈머는 유휴 상태로 남아 스레드만 차지하고
실질적인 데이터를 처리하지 못한다.

컨슈머 그룹은 다른 컨슈머 그룹과 격리되는 특징을 가지고 있다.
프로듀서가 보낸 데이터를 각기 다른 역할을 하는 컨슈머 그룹끼리 영향을 받지 않게 처리할 수 있다는 장점을 가진다.

컨슈머 그룹에 장애가 발생하면 어떻게 될까? 
컨슈머 그룹으로 이루어진 컨슈머들 중 일부 컨슈머에 장애가 발생하면,
장애가 발생한 컨슈머에 할당된 파티션은 장애가 발생하지 않은 컨슈머에 소유권이 넘어간다.
이러한 과정을 리밸런싱이라고 부른다.
리밸런싱은 두 가지 상황에서 일어난다.

1. 컨슈머가 추가되는 상황
2. 컨슈머가 제외되는 상황

리밸런싱은 컨슈머가 데이터를 처리하는 도중에 언제든지 발생할 수 있으므로 데이터 처리중 발생한 리밸런싱에 대응하는 코드를 작성해야 한다.
가용성을 높이면서도 안정적인 운영을 도와주는 리밸런싱은 유용하지만 자주 일어나서는 안 된다.
파티션의 소유권을 컨슈머로 재할당하는 과정에서 해당 컨슈머 그룹의 컨슈머들이 토픽의 데이터를 읽을 수 없기 때문이다.

그룹 조정자는 리밸런싱을 발동시키는 역할을 하는데 컨슈머 그룹의 컨슈머가 추가되고 삭제될 때를 감지한다.
카프카 브로커 중 한 대가 그 그룹 조정자의 역할을 수행한다.

컨슈머는 카프카 브로커로부터 데이터를 어디까지 가져갔는지 커밋을 통해 기록한다.
특정 토픽의 파티션을 어떤 컨슈머 그룹이 몇 번째 가져갔는지 카프카 브로커 내부에서 사용되는 내부 토픽(__consumer_offsets)에 기록된다.
컨슈머 동작 이슈가 발생하여 __consumer_offsets 토픽에 어느 레코드까지 읽어갔는지 오프셋 커밋이 기록되지 못했다면
데이터 처리의 중복이 발생할 수 있다.
그러므로 데이터 처리의 중복이 발생하지 않게 하기 위해서는 컨슈머 애플리케이션이 오프셋 커밋을 정상적으로 처리했는지 검증해야만 한다.

오프셋 커밋은 컨슈머 애플리케이션에서 명시적, 비명시적으로 수행할 수 있다.
기본 옵션은 poll() 메서드가 수행될 때 일정 간격마다 오프셋을 커밋하도록 `enable.auto.commit=true`로 설정되어 있다.
이렇게 일정 간격마다 자동으로 커밋되는 것을 비명시 '오프셋 커밋'이라고 부른다.
이 옵셧은 `auto.commit.interval.ms`에 설정된 값과 함꼐 사용되는데, poll() 메서드가 `auto.commit.interval.ms`에 설정된 값 이상이 지났을때
그 시점까지 읽은 레코드의 오프셋을 커밋한다.
poll() 메서드를 호출할 때 커밋을 수행하므로 코드상에서 따로 커밋 관련 코드를 작성할 필요가 없다.
비명시 오프셋 커밋은 편리하지만 poll() 메서드 호출 이후에 리밸런싱 또는 컨슈머가 강제종료 발생 시 컨슈머가 처리하는 데이터가 중복 또는 유실될 수 있는 가능성이 있는
취약한 구조를 가진다. 그러므로 데이터 중복이나 유실을 허용하지 않는 서비스라면 자동 커밋을 사용해서는 안된다.

명시적으로 오프셋을 커밋하려면 poll() 메서드를 호출 이후에 반환 받은 데이터가 처리 완료되고 `commitSync()` 메서드를 호출하면 된다.
`commitSync()` 메서드는 poll() 메서드를 통해 반환된 레코드의 가장 마지막 오프셋을 기준으로 커밋을 수행한다.
`commiySync()` 메서드는 브로커에 커밋을 요청하고 커밋이 정상적으로 처리되었는지 응답까지 기다리는데
이는 컨슈머의 처리량에 영향을 끼친다.
데이터 처리 시간에 비해 커밋 요청 및 응답에 시간이 오래 걸린다면 동일 시간당 데이터 처리량이 줄어들게 된다.
이를 해결하기 위해 `commitAsync()` 메서드를 사용하여 커밋 요청을 전송하고 응답이 오기 전까지 데이터 처리를 수행할 수 있다.
하지만 비동기 커밋은 커밋 요청이 실패했을 경우 현재 처리중인 데이터의 순서를 보장하지 않으며 데이터의 중복처리가 발생할 수 있다.

> 컨슈머 주요 옵션
* 필수 옵션:
  * `bootstrap.servers`: 프로듀서가 데이터를 전송할 대상 카프카 클러스터에 속한 브로커의 호스트 이름:포트를 1개이상 작성한다.
  * `key.deserializer`: 레코드의 메시지 키를 역직렬화하는 클래스를 지정한다.
  * `value.deserializer`: 레코드의 메시지 값을 역직렬화하는 클래스를 지정한다.
* 선택 옵션
  * `group.id`: 컨슈머 그룹 아이디를 지정한다. subscribe() 메서드로 토픽을 구독하여 사용할 때는 이 옵션을 필수로 넣어야 한다. 기본값은 null이다.
  * `auto.offset.reset`: 컨슈머 그룹이 특정 파티션을 읽을 때 저장된 컨슈머 오프셋이 없는 경우 어느 오프셋부터 읽을지 선택하는 옵션이다.
  이미 컨슈머 오프셋이 있다면 이 옵션 값은 무시된다. 이 옵션은 `latest`, `earliest`, `none` 중 1개를 설정할 수 있다. 기본값은 `latest`이다.
    * `latest`: 가장 높은 오프셋(가장 최근에 넣은)부터 읽기 시작한다.
    * `earliest`: 가장 낮은 오프셋(가장 오래전에 넣은)부터 읽기 시작한다.
    * `none`: 컨슈머 그룹이 커밋한 기록이 있는지 찾아본다. 만약 기록이 없으면 오류를 반환하고, 커밋 기록이 있다면 기존 커밋 기록 이후 오프셋부터 읽기 시작한다.
  * `enable.auto.commit`: 자동 커밋으로 할지 수동 커밋으로 할지 선택한다. 기본값은 true이다.
  * `auto.commit.interval.ms`: 자동 커밋(enable.auto.commit)일 경우 오프셋 커밋 간격을 지정한다. 기본값은 5000(5초)이다.
  * `max.poll.records`: poll() 메서드를 통해 반환되는 레코드 개수를 지정한다. 기본값은 500이다.
  * `session.timeout.ms`: 컨슈머가 브로커와 연결이 끊기는 최대 시간이다. 이 시간내에 하트비트를 전송하지 않으면 브로커는 컨슈머에 이슈가 발생했다고 가정하고 리밸런싱을 시작한다.
  보통 하트비트 시간 간격의 3배로 설정한다. 기본값은 10000(10초)이다.
  * `heartbeat.interval.ms`: 하트비트를 전송하는 시간 간격이다. 기본값은 3000(3초)이다.
  * `max.poll.interval.ms`: poll() 메서드를 호출하는 간격의 최대 시간을 지정한다. poll() 메서드를 호출한 이후에 데이터를 처리하는 데에 시간이 너무 많이 걸리는 겨웅 비정상으로 판단하고 리밸런싱을 시작한다. 기본값은 300000(5분)이다.
  * `isolation.level`: 트랜잭션 프로듀서가 레코드를 트랜잭션 단위로 보낼 경우 사용한다. 기본값은 read_uncommited이다.
    * `read_committed`: 커밋이 완료된 레코드만 읽는다.
    * `read_uncommited`: 커밋 여부와 관계없이 파티션에 있는 모든 레코드를 읽는다.

> 동기 오프셋 커밋

```java
@Slf4j
public class SyncCommitConsumer {


    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private final static String GROUP_ID = "test-group";

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // 컨슈머 그룹 이름을 선언한다. 컨슈머 그룹을 통해 컨슈머의 목적을 구분할 수 있다.
        // 컨슈머 그룹을 기준으로 컨슈머 오프셋을 관리하기 때문에 subscribe() 메서드를 사용하여 토픽을 구독하는 경우에는 컨슈머 그룹을 선언해야 한다.
        // 컨슈머가 중단되거나 재시작되더라도 컨슈머 그룹의 컨슈머 오프셋을 기준으로 이후 데이터를 처리하기 때문이다.
        // 컨슈머 그룹을 선언하지 않으면 어떤 그룹에도 속하지 않는 컨슈머로 동작하게 된다.
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);

        // 프로듀서가 직렬화하여 전송한 데이터를 역직렬화하기 위해 역직렬화 클래스를 지정한다.
        // 프로듀서에서 직렬화한 타입으로 역직렬화해야 한다.
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // 동기 오프셋 커밋으로 설정
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs)) {
            // 컨슈머에게 토픽을 할당하기 위해 subscribe() 메서드를 사용한다.
            // Collection 타입의 String 값들을 인자로 받는다.
            consumer.subscribe(List.of(TOPIC_NAME));

            while (true) {
                // 컨슈머는 poll() 메서드를 호출하여 데이터를 가져와서 처리한다.
                // 지속적으로 데이터를 처리하기 위해 반복 호출을 해야 한다.
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                // 컨슈머는 poll() 메서드를 통해 ConsumerRecord 리스트를 반환한다. poll() 메서드는 Duration 타입을 인자로 받는다.
                // 이 인자값은 브로커로부터 데이터를 가져올 때 컨슈머 버퍼에 데이터를 기다리기 위한 타임아웃 간격을 의미한다.
                for (ConsumerRecord<String, String> record : records) {
                    log.info("record:{}", record);
                }
                // poll() 메서드로 받은 가장 마지막 레코드의 오프셋을 기준으로 커밋한다.
                consumer.commitSync();
            }
        }

    }
}
```
poll() 메서드가 호출된 이후에 commitSync() 메서드를 호출하여 오프셋 커밋을 명시적으로 수행할 수 있다.
그렇기 때문에 동기 오프셋 커밋을 사용할 경우에는 poll() 메서드로 받은 모든 레코드의 처리가 끝난 이후 commitSync() 메서드를 호출해야 한다.
동기 커밋의 경우 브로커로 커밋을 요청하고 커밋이 완료될 때까지 기다린다.
브로커로부터 컨슈머 오프셋 커밋이 완료되었음을 받기전까지 컨슈머는 데이터를 처리하지 않고 기다리기 때문에 자동 커밋이나 비동기 오프셋 커밋보다 
동일 시간당 데이터 처리량이 적다는 특징이 있다.

commitSync()에 파라미터가 들어가지 않으면 poll()로 반환된 가장 마지막 레코드의 오프셋을 기준으로 커밋된다.
개별 레코드 단위로 매번 오프셋을 커밋하고 싶다면 commitSync() 메서드에 Map<TopicPartition, OffsetAndMetadata> 인스턴스를 파라미터로 넣으면 된다.

```java
@Slf4j
public class SyncCommitConsumer {

  private final static String TOPIC_NAME = "test";
  private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";
  private final static String GROUP_ID = "test-group";

  public static void main(String[] args) {
    Properties configs = new Properties();
    configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
    configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

    try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs)) {
      consumer.subscribe(List.of(TOPIC_NAME));

      while (true) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
        HashMap<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();

        for (ConsumerRecord<String, String> record : records) {
          log.info("record:{}", record);
          currentOffset.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
          consumer.commitSync(currentOffset);
        }
      }
    }
  }
}
```
> 비동기 오프셋 커밋

동기 오프셋 커밋을 사용할 경우 커밋 응답을 기다리는 동안 데이터 처리가 일시적으로 중단된다.
더 많은 데이터를 처리하기 위해서 비동기 오프셋 커밋을 사용할 수 있다.
비동기 오프셋 커밋은 commitAsync() 메서드를 호출하여 사용할 수 있다.

```java
@Slf4j
public class AsyncCommitConsumer {


    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private final static String GROUP_ID = "test-group";

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs)) {
            consumer.subscribe(List.of(TOPIC_NAME));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, String> record : records) {
                    log.info("record:{}", record);
                }
                consumer.commitAsync();
            }
        }
    }
}
```
비동기 오프셋 커밋도 동기 커밋과 마찬가지로 poll() 메서드로 리턴된 가장 마지막 레코드를 기준으로 오프셋을 커밋한다.
다만 동기 오프셋 커밋처럼 응답을 기다리지 않는다. 비동기 오프셋 커밋을 사용할 경우 커밋 응답을 받기 때문에 callback 함수를 파라미터로 받아서 결과를 얻을 수 있다.

```java
@Slf4j
public class AsyncCommitCallbackConsumer {


    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private final static String GROUP_ID = "test-group";

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs)) {
            consumer.subscribe(List.of(TOPIC_NAME));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, String> record : records) {
                    log.info("record:{}", record);
                }

                consumer.commitAsync(
                        (map, e) -> {
                            if (e != null) {
                                log.error("Commit failed");
                                log.error(e.getMessage(), e);
                            } else {
                                log.info("commit success");
                            }
                        }
                );
            }
        }
    }
}
```
OffsetCommitCallback 함수는 commitAsync()의 응답을 받을 수 있도록 도와주는 콜백 인터페이스다.
비동기로 받은 커밋 응답은 onComplete() 메서드를 통해 확인할 수 있다.
정상적으로 커밋되었다면 Exception 변수는 null이고, 커밋이 완료된 오프셋 정보가 Map<TopicPartition, OffsetAndMetadata>에 포함되어 있다.
만약 커밋이 실패했다면 Exception 변수에 에러값이 포함되어 있으므로 어떠한 이유로 커밋이 실패했는지 확인할 수 있다.

> 리밸런스 리스너를 가진 컨슈머

컨슈머 그룹에서 컨슈머가 추가 또는 제거되면 파티션을 컨슈머에 재할당하는 과정인 리밸런스가 일어난다.
poll() 메서드를 통해 반환받은 데이터를 모두 처리하기 전에 리밸런스가 발생하면 데이터를 중복 처리할 수 있다.
poll() 메서드를 통해 받은 데이터 중 일부를 처리했으나 커밋하지 않았기 때문이다.

리밸런스 발생 시 데이터를 중복 처리하지 않게 하기 위해서는 리밸런스 발생 시 처리한 데이터를 기준으로 커밋을 시도해야 한다.
리밸런스 발생을 감지하기 위해 카프카 라이브러리는 ConsumerRebalanceListener 인터페이스를 지원한다.
ConsumerRebalanceListener 인터페이스로 구현된 클래스는 onPartitionAssigned() 메서드와 onPartitionRevoked() 메서드로 이루어져 있다.

* `onPartitionAssigned()`: 리밸런스가 끝난 뒤에 파티션이 할당 완료되면 호출되는 메서드이다.
* `onPartitionRevoked()`: 리밸런스가 시작되기 직전에 호출되는 메서드이다.

리밸런스가 시작하기 직전에 커밋을 하면 되므로 `onPartitionRevoked()` 메서드에 커밋을 구현하여 처리할 수 있다.

```java
@Slf4j
public class RebalanceListenerConsumer {


    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private final static String GROUP_ID = "test-group";
    private static KafkaConsumer<String, String> consumer;
    private static HashMap<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // 컨슈머 그룹 이름을 선언한다. 컨슈머 그룹을 통해 컨슈머의 목적을 구분할 수 있다.
        // 컨슈머 그룹을 기준으로 컨슈머 오프셋을 관리하기 때문에 subscribe() 메서드를 사용하여 토픽을 구독하는 경우에는 컨슈머 그룹을 선언해야 한다.
        // 컨슈머가 중단되거나 재시작되더라도 컨슈머 그룹의 컨슈머 오프셋을 기준으로 이후 데이터를 처리하기 때문이다.
        // 컨슈머 그룹을 선언하지 않으면 어떤 그룹에도 속하지 않는 컨슈머로 동작하게 된다.
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);

        // 프로듀서가 직렬화하여 전송한 데이터를 역직렬화하기 위해 역직렬화 클래스를 지정한다.
        // 프로듀서에서 직렬화한 타입으로 역직렬화해야 한다.
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // 명시적으로 오프셋 커밋을 수행할 때는 ENABLE_AUTO_COMMIT_CONFIG을 false로 설정한다.
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        consumer = new KafkaConsumer<>(configs);
        // 컨슈머에게 토픽을 할당하기 위해 subscribe() 메서드를 사용한다.
        // Collection 타입의 String 값들을 인자로 받는다.
        // RebalanceListner를 오버라이드 변수로 포함시킨다.
        consumer.subscribe(List.of(TOPIC_NAME), new RebalanceListener());

        while (true) {
            // 컨슈머는 poll() 메서드를 호출하여 데이터를 가져와서 처리한다.
            // 지속적으로 데이터를 처리하기 위해 반복 호출을 해야 한다.
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

            // 현재 처리한 오프셋을 매번 커밋하기 위해 commitSync() 메서드가 파라미터로 받을 HashMap 타입을 선언한다.
            // 키는 토픽과 파티션 정보가 담긴 TopicPartition 클래스가 되고
            // 값은 오프셋 정보가 담긴 OffsetAndMetadata 클래스가 된다.


            // 컨슈머는 poll() 메서드를 통해 ConsumerRecord 리스트를 반환한다. poll() 메서드는 Duration 타입을 인자로 받는다.
            // 이 인자값은 브로커로부터 데이터를 가져올 때 컨슈머 버퍼에 데이터를 기다리기 위한 타임아웃 간격을 의미한다.
            for (ConsumerRecord<String, String> record : records) {
                log.info("record:{}", record);

                // 처리를 완료한 레코드의 정보를 토대로 키, 값을 설정한다.
                // 이때 주의할 점은 현재 처리한 오프셋에 1을 더한 값을 커밋해야 한다는 점이다.
                // 이후에 컨슈머가 poll()을 수행할 때 마지막으로 커밋한 오프셋부터 레코드를 리턴하기 때문이다.
                currentOffset.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
                consumer.commitSync(currentOffset);
            }
        }

    }

    private static class RebalanceListener  implements ConsumerRebalanceListener {
        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> collection) {
            log.warn("onPartitionsRevoked");
            // 리밸런스가 발생하면 가장 마지막으로 처리 완료한 레코드를 기준으로 커밋을 실시한다.
            // 이를 통해 데이터 처리의 중복을 방지할 수 있다.
            consumer.commitSync(currentOffset);
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> collection) {
            log.warn("onPartitionsAssigned");
        }
    }

}
```
> 파티션 할당 컨슈머

컨슈머를 운영할 때 subscribe() 메서드를 사용하여 구독 형태로 사용하는 것 외에도 직접 파티션을 컨슈머에 명시적으로 할당하여 운영할 수도 있다.
컨슈머가 어떤 토픽, 파티션을 할당할지 명시적으로 선언할 때는 assign() 메서드를 사용하면 된다.
assign() 메서드는 다수의 TopicPartition 인스턴스를 지닌 자바 컬렉션 타입을 파라미터로 받는다.
TopicPartition 클래스가 카프카 라이브러리 내/외부에서 사용되는 토픽, 파티션의 정보를 담는 객체로 사용된다.

```java
@Slf4j
public class ExactConsumer {

    private final static String TOPIC_NAME = "test";
    private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private final static String GROUP_ID = "test-group";
    private final static int PARTITION_NUMBER = 0;

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // 컨슈머 그룹 이름을 선언한다. 컨슈머 그룹을 통해 컨슈머의 목적을 구분할 수 있다.
        // 컨슈머 그룹을 기준으로 컨슈머 오프셋을 관리하기 때문에 subscribe() 메서드를 사용하여 토픽을 구독하는 경우에는 컨슈머 그룹을 선언해야 한다.
        // 컨슈머가 중단되거나 재시작되더라도 컨슈머 그룹의 컨슈머 오프셋을 기준으로 이후 데이터를 처리하기 때문이다.
        // 컨슈머 그룹을 선언하지 않으면 어떤 그룹에도 속하지 않는 컨슈머로 동작하게 된다.
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);

        // 프로듀서가 직렬화하여 전송한 데이터를 역직렬화하기 위해 역직렬화 클래스를 지정한다.
        // 프로듀서에서 직렬화한 타입으로 역직렬화해야 한다.
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);
        // subscribe() 대신 assign() 메서드를 사용한다.
        // test 토픽의 0번 파티션을 할당하여 레코드를 가져오는 설정이 적용된다.
        // subscribe() 메서드를 사용할 때와 다르게 직접 컨슈머가 특정 토픽, 특정 파티션에 할당되므로 리밸런싱하는 과정이 없다.
        consumer.assign(Collections.singleton(new TopicPartition(TOPIC_NAME, PARTITION_NUMBER)));

        // 컨슈머에게 토픽을 할당하기 위해 subscribe() 메서드를 사용한다.
        // Collection 타입의 String 값들을 인자로 받는다.
        consumer.subscribe(List.of(TOPIC_NAME));

        while (true) {
            // 컨슈머는 poll() 메서드를 호출하여 데이터를 가져와서 처리한다.
            // 지속적으로 데이터를 처리하기 위해 반복 호출을 해야 한다.
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

            // 컨슈머는 poll() 메서드를 통해 ConsumerRecord 리스트를 반환한다. poll() 메서드는 Duration 타입을 인자로 받는다.
            // 이 인자값은 브로커로부터 데이터를 가져올 때 컨슈머 버퍼에 데이터를 기다리기 위한 타임아웃 간격을 의미한다.
            for (ConsumerRecord<String, String> record : records) {
                log.info("{}", record);
            }
        }
    }
}
```

> 컨슈머에 할당된 파티션 확인 방법

컨슈머에 할당된 토픽과 파티션에 대한 정보는 `assignment()` 메서드로 확인할 수 있다.
`assignment()` 메서드는 Set<TopicPartition> 인스턴스를 반환한다. TopicPartition 클래스는 토픽 이름과 파티션 번호가 포함된 객체이다.

```java
KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);
consumer.subscribe(List.of(TOPIC_NAME));
Set<TopicPartition> assignedTopicPartition = consumer.assignment();
```

> 컨슈머의 안전한 종료

컨슈머의 안전한 종료
컨슈머 애플리케이션은 안전하게 종료되어야 한다.
정상적으로 종료되지 않은 컨슈머는 세션 타임아웃이 발생할때까지 컨슈머 그룹에 남게 된다.

이로 인해 실제로는 종료되었지만 더는 동작하지 않는 컨슈머가 존재하기 때문에 파티션의 데이터는 소모되지 못하고 컨슈머 랙이 늘어나게 된다.
컨슈머 랙이 늘어나면 데이터 처리 지연이 발생하게 된다.

컨슈머를 안전하게 종료하기 위해 KafkaConsumer 클래스는 `wakeup()` 메서드를 지원한다.
`wakeup()` 메서드를 실행하여 kafkaConsumer 인스턴스를 안전하게 종료할 수 있다.
`wakeup()` 메서드가 실행된 이후 poll() 메서드가 호출되면 WakeupException 예외가 발생한다.
WakeupException 예외를 받은 뒤에는 데이터 처리를 위해 사용한 자원들을 해제하면 된다.
마지막에는 close() 메서드를 호출하여 카프카 클러스터에 컨슈머가 안전하게 종료되었음을 명시적으로 알려주면 종료가 완료되었다고 볼 수 있다.
close() 메서드를 호출하면 해당 컨슈머는 더이상 동작하지 않는다.
명시적으로 알려주었으므로 컨슈머 그룹에서 이탈되고 나머지 컨슈머들이 파티션을 할당받게 된다.

```java
@Slf4j
public class SyncOffsetCommitShutdownHook {


  private final static String TOPIC_NAME = "test";
  private final static String BOOTSTRAP_SERVERS = "my-kafka:9092";
  private final static String GROUP_ID = "test-group";
  private static KafkaConsumer<String, String> consumer;

  public static void main(String[] args) {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      log.info("Shutting down...");
      consumer.wakeup();
    }));

    Properties configs = new Properties();
    configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

    // 컨슈머 그룹 이름을 선언한다. 컨슈머 그룹을 통해 컨슈머의 목적을 구분할 수 있다.
    // 컨슈머 그룹을 기준으로 컨슈머 오프셋을 관리하기 때문에 subscribe() 메서드를 사용하여 토픽을 구독하는 경우에는 컨슈머 그룹을 선언해야 한다.
    // 컨슈머가 중단되거나 재시작되더라도 컨슈머 그룹의 컨슈머 오프셋을 기준으로 이후 데이터를 처리하기 때문이다.
    // 컨슈머 그룹을 선언하지 않으면 어떤 그룹에도 속하지 않는 컨슈머로 동작하게 된다.
    configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);

    // 프로듀서가 직렬화하여 전송한 데이터를 역직렬화하기 위해 역직렬화 클래스를 지정한다.
    // 프로듀서에서 직렬화한 타입으로 역직렬화해야 한다.
    configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

    consumer = new KafkaConsumer<>(configs);
    try {
      // 컨슈머에게 토픽을 할당하기 위해 subscribe() 메서드를 사용한다.
      // Collection 타입의 String 값들을 인자로 받는다.
      consumer.subscribe(List.of(TOPIC_NAME));

      while (true) {
        // 컨슈머는 poll() 메서드를 호출하여 데이터를 가져와서 처리한다.
        // 지속적으로 데이터를 처리하기 위해 반복 호출을 해야 한다.
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

        // 컨슈머는 poll() 메서드를 통해 ConsumerRecord 리스트를 반환한다. poll() 메서드는 Duration 타입을 인자로 받는다.
        // 이 인자값은 브로커로부터 데이터를 가져올 때 컨슈머 버퍼에 데이터를 기다리기 위한 타임아웃 간격을 의미한다.
        for (ConsumerRecord<String, String> record : records) {
          log.info("{}", record);
        }
      }
    } catch (Exception e) {
      log.warn("Wakeup consumer");
      log.error(e.getMessage(), e);
    } finally {
      consumer.close();
    }
  }
}
```

### 3.4.3 어드민 API
카프카 클라이언트는 내부 옵션들을 설정하거나 조회하기 위해 `AdminClient` 클래스를 제공한다.
AdminClient 클래스를 활용하면 클러스터의 옵션과 관련된 부분을 자동화할 수 있다.

KafkaAdminClient 주요 메서드
* describeCluster(DescribeClusterOptions options): 브로커 정보조회
* listTopics(ListTopicsOptions options): 토픽 리스트 조회
* listConsumerGroup(ListConsumerGroupsOptions options): 컨슈머 그룹 조회
* createTopics(Collection<NewTopic> newTopics, CreateTopicsOption options): 신규 토픽 생성
* createAcls(Collection<AclBinding> acls, CreateAclsOptions options): 접근 제어 규칙 생성

```java
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
```
어드민 API를 활용할 때 클러스터의 버전과 클라이언트의 버전을 맞춰서 사용해야 한다. 어드민 API의 많은 부분이 버전이 올라가면서 자주 바뀌기 때문이다.

## 3.5 카프카 스트림즈
카프카 스트림즈는 토픽에 적재된 데이터를 상태기반 또는 비상태기반으로 실시간 변환하여 다른 토픽에 적재하는 라이브러리이다.
카프카의 스트림 데이터를 처리하기 위해 아파치 스파크, 아파치 플링크, 아파치 스톰, 플루언티드와 같은 다양한 오픈소스 애플리케이션이 존재하지만
카프카 스트림즈를 사용해야 하는 이유는 무엇일까?

스트림즈는 카프카에서 공식적으로 지원하는 라이브러리이다.
카프카 버전이 오를 때마다 스트림즈 라이브러리도 같이 릴리즈 된다.
카프카 클러스터와 완벽하게 호환되면서 스트림 처리에 필요한 기능들을 제공한다.
카프카 클러스터를 운영하면서 실시간 스트림 처리를 해야하는 필요성이 있다면
카프카 스트림즈 애플리케이션으로 개발하는 것을 1순위로 고려하는 것이 좋다.

카프카 스트림즈의 구조와 사용법을 알기 위해 우선 토폴로지와 관련된 개념을 익혀야 한다.
토폴로지란 2개 이상의 노드들과 선으로 이루어진 집합을 뜻한다.
토포롤지의 종류로는 링형, 트리형, 성형 등이 있는데 스트림즈에서 사용하는 토폴로지는 트리 형태와 유사하다.

카프카 스트림즈에서 토폴로지를 이루는 노드를 하나의 프로세서라고 부르고
노드와 노드를 이은 선을 스트림이라고 부른다.
스트림은 토픽의 데이터를 뜻하는데 프로듀서와 컨슈머에서 활용했던 레코드와 동일하다.

프로세서에서는 소스 프로세서, 스트림 프로세서, 싱크 프로세서 3가지가 있다.
* 소스 프로세서: 데이터를 처리하기 위해 최초로 선언해야하는 노드로 하나 이상의 토픽에서 데이터를 가져오는 역할을 한다. 
* 스트림 프로세서: 다른 프로세서가 반환한 데이터를 처리하는 역할을 한다. 변환, 분기처리와 같은 로직이 데이터 처리의 일종이라고 볼 수 있다.
* 싱크 프로세서: 데이터를 특정 카프카 토픽으로 저장하는 역할을 하며 스트림즈로 처리된 데이터의 최종 종착지다.

스트림즈 DSL과 프로세서 API 2가지 방법으로 개발 가능하다.
스트림즈 DSL은 스트림 프로세싱에 쓰일 만한 다양한 기능들을 자체 API로 만들어 놓았기 때문에 대부분 변환 로직을 어렵지 않게 개발할 수 있다.
만약 스트림즈 DSL에서 제공하지 않는 일부 기능들의 경우 프로세서 API를 사용하여 구현할 수 있다.
스트림즈 DSL과 프로세서 API가 구현할 수 있는 종류는 다음과 같다.

* 스트림즈 DSL로 구현하는 데이터 처리 예시
  * 메시지 값을 기반으로 토픽 분기 처리
  * 지난 10분간 들어온 데이터의 개수 집계
  * 토픽과 다른 토픽의 결합으로 새로운 데이터 생성
* 프로세서 API로 구현하는 데이터 처리 예시
  * 메시지 값의 종류에 따라 토픽을 가변적으로 전송
  * 일정한 시간 간격으로 데이터 처리

### 3.5.1 스트림즈 DSL
스트림즈 DSL로 구성된 애플리케이션을 코드로 구현하기 전에 스트림즈 DSL에서 다루는 새로운 개념들에 대해 짚고 넘어가야 한다.
스트림즈 DSL에는 레코드의 흐름을 추상화한 3가지 개념인 `KStream`, `KTable`, `GlobalKTable`이 있다.
이 3가지 개념은 컨슈머, 프로듀서, 프로세서 API에서는 사용되지 않고 스트림즈 DSL에서만 사용되는 개념이다.

> KStream

KStream은 레코드의 흐름을 표현한 것으로 메시지 키와 메시지 값으로 구성되어있다.
KStream으로 데이터를 조회하면 토픽에 존재하는 (또는 KStream에 존재하는) 모든 레코드가 출력된다.
KStream은 컨슈머로 토픽을 구독하는 것과 동일한 선상에서 사용하는 것이라고 볼 수 있다.

> KTable

KTable은 KStream과 다르게 메시지 키를 기준으로 묶어서 사용한다.
KStream은 토픽의 모든 레코드를 조회할 수 있지만 KTable은 유니크한 메시지 키를 기준으로 가장 최신 레코드를 사용한다.
그로므로 KTable로 데이터를 조회하면 메시지 키를 기준으로 가장 최신에 추가된 레코드의 데이터가 출력된다.
새로 데이터를 적재할 때 동일한  메시지 키가 있을 겨웅 데이터가 업데이트 되었다고 볼 수 있다.

> GlobalKTable

GlobalKTable은 KTable과 동일하게 메시지 키를 기준으로 묶어서 사용된다.
그러나 KTable로 선언된 토픽은 1개 파티션이 1개 태스크에 할당되어 사용되고, 
GlobalKTable로 선언된 토픽은 모든 파티션 데이터가 각 태스크에 할당되어 사용된다는 차이점이 있다.

KStream과 KTable을 조인하려면 반드시 코파티셔닝(co-partitioning) 되어야 한다.
코파티셔닝이란 조인을 하는 2개 데이터의 파티션 개수가 동일하고 파티셔닝 전략을 동일하게 맞추는 작업이다.
파티션 개수가 동일하고 파티셔닝 전략이 같은 경우에는 동일한 메시지 키를 가진 데이터가 동일한 태스크에 들어가는 것을 보장한다.
이를 통해 각 태스크는 KStream의 레코드와 KTable의 메시지 키가 동일할 경우 조인을 수행할 수 있다.

문제는 조인을 수행하려는 토픽들이 코파티셔닝되어 있음을 보장할 수 없다는 것이다.
KStream과 KTable로 사용하는 2개의 토픽이 파티션 개수가 다를 수 도 있고 파티션 전략이 다를 수 있다.
이런 경우에는 조인을 수행할 수 없다.
코파티셔닝이 되지 않은 2개의 토픽을 조인하는 로직이 담긴 스트림즈 애플리케이션을 실항하면 TopologyException이 발생한다.

조인을 수행하는 KStream과 KTable이 코파티셔닝되어 있지 않으면 KStream 또는 KTable을 리파티셔닝하는 과정을 거쳐야 한다.
리파티셔닝이란 새로운 토픽에 새로운 메시지 키를 가지도록 재배열하는 과정이다. 리파티셔닝 과정을 거쳐 KStream 토픽과 KTable로 사용하는 토픽이 코파티셔닝되도록 할 수 있다.

리파티셔닝을 하는 과정은 토픽에 기존 데이터를 중복해서 생성할 뿐만 아니라 파티션을 재배열하기 위해 프로세싱하는 과정도 거쳐야 한다.
이렇게 코파티셔닝되지 않은 KStream과 KTable을 조인해서 사용하고 싶다면 KTable을 GlobalKTable로 선언하여 사용하면 된다.
GlobalKTable은 코파티셔닝되지 않은 KStream과 데이터 조인을 할 수 있다.
GlobalKTable로 정의된 데이터는 스트림즈 애플리케이션의 모든 태스크에 동일하게 공유되어 사용되기 때문이다.

다만 GlobalKTable을 사용하면 각 태스크마다 GlobalKTable로 정의된 모든 데이터를 저장하고 사용하기 때문에 
스트림즈 애플리케이션의 로컬 스토리지의 사용량이 증가하고 네트워크, 브로커에 부하가 생기므로 되도록이면 작은 용략의 데이터일 경우에만 사용하는 것이 좋다.
많은 양의 데이터를 가진 토픽으로 조인할 경우에는 리파티셔닝을 통해 KTable을 사용하는 것을 권장한다.

> 스트림즈 DSL 주요 옵션

스트림즈 DSL 애플리케이션을 실행할 때 설정해야 하는 필수 옵션과 선택 옵션이 있다.
필수 옵션은 사용자가 반드시 설정해야 하는 옵션이다.

* 필수 옵션
  * `bootstrap-server`: 프로듀서가 데이터를 전송할 대상 카프카 클러스터에 속한 브로커의 호스트 이름:포트를 1개 이상 작성한다. 2개 이상 브로커 정보를 입력하여 브로커에 이슈가 발생하더라도 접속하는 데에 이슈가 없도록 설정가능하다.
  * `application.id`: 스트림즈 애플리케이션을 구분하기 위한 고유한 아이디어를 설정한다. 다른 로직을 가진 스트림즈 애플리케이션들은 서로 다른 application.id를 가진다.
* 선택 옵션
  * `default.key.serde`: 레코드의 메시지 키를 직렬화, 역직렬화하는 클래스를 지정한다. 기본값은 바이트 직렬화, 역직렬화 클래스인 `Serdes.ByteArray().getClass().getName()`이다.
  * `default.value.serde`: 레코드의 메시지 값을 직렬화, 역직렬화하는 클래스를 지정한다. 기본값은 바이트 직렬화, 역직렬화 클래스인 `Serdes.ByteArray().getClass().getName()`이다.
  * `num.stream.threads`: 스트림 프로세싱 실행 시 실행될 스레드 개수를 지정한다. 기본값은 1이다.
  * `state.dir`: rockDB 저장소가 위치할 디렉토리를 지정한다. rockDB는 페이스북이 개발한 고성능의 key-value DB로서 카프카 스트림즈가 상태기반 데이터 처리를 할 때 로컬 저장소로 사용한다.
  기본값으 `/tmp/kafka-streams`이다. 스트림즈 애플리케이션을 상용에 배포할때는 /tmp 디렉토리가 아닌 별도로 관리되는 디렉토리로 지정해야 안전하게 데이터가 저장된다.

> 스트림즈 DSL - stream(), to()

```java
@Slf4j
public class SimpleStream {

    private static String APPLICATION_NAME = "streams-application";
    private static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private static String STREAM_LOG = "stream_log";
    private static String STREAM_LOG_COPY = "streams_log_copy";

    public static void main(String[] args) {
        Properties props = new Properties();

        // 스트림즈 애플리케이션은 애플리케이션 아이디를 지정해야 한다.
        // 애플리케이션 아이디 값을 기준으로 병렬처리하기 때문이다.
        // 기존에 사용하지 않은 이름을 아이디로 사용해야 한다.
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);

        // 스트림즈 애플리케이션과 연동할 카프카 클러스터 정보를 입력한다.
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // 스트림 처리를 위한 메시지 키와 값의 역직렬화, 직렬화 방식을 지정한다.
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // StreamBuilder는 스트림 토폴로지를 정의하기 위한 용도로 사용된다.
        StreamsBuilder builder = new StreamsBuilder();

        // stream_log 토픽으로부터 KStream 객체를 만들기 위해 StreamBuilder의 stream() 메서드를 사용했다.
        // StreamBuilder는 stream() 외에 KTable을 만드는 table(), GlobalKTable을 만드는 globalTable() 메서드를 지원한다.
        // stream(), table(), globalTable() 메서드들은 최초의 토픽 데이터를 가져오는 소스 프로세서 이다.
        KStream<String, String> streamLog = builder.stream(STREAM_LOG);

        // stream_log 토픽을 담은 KStream 객체를 다른 토픽으로 전송하기 위해 to() 메서드를 사용했다.
        // to() 메서드는 KStream() 인스턴스의 데이터들을 특정 토픽으로 저장하기 위한 용도로 사용된다. 즉 싱크 프로세서이다.
        streamLog.to(STREAM_LOG_COPY);

        // StreamBuilder로 정의한 토폴로지에 대한 정보와 스트림즈 실행을 위한 기본 옵션을 파라미터로 KafkaStreams 인스턴스를 생성한다.
        // KafkaStreams 인스턴스를 실행하려면 start() 메서드를 사용하면된다.
        // 이 스트림 애플리케이션은 stream_log 토픽의 데이터를 stream_log_copy 토픽으로 전달한다.
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }
}
```
스트림즈 애플리케이션을 실행하기 전에 먼저 스트림즈의 소스 프로세서에서 사용하는 토픽을 생성해야한다.

```shell
bin/kafka-topics.sh --create --bootstrap-server my-kafka:9092 --partitions 3 --topic stream_log
```

```shell
Created topic stream_log.
```

```shell
$ bin/kafka-console-producer.sh --bootstrap-server my-kafka:9092 --topic stream_log
>hello
>kafka
>stream 
```
```shell
``$ bin/kafka-console-consumer.sh --bootstrap-server my-kafka:9092 --topic stream_log_copy --from-beginning
hello
kafka
stream
```

> 스트림 DSL - filter()

토픽으로 들어온 문자열 데이터중 문자열의 길이가 5보다 큰 경우만 필터링하는 스트림즈 애플리케이션을 스트림 프로세서를 사용하여 만들 수 있다.
메시지 키 또는 메시지 값을 필터링하여 특정 조거에 맞는 데이터를 골라낼 때는 `filter()` 메서드를 사용하면 된다.
`filter()` 메서드는 스트림즈 DSL에서 사용 가능한 필터링 스트림 프로세서이다.

```java
@Slf4j
public class FilterStream {

    private static String APPLICATION_NAME = "streams-application";
    private static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private static String STREAM_LOG = "stream_log";
    private static String STREAM_LOG_FILTER = "stream_log_filter";

    public static void main(String[] args) {
        Properties props = new Properties();

        // 스트림즈 애플리케이션은 애플리케이션 아이디를 지정해야 한다.
        // 애플리케이션 아이디 값을 기준으로 병렬처리하기 때문이다.
        // 기존에 사용하지 않은 이름을 아이디로 사용해야 한다.
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);

        // 스트림즈 애플리케이션과 연동할 카프카 클러스터 정보를 입력한다.
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // 스트림 처리를 위한 메시지 키와 값의 역직렬화, 직렬화 방식을 지정한다.
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // StreamBuilder는 스트림 토폴로지를 정의하기 위한 용도로 사용된다.
        StreamsBuilder builder = new StreamsBuilder();

        // stream_log 토픽으로부터 KStream 객체를 만들기 위해 StreamBuilder의 stream() 메서드를 사용했다.
        // StreamBuilder는 stream() 외에 KTable을 만드는 table(), GlobalKTable을 만드는 globalTable() 메서드를 지원한다.
        // stream(), table(), globalTable() 메서드들은 최초의 토픽 데이터를 가져오는 소스 프로세서 이다.
        KStream<String, String> streamLog = builder.stream(STREAM_LOG);

        // 데이터를 필터링하는 filter() 메서드는 자바의 함수형 인터페이스인 Predicate를 파라미터로 받는다.
        // Predicate는 함수형 인터페이스로 특정 조건을 표현할 때 사용할 수 있는데, 여기서는 메시지 키와 메시지 값에 대한 조건을 나타낸다.
        KStream<String, String> filteredStream = streamLog.filter((key, value) -> value.length() > 5);

        // 필터링된 KStream을 stream_log_filter 토픽에 저장하도록 소스프로세스를 작성하였다.
        filteredStream.to(STREAM_LOG_FILTER);

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }
}
```
```shell
$ bin/kafka-console-producer.sh --bootstrap-server my-kafka:9092 --topic stream_log
>apache
>kafka
>streams
>test
```

```shell
$ bin/kafka-console-consumer.sh --bootstrap-server my-kafka:9092 --topic stream_log_filter --from-beginning
apache
streams
```

> 스트림 DSL - KTable과 KStream을 join()

KTable과 KStream은 다음 메시지 키를 기준으로 조인할 수 있다.
카프카에서는 실시간으로 들어오는 데이터들을 조인할 수 있다.

이름을 메시지 키, 주소를 메시지 값으로 가지고 있는 KTable이 있고
이름을 메시지 키, 주문한 물품을 메시지 값으로 가지고 있는 KStream이 있다고 가정하자.

사용자가 물품을 주문하면 이미 토픽에서 저장된 이름:주소로 구성된 KTable과 조인하여 물품과 주소가 조합된 데이터를 새로 생성할 수 있다.
사용자의 이벤트 데이터를 데이터베이스에 저장하지 않고도 조인하여 스트리밍 처리할 수 있다는 장점이 있다.
이를 통해 이벤트 기반 스트리밍 데이터 파이프라인을 구성할 수 있다.

KTable과 KStream을 조인할 때 가장 중요한 것은 코파티셔닝이 되어 있는지 확인하는 것이다.
코파티셔닝되어 있지 않는 상태에서 KTable과 KStream을 조인하면 스트림 프로세서에서 TopologyException을 발생시키기 때문이다.
그러므로 KTable로 사용할 토픽과 KStream으로 사용할 토픽을 생성할 때 동일한 파티션 개수, 동일한 파티셔닝을 사용하는 것이 중요하다.
KTable로 사용할 토픽과 KSteram으로 사용할 토픽을 만들 때 둘다 파티션을 3개로 동일하게 만든다.
파티셔닝 전략은 기본 파티셔너를 사용한다.
KTable로 사용할 토픽은 address이고 KStream으로 사용할 토픽은 order이다.
그리고 조인된 데이터를 저장할 토픽은 order_join으로 생성한다.

```shell
$ bin/kafka-topics.sh --create --bootstrap-server my-kafka:9092 --partitions 3 --topic address
$ bin/kafka-topics.sh --create --bootstrap-server my-kafka:9092 --partitions 3 --topic order
$ bin/kafka-topics.sh --create --bootstrap-server my-kafka:9092 --partitions 3 --topic order_join
```

```shell
@Slf4j
public class KstreamKtableJoin {

    private static String APPLICATION_NAME = "order-join-application";
    private static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private static String ADDRESS_TABLE = "address";
    private static String ORDER_STREAM = "order";
    private static String ORDER_JOIN_STREAM = "order_join";

    public static void main(String[] args) {
        Properties props = new Properties();

        // 스트림즈 애플리케이션은 애플리케이션 아이디를 지정해야 한다.
        // 애플리케이션 아이디 값을 기준으로 병렬처리하기 때문이다.
        // 기존에 사용하지 않은 이름을 아이디로 사용해야 한다.
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);

        // 스트림즈 애플리케이션과 연동할 카프카 클러스터 정보를 입력한다.
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // 스트림 처리를 위한 메시지 키와 값의 역직렬화, 직렬화 방식을 지정한다.
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());


        // StreamBuilder는 스트림 토폴로지를 정의하기 위한 용도로 사용된다.
        StreamsBuilder builder = new StreamsBuilder();

        // KTable과 KStream을 생성한다.
        KTable<String, String> addressTable = builder.table(ADDRESS_TABLE);
        KStream<String, String> orderStream = builder.stream(ORDER_STREAM);

        // 조인을 위해 KStream 인스턴스에 정의되어 있는 join() 메서드를 사용한다. 첫번째 파라미터로 조인을 수행할 KTable 인스턴스를 넣는다.
        // KStream과 KTable에서 동일한 메시지 키를 가진 데이터를 찾았을 경우 각각의 메시지 값을 조합해서 어떤 데이터를 만들지 결정한다.
        // 조인을 통해 성생된 데이터를 order_join 토픽에 저장하기 위해 to() 싱크 프로세서를 사용 한다.
        orderStream.join(addressTable, (orderValue, addressValue) -> orderValue + " send to " + addressValue)
                .to(ORDER_JOIN_STREAM);


        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }
}
```
```shell
$ bin/kafka-console-producer.sh --bootstrap-server my-kafka:9092 --topic address --property "parse.key=true" --property "key.separator=:"
>kim:Seoul
>Lee:Busan
>Park:Naju
```

```shell
$ bin/kafka-console-producer.sh --bootstrap-server my-kafka:9092 --topic order --property "parse.key=true" --property "key.separator=:"
>kim:PC
>Lee:Monitor
```
```shell
Lee:Monitor send to Busan
kim:PC send to Seoul
```
KTable에 존재하는 메시지 키를 기준으로 KStream이 데이터를 조인하여 order_join 토픽에서는 물품과 주소 데이터가 합쳐진 것을 볼 수 있다.
조인할 때 사용했던 메시지 키는 조인이 된 데이터의 메시지 키로 들어간다.

만약 사용자의 주소가 변경되는 경우는 어떻게 될까?
KTable은 동일한 메시지 키가 들어올 경우 가장 마지막의 레코드를 유효한 데이터로 보기 때문에 가장 최근에 바뀐 주소로 조인을 수행할 것이다.
현재 kim 사용자 주소가 Seoul인데 Jeju로 변경 되도록 address 레코드를 추가 해보자.

```shell
bin/kafka-console-producer.sh --bootstrap-server my-kafka:9092 --topic address --property "parse.key=true" --property "key.separator=:"
>kim:Jeju

bin/kafka-console-producer.sh --bootstrap-server my-kafka:9092 --topic order --property "parse.key=true" --property "key.separator=:"
>kim:Keyboard
```
```shell
$ bin/kafka-consoconsumer.sh --bootstrap-server my-kafka:9092 --topic order_join --property print.key=true --property key.separator=":" --from-beginning
Lee:Monitor send to Busan
kim:PC send to Seoul
kim:Keyboard send to Jeju
```

> 스트림즈 DSL - GlobalKTable과 KStream을 join()

order 토픽과 address 토픽은 코파티셔닝되어 있으므로 각각 KStream과 KTable로 선언해서 조인을 할 수 있었다.
그러나 코파티셔닝되어 있지 않은 토픽을 조인해야할 때는 어떻게 해야할까?
코파티셔닝되지 않은 데이터를 조인하는 방법은 두 가지가 있다.

1. 리파티셔닝을 수행한 이후에 코파티셔닝이 된 상태로 조인 처리
2. KTable로 사용하는 토픽을 GlobalKTable로 선언하여 사용

GlobalKTable로 토픽을 선언해서 사용해 보겠다.
파티션 개수가 다른 2개의 토픽을 조인하는 예제를 GlobalKTable로 선언해 작성해 볼 것인데
파티션 2개로 이루어진 address_v2 토픽을 생성한다.

```shell
$ bin/kafka-topics.sh --create --bootstrap-server my-kafka:9092 --partitions 2 --topic address_v2
```

```java
@Slf4j
public class KstreamGlobalKtableJoin {

    private static String APPLICATION_NAME = "order-join-application";
    private static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private static String ADDRESS_TABLE = "address_v2";
    private static String ORDER_STREAM = "order";
    private static String ORDER_JOIN_STREAM = "order_join";

    public static void main(String[] args) {
        Properties props = new Properties();

        // 스트림즈 애플리케이션은 애플리케이션 아이디를 지정해야 한다.
        // 애플리케이션 아이디 값을 기준으로 병렬처리하기 때문이다.
        // 기존에 사용하지 않은 이름을 아이디로 사용해야 한다.
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);

        // 스트림즈 애플리케이션과 연동할 카프카 클러스터 정보를 입력한다.
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // 스트림 처리를 위한 메시지 키와 값의 역직렬화, 직렬화 방식을 지정한다.
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());


        // StreamBuilder는 스트림 토폴로지를 정의하기 위한 용도로 사용된다.
        StreamsBuilder builder = new StreamsBuilder();

        // GlobalKTable과 KStream을 생성한다.
        GlobalKTable<String, String> addressGlobalTable = builder.globalTable(ADDRESS_TABLE);
        KStream<String, String> orderStream = builder.stream(ORDER_STREAM);

        // 첫번째 파라미터에는 GlobalKTable 인스턴스를 입력한다.
        // GlobalKTable은 KTable의 조인과 다르게 레코드를 매칭할 때 KStream의 메시지 키와 메시지 값 둘다 사용할 수 있다.
        // 여기서는 KStream의 메시지 키를 GlobalKTable의 메시지 키와 매칭하도록 설정했다.
        orderStream.join(addressGlobalTable, (orderKey, orderValue) -> orderKey, (orderValue, addressValue) -> orderValue + " send to " + addressValue)
                .to(ORDER_JOIN_STREAM);


        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }
}
```
```shell
$ bin/kafka-console-producer.sh --bootstrap-server my-kafka:9092 --topic address_v2 --property "parse.key=true" --property "key.separator=:"
>kim:Seoul
>Lee:Busan
```
```shell
$ bin/kafka-console-producer.sh --bootstrap-server my-kafka:9092 --topic order --property "parse.key=true" --property "key.separator=:"
>kim:mouse
>Lee:speaker
```

```shell
$ bin/kafka-console-consumer.sh --bootstrap-server my-kafka:9092 --topic order_join --property print.key=true --property key.separator=":" --from-beginning
Lee:Monitor send to Busan
Lee:speaker send to Busan
kim:PC send to Seoul
kim:Keybourd send to Jeju
kim:mouse send to Seoul
```

언뜻 결과물을 보면 KTable과 크게 다르지 않아 보인다.
그러나 GlobalKTable로 선언한 토픽은 토픽에 존재하는 모든 데이터를 태스크마다 저장하고 조인 처리를 수행하는 점이 다르다.
그리고 조인을 수행할 때 KStream의 메시지 키뿐만 아니라 메시지 값을 기준으로도 매칭하여 조인할 수 있다는 점도 다르다.

### 3.5.2 프로세서 API
프로세서 API는 스트림즈 DSL 보다 투박한 코드를 가진다.
토폴로지를 기준으로 데이터를 처리한다는 관점에서는 동일한 역할을 한다.
스트림즈 DSL이 제공하지 않는 상세 로직의 구현이 필요하면 프로세서 API를 활용해야 한다.
프로세서 API는 스트림즈 DSL의 KStream, KTable, GlobalKTable 개념이 없다는 점을 주의해야 한다.

```java
@Slf4j
public class FilterProcessor implements Processor<String, String> {
    // 스트림 프로세서 클래스를 생성하기 위해서는 kafka-streams 라이브러리에서 제공하는 Processor 또는 Transformer 인터페이스를 사용해야 한다.

    // 프로세서에 대한 정보를 담고 있다.
    // ProcessorContext 클래스로 생성된 인스턴스로 현재 스트림 처리 중인 토폴로지의 토픽 정보, 애플리케이션 아이디를 조회할 수 있다.
    // schedule(), forward(), commit() 등 프로세싱 처리에 필요한 메서드를 사용할 수 있다.
    private ProcessorContext context;


    /**
     * init() 메서드는 스트림프로세서의 생성자이다.
     * 프로세싱 처리에 필요한 리소스를 선언하는 구문이 들어갈 수 있다.
     */
    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    /**
     *  실질적인 프로세싱 로직이 들어가는 부분이다. 1개의 레코드를 받는 것을 가정하여 데이터를 처리하면 된다.
     *  메시지 키, 메시지 값을 파라미터로 받는다.
     *  필터링된 데이터의 경우 forward() 메서드를 사용하여 다음 토폴로지(다음 프로세서)로 넘어가도록 한다.
     *  처리가 완료된 경우에는 commit()을 호출하여 명시적으로 데이터가 처리되었음을 선언한다.
     */
    @Override
    public void process(String key, String value) {
        if (value.length() > 5) {
            context.forward(key, value);
        }
        context.commit();
    }

    /**
     * FilterProcessor가 종료되기 전에 호출되는 메서드이다.
     * 프로세싱을 하기 위해 사용했떤 리소스를 해제하는 구문을 넣는다.
     */
    @Override
    public void close() {
        log.info("Closing Processor");
    }
}
```

```java
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
```
```shell
$ bin/kafka-console-producer.sh --bootstrap-server my-kafka:9092 --topic stream_log
>hello
>kafka
>streams
>api
```

```shell
$ bin/kafka-console-consumer.sh --bootstrap-server my-kafka:9092 --topic stream_log_filter --from-beginning
streams
```

## 3.6 카프카 커넥트
카프카 커넥트는 카프카 오픈소스에 포함된 툴 중 하나로 데이터 파이프라인 생성 시 반복 작업을 줄이고 효율적인 전송을 이루기 위한 애플리케이션이다.
파이프라인을 생성할 때 프로듀서, 컨슈머 애플리케이션을 만드는 것은 좋은 방법이지만
반복적인 파이프라인 생성 작업이 있을 때는 매번 프로듀서, 컨슈머 애플리케이션을 개발하고 배포, 운영해야 하기 때문에 비효율 적이다.

반면 커넥트는 특정한 작업 형태를 템플릿으로 만들어 놓은 커넥터를 실행함으로써 반복작업을 줄일 수 있다.
파이프라인 생성 시 자주 반복되는 값들을 파라미터로 받는 커넥터를 코드로 작성하면
이후에 파이프라인을 실행할 때는 코드를 작성할 필요가 없기 때문이다.
커넥터는 각 커넥터가 가진 고유한 설정값을 입력받아서 데이터를 처리한다.

> 커넥트를 실행하는 방법

커넥트를 실행하는 방법은 크게 두 가지가 있다.

1. 단일 모드 커넥트
2. 분산 모드 커넥트

단일 모드 커넥트는 1개 프로세스만 실행되는 점이 특징이다.
단일 프로세스로 실행되기 때문에 고가용성 구성이 되지 않아 단일 장애점(SPOF: Single Point Of Failure)이 될 수 있다.
그러므로 단일 모드 커넥트 파이프 라인은 주로 개발환경이나 중요도가 낮은 파이프라인을 운영할 때 사용한다.

분산 모드 커넥트는 2대 이상의 서버에서 클러스터 형태로 운영함으로써 단일 모드 커넥트 대비 안전하게 운영할 수 있다는 장점이 있다.
2개 이상의 커넥트가 클러스터로 묶이면 1개의 커넥트가 이슈 발생으로 중단되더라도 남은 1개의 커넥트가 파이프라인을 지속적으로 처리할 수 있기 때문이다.
데이터 처리량의 변화에도 유연하게 대응할 수 있다.
커넥트가 실행되는 서버 개수를늘림으로써 무중단으로 스케일 아웃하여 처리량을 늘릴 수 있기 때문이다.
이러한 장점이 있기 때문에 상용환경에서 커넥트를 운영한다면 분산 모드 커넥트를 2대 이상으로 구성하고 설정하는 것이 좋다.

> 단일 모드 커넥트

단일 모드 커넥트를 실행하기 위해서는 단일 모드 커넥트를 참조하는 설정 파일인 `connect-standalone.properties` 파일을 수정해야 한다.
해당 파일은 카프카 바이너리 디렉토리의 config 디렉토리에 있다.

```shell
cat connect-standalone.properties 
```
```shell
# 커넥트와 연동할 카프카 클러스터의 호스트와 포트 번호를 입력한다.
# 2개 이상의 브로커로 이루어진 클러스터와 연동할 때는 2개 이상의 정보를 콤마(,)로 구분하여 적으면 된다.
bootstrap.servers=localhost:9092

# 데이터를 카프카에 저장할 때 혹은 카프카에서 데이터를 가져올 때 변환하는 데에 사용한다.
# 카프카 커넥터는 JsonConverter, StringConverter, ByteArrayConverter를 기본으로 제공한다.
# 만약 스키마 형태를 사용하고 싶다면 enable 옵션을 false로 설정하면 된다.
key.converter=org.apache.kafka.connect.json.JsonConverter
value.converter=org.apache.kafka.connect.json.JsonConverter
key.converter.schemas.enable=true
value.converter.schemas.enable=true

# 태스크가 처리 완료한 오프셋을 커밋하는 주기를 설정한다.
offset.storage.file.filename=/tmp/connect.offsets
offset.flush.interval.ms=10000

# 플러그인 형태로 추가할 커넥터의 디렉토리 주소를 입력한다.
# 오픈소르로 다운받았거나 직접 개발한 커넥터의 jar파일이 위치하는 디렉토리를 값으로 입력한다.
# 2개 이상의 디렉토리를 콤마(,)로 구분해 입력할 수 있다.
# 커넥터가 실행될 때 디렉토리로부터 jar 파일을 로드한다.
# 커넥터 이외에도 직접 컨버터, 트랜스폼도 플러그인으로 추가할 수 있다.
# plugin.path=/usr/local/share/java,/usr/local/share/kafka/plugins,/opt/connectors,
#plugin.path=
```
단일 모드 커넥트는 커넥트 설정파일과 함께 커넥터 설정파일도 정의하여 실행해야 한다.
카프카에서 기본으로 제공하는 파일 소스 커넥터를 살펴보자.

```shell
cat connect-file-source.properties
```
```shell
# 커넥터의 이름을 지정한다.
name=local-file-source

# 사용할 커넥터의 클래스 이름을 지정한다. 여기서는 카프카에서 제공하는 기본 클래스중 하나인 FileStreamSource를 지정했다.
connector.class=FileStreamSource

# 커넥터로 실행할 태스크 개수를 지정한다. 태스크 개수를 늘려서 병렬처리를 할 수 있다.
tasks.max=1

# 읽을 파일의 위치를 지정한다.
file=test.txt

# 읽은 파일의 데이터를 저장할 토픽의 이름을 지정한다.
topic=connect-test
```
```shell
$ bin/connect-standalone.sh config/connect-standalone.properties config/connect-file-source.properties 
```

> 분산 모드 커넥트

분산 모드 커넥트는 단일 모드 커넥트와 다르게 2개 이상의 프로세스가 1개의 그룹으로 묶여서 운영된다.
이를 통해 1개의 커넥트 프로세스에 이슈가 발생하여 종료되더라도 살아있는 나머지 1개 커넥트 프로세스가 커넥터를 이어받아서 파이프라인을 지속적으로 실행할 수 있다는 특징이 있다.
이제 분산 모드 커넥트를 묶어서 운영하기 위해 어떤 설정을 해야하는지 분산 모드 설정 파일인 connect-distributed.properties를 살펴보자

```shell
# 커넥트와 연동할 카프카 클러스터의 호스트 이름과 포트를 작성한다.
bootstrap.servers=localhost:9092

# 다수의 커넥트 프로세스들을 묶을 그룹 이름을 지정한다.
# 동일한 group.id로 지정된 커넥트들은 같은 그룹으로 인식한다.
# 같은 그룹으로 지정된 커넥트들에서 커넥터가 실행되면 커넥트들에 분산되어 실행된다.
# 이를 통해 커넥트 중 한 대에 이슈가 발생하더라도 나머지 커넥트가 커넥터를 안전하게 실행할 수 있다.
group.id=connect-cluster

# 데이터를 카프카에 저장할 때 혹은 카프카에서 데이터를 가져올 때 변환하는 데에 사용한다.
# 카프카 커넥터는 JsonConverter, StringConverter, ByteArrayConverter를 기본으로 제공한다.
# 만약 스키마 형태를 사용하고 싶다면 enable 옵션을 false로 설정하면 된다.
key.converter=org.apache.kafka.connect.json.JsonConverter
value.converter=org.apache.kafka.connect.json.JsonConverter
key.converter.schemas.enable=true
value.converter.schemas.enable=true

# 분산 모드 커넥트는 카프카 내부 토픽에 오프셋 정보를 저장한다.
# 이 오프셋 정보는 소스 커넥터 또는 싱크 커넥터가 데이터 처리 시점을 저장하기 위해 사용한다.
# 해당 정보는 데이터를 처리하는 데에 있어 중요한 역할을 하므로 실제 운영할 때는 복제 개수를 3보다 큰 값으로 설정하는 것이 좋다.
offset.storage.topic=connect-offsets
offset.storage.replication.factor=1

config.storage.topic=connect-configs
config.storage.replication.factor=1

status.storage.topic=connect-status
status.storage.replication.factor=1

# 태스크가 처리 완료한 오프셋을 커밋하는 주기를 설정한다.
offset.flush.interval.ms=10000

# 플러그인 형태로 추가할 커넥터의 디렉토리 주소를 입력한다.
# 오픈소르로 다운받았거나 직접 개발한 커넥터의 jar파일이 위치하는 디렉토리를 값으로 입력한다.
# 2개 이상의 디렉토리를 콤마(,)로 구분해 입력할 수 있다.
# 커넥터가 실행될 때 디렉토리로부터 jar 파일을 로드한다.
# 커넥터 이외에도 직접 컨버터, 트랜스폼도 플러그인으로 추가할 수 있다.
# plugin.path=/usr/local/share/java,/usr/local/share/kafka/plugins,/opt/connectors,
#plugin.path=
```

분산 모드 커넥트를 실행할 때는 커넥트 설정파일만 있으면 된다.
커넥터는 커넥트가 실행된 이후 REST API를 통해 실행/중단/변경할 수 있기 때문이다.

```shell
$ bin/connect-distributed.sh config/connect-distributed.properties
```

분산 모드 커넥트가 실행되고 난 이후에 REST API로 커넥트의 상태, 커넥터 생성, 커넥터 조회, 커넥터 수정, 커넥터 중단 등 명령을 날릴 수 있다.
현재 커넥트에서 사용할 수 있는 플러그인을 조회해 보자.

```shell
curl -X GET http://localhost:8083/connector-plugins
```
```shell
[
  {"class":"org.apache.kafka.connect.file.FileStreamSinkConnector","type":"sink","version":"2.5.0"},
  {"class":"org.apache.kafka.connect.file.FileStreamSourceConnector","type":"source","version":"2.5.0"},
  {"class":"org.apache.kafka.connect.mirror.MirrorCheckpointConnector","type":"source","version":"1"},
  {"class":"org.apache.kafka.connect.mirror.MirrorHeartbeatConnector","type":"source","version":"1"},
  {"class":"org.apache.kafka.connect.mirror.MirrorSourceConnector","type":"source","version":"1"}
]
```

FileStreamSourceConnector를 실행해보자.
```shell
$ curl -X POST -H "Content-Type: application/json" --data '{"name": "local-file-source", "config": {"connector.class": "org.apache.kafka.connect.file.FileStreamSourceConnector", "file": "/tmp/test.txt", "tasks.max": "1", "topic": "connect-test"}}' http://localhost:8083/connectors
```
```shell
{"name":"local-file-source","config":{"connector.class":"org.apache.kafka.connect.file.FileStreamSourceConnector","file":"/tmp/test.txt","tasks.max":"1","topic":"connect-test","name":"local-file-source"},"tasks":[],"type":"source"}
```

커넥터의 사용이 끝나면 커넥터를 종료하여 커넥트가 사용하는 리소스의 낭비를 줄일 수 있다.
```shell
curl -X DELETE http://localhost:8083/connectors/local-file-source
```

커넥터를 종료한 후 커넥터 리스트를 확인하여 커넥터가 완전히 중지되었는지 확인한다.


> 소스 커넥터

소스 커넥터는 소스 애플리케이션 또는 소스 파일로부터 데이터를 가져와 토픽으로 넣는 역할을 한다.
오픈소스 소스 커넥터를 사용해도 되지만 라이선스 문제나 로직이 원하는 요구사항과 맞지 않아서 직접 개발해야 하는 경우도 있는데
이때는 카프카 커넥트 라이브러리에서 제공하는 SourceConnector와 SourceTask 클래스를 사용하여 직접 소스 커넥터를 구현하면 된다.
직접 구현한 소스 커넥터를 빌드하여 jar파일로 만들고 커넥트를 실행 시 플러그인으로 추가하여 사용할 수 있다.

소스 커넥터를 만들때 필요한 클래스는 2개다.

1. SourceConnector
2. SourceTask

SourceConnector는 태스크를 실행하기 전 커넥터 설정파일을 초기화하고 어떤 태스크 클래스를 사용할 것인지 정의하는데 사용한다.
SourceTask는  소스 애플리케이션 또는 소스 파일로부터 데이터를 가져와서 토픽으로 데이터를 보내는 역할을 수행한다.
SourceTask 특징은 토픽에서 사용하는 오프셋이 아닌 자체적으로 사용하는 오프셋을 사용한다는 점이다.
이 오프셋은 소스 애플리케이션 또는 소스 파일을 어디까지 읽었는지 저장하는 역할을 한다.

```java
/**
 * SourceConnector를 상속받은 사용자 정의 클래스를 선언한다.
 * 사용자가 지정한 이클래스 이름은 최종적으로 커넥트에서 호출할 때 사용되므로 명확하게 어떻게 사용되는지 적으면 좋다.
 * 예) MongoDbSourceConnector
 */
public class TestSourceConnector extends SourceConnector {

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
    public List<Map<String, String>> taskConfigs(int i) {
        return List.of();
    }

    /**
     * 커넥터가 종료될 때 필요한 로직을 작성한다.
     */
    @Override
    public void stop() {

    }

    /**
     * 커넥터가 사용할 설정값에 대한 정보를 받는다. 커넥터의 설정값은  ConfigDef 클래스를 통해 각 설정의 이름, 기본값, 중요도, 설명을 정의할 수 있다.
     */
    @Override
    public ConfigDef config() {
        return null;
    }

    /**
     * 커넥터의 버전을 리턴한다.
     * 커넥트에 포함된 커넥터 플러그인을 조회할 때 이 버전이 노출된다.
     * 지속적으로 유지보수하고 신규 배포할 때 이 메서드가 리턴하는 버전 값을 변경해야 한다.
     */
    @Override
    public String version() {
        return "";
    }
}
```

```java
public class TestSourceTask extends SourceTask {

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
     * 태스크는 실질적으로 데이터를 처리하는 역할을 하므로 데이터 처리에 필요한 리소스를 여기서 초기화하면 좋다.
     */
    @Override
    public void start(Map<String, String> map) {
        
    }

    /**
     * 소스 애플리케이션 또는 소스 파일로부터 데이터를 읽어오는 로직을 작성한다.
     * 데이터를 읽어오면 토픽으로 보낼 데이터를 SourceRecord로 정의한다.
     * SourceRecord 클래스는 토픽으로 데이터를 정의하기 위해 사용한다.
     * List<SourceRecord> 인스턴스에 데이터를 담아 리턴하면 데이터가 토픽으로 전송된다.
     */
    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        return List.of();
    }

    /**
     * 태스크가 종료될 때 필요한 로직을 작성한다.
     */
    @Override
    public void stop() {

    }
}
```



---

# 4. 카프카 상세 개념 설명
## 4.1 토픽과 파티션
## 4.2 카프카 프로듀서
## 4.3 카프카 컨슈머
## 4.4 스프링 카프카
## 4.5 정리



