# 📝 JUnit 설정 검토 보고서

## 1️⃣ 개요
- Reactive App 프로젝트에서 **JUnit과 Mockito**를 활용한 **테스트 결과 검증**
- **도입 이유**: 
  - WebFlux, R2DBC, Kafka, Redis 등의 **비동기(논블로킹) 환경에서의 테스트 필요** 
  - 서비스 레이어 및 데이터 접근 레이어의 단위 테스트를 강화하여 안정적인 코드 유지
  - Kafka 및 Redis를 사용하는 **비동기 시스템의 검증 방법 확립**
  
* * *

## 2️⃣ 현재 설정
### 📌 `pom.xml` 의존성 추가
```xml
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Mockito -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Reactor Test (WebFlux, R2DBC) -->
    <dependency>
        <groupId>io.projectreactor</groupId>
        <artifactId>reactor-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
        <exclusions>
            <exclusion>
                <groupId>org.junit.vintage</groupId>
                <artifactId>junit-vintage-engine</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <!-- Spring Kafka Test -->
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

* * *

## 3️⃣ 트러블슈팅
### 🔹 이슈 1: Kafka 테스트 코드에서 `sendMessage()` 호출되지 않는 문제
**원인**: 
- `KafkaTemplate.send()`가 **비동기적으로 실행**되면서 테스트에서 `verify()`가 실행되기 전에 종료됨
- `Mono.fromRunnable()`이 즉시 실행되지 않아 **Kafka 메시지가 전송되지 않음**
**해결책**:
1. **테스트에서 `KafkaProducerService`의 Mock 설정 추가**
```
when(kafkaProducerService.sendMessage(anyString())).thenReturn(Mono.empty());
```
2. **`sendMessage()` 내부에서 `Mono.defer()`로 변경하여 실행 시점 보장** 
```java
public Mono<Void> sendMessage(String message) {
    return Mono.defer(() -> {
        kafkaTemplate.send(TOPIC, message);
        return Mono.empty();
    }).doOnNext(result -> System.out.println("Kafka 메시지 전송 완료: " + message))
      .then();
}
```
3. **테스트에서 `verify()` 추가하여 호출 여부 확인**
```
verify(kafkaProducerService, times(1)).sendMessage(anyString());
```
✅ **위 조치를 적용한 후, 테스트에서 `sendMessage()` 호출이 정상적으로 검증됨.**

---

### 🔹 이슈 2: `deleteUser()` 실행 시 NPE 발생
**원인**:
- `ReactiveRedisTemplate`의 `opsForValue().delete()`가 **Mock 설정되지 않음**
- `userRepository.findById(id)`의 Mock 값이 **null을 반환하여 `flatMap()` 내부 실행되지 않음**

**해결책**:
1. **Mock 설정 추가**
```
when(userRepository.findById(1L)).thenReturn(Mono.just(new User(1L, "John Doe", "john@example.com")));
when(valueOperations.delete("user:1")).thenReturn(Mono.just(true));
when(userRepository.deleteById(1L)).thenReturn(Mono.empty());
when(kafkaProducerService.sendMessage(anyString())).thenReturn(Mono.empty());
```
2. 테스트 코드 검증 강화
```
verify(valueOperations).delete("user:1");
verify(userRepository).deleteById(1L);
verify(kafkaProducerService).sendMessage(anyString());
```
✅ **위 조치를 통해 NPE 발생 없이 정상 동작 검증 완료**

---

### 🔹 이슈 3: WebFlux `StepVerifier` 사용 시 `verifyComplete()`에서 AssertionError 발생
**원인**:
- `Mono<Void>` 반환 시 아무 값도 방출하지 않기 때문에 **StepVerifier에서 onComplete 이벤트를 감지하지 못함**
- `Mono.empty()`를 올바르게 검증하지 않음

**해결책**:
1. **`StepVerifier`에서 `verifyComplete()`를 사용하여 정상 종료 검증**
```
StepVerifier.create(userService.deleteUser(1L))
    .verifyComplete(); 
```
2. 비동기 호출이므로, **StepVerifier 내부에서 호출 검증 추가**
```
StepVerifier.create(userService.createUser(new User(1L, "John", "john@example.com")))
        .expectNextMatches(user -> user.getName().equals("John"))
        .verifyComplete();

```
✅ **위 조치를 통해 WebFlux 비동기 흐름을 테스트 환경에서 올바르게 검증 가능**

---


3. 테스트에서 `verify()` 추가하여 호출 여부 확인
```
verify(kafkaProducerService, times(1)).sendMessage(anyString());
```
✅ 위 조치를 적용한 후, 테스트에서 sendMessage() 호출이 정상적으로 검증됨.

---

## 4️⃣ 결론 및 정리
- **WebFlux, R2DBC, Kafka 등 비동기 환경에서 JUnit 5 + Mockito 조합으로 테스트 구축**
- **Kafka, Redis와 연계된 비동기 로직에서 Mock 설정 필수**
- **StepVerifier를 사용한 WebFlux 비동기 흐름 검증 강화**
- **비동기 메시지 처리 시 `Mono.defer()` 활용하여 실행 시점 제어**
- **✅ 적용 후 테스트 성공적으로 통과 및 NPE, AssertionError 해결**