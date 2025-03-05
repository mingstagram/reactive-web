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