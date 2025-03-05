# 📝 Kafka 설정 검토 보고서

## 1️⃣ 개요
- Reactive App 프로젝트에서 **Kafka를 활용한 이벤트 스트리밍 아키텍처** 검토
- **도입 이유**: 비동기 메시징 처리, 마이크로서비스 간 이벤트 연동
* * *
## 2️⃣ 현재 설정
```yaml
spring:
  kafka:
    bootstrap-servers: 172.25.54.32:9092
    consumer:
      group-id: group_id
      auto-offset-reset: earliest
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
    listener:
      ack-mode: manual 
```
* * *
## 3️⃣ 트러블슈팅
### 🔹 이슈 1: `localhost:9092` 연결 오류
**원인**: Kafka의 `advertised.listeners` 설정이 잘못됨  
**해결책**:
```init
KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://172.25.54.32:9092
```
---
### 🔹 이슈 2: kafka 테스트 코드에서 `sendMessage()`호출되지 않는 문제
**원인**: 
- `KafkaTemplate.send()`가 비동기적으로 실행되면서 테스트에서 `verify()`가 실행되기 전에 종료됨
- `Mono.fromRunnable()`이 즉시 실행되지 않아 메시지가 전송되지 않음

**해결책**:
1. 테스트에서 `KafkaProducerService`의 Mock 설정 추가
```
when(kafkaProducerService.sendMessage(anyString())).thenReturn(Mono.empty());
```
2. `sendMessage()` 내부에서 `Mono.defer()`로 변경하여 실행 시점 보장
```java
public Mono<Void> sendMessage(String message) {
    return Mono.defer(() -> {
        kafkaTemplate.send(TOPIC, message);
        return Mono.empty();
    }).doOnNext(result -> System.out.println("Kafka 메시지 전송 완료: " + message))
      .then();
}
```
3. 테스트에서 `verify()` 추가하여 호출 여부 확인
```
verify(kafkaProducerService, times(1)).sendMessage(anyString());
```
✅ 위 조치를 적용한 후, 테스트에서 sendMessage() 호출이 정상적으로 검증됨.
---