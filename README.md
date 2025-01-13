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
## 3.5 카프카 스트림즈
## 3.6 카프카 커넥트

---

# 4. 카프카 상세 개념 설명
## 4.1 토픽과 파티션
## 4.2 카프카 프로듀서
## 4.3 카프카 컨슈머
## 4.4 스프링 카프카
## 4.5 정리



