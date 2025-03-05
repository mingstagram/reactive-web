# 📝 WebFlux + R2DBC 설정 검토 보고서

## 1️⃣ 개요
- **Spring WebFlux + R2DBC**를 활용하여 **비동기 데이터베이스 처리**를 구현
- 기존 **Spring MVC + JPA(Hibernate)** 방식 대비 **Reactive(논블로킹) 아키텍처**를 도입하여 성능 최적화
- **도입 이유**:
    - 대량의 동시 요청 처리 (High Concurrency)
    - Non-blocking 데이터베이스 접근 (R2DBC)
    - 이벤트 기반 비동기 시스템과의 연동 (Kafka, Redis)

---

## 2️⃣ 현재 설정

### **📌 `pom.xml` 의존성 추가**
```xml
<dependencies>
    <!-- Spring WebFlux -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>

    <!-- R2DBC (MariaDB) -->
    <dependency>
        <groupId>org.mariadb</groupId>
        <artifactId>r2dbc-mariadb</artifactId>
    </dependency>

    <!-- Spring Data R2DBC -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-r2dbc</artifactId>
    </dependency>

    <!-- Reactive Spring Boot Test -->
    <dependency>
        <groupId>io.projectreactor</groupId>
        <artifactId>reactor-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

### 📌 `pom.xml` 의존성 추가
```yaml
spring:
  r2dbc:
    url: r2dbc:mariadb://localhost:3306/reactive_db
    username: root
    password: password
  sql:
    init:
      mode: always
```

---

### 📌 R2DBC Configuration (R2DBCConfig.java)
```java
@Configuration
@EnableR2dbcRepositories
public class R2DBCConfig {
    
    @Bean
    public ConnectionFactory connectionFactory() {
        return new MariadbConnectionFactory(
            MariadbConnectionConfiguration.builder()
                .host("localhost")
                .port(3306)
                .database("reactive_db")
                .username("root")
                .password("password")
                .build()
        );
    }
}
```

---

## 3️⃣ 트러블슈팅
### 🔹 이슈 1: `R2dbcTransientResourceException` 발생
#### 원인: Blocking 코드(JPA 등)를 함께 사용하여 WebFlux의 논블로킹 아키텍처와 충돌
#### 해결책:
- WebFlux 프로젝트에서 `@Transactional` 및 JPA 사용 금지
- **JDBC 대신 R2DBC Repository를 사용**
```java
@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
}
```

---
 
### 🔹 이슈 2: `io.netty.channel.AbstractChannel$AnnotatedConnectException` 오류
#### 원인: R2DBC가 DB에 연결하지 못함 (DB 미구동)
#### 해결책:
- DB가 실행 중인지 확인
```shell
docker ps | grep mariadb
```
- 실행되지 않았다면, Docker Compose로 실행
```shell
docker-compose up -d
```

---

### 🔹 이슈 3: `DataAccessException` (DB Connection Timeout)
#### 원인: R2DBC는 커넥션 풀을 기본적으로 사용하지 않음
#### 해결책:
- Spring Boot에서 R2DBC 커넥션 풀 활성화
```yaml
spring:
  r2dbc:
    pool:
      enabled: true
      max-size: 10
```
- R2DBC ConnectionFactory 설정 추가
```java
@Bean
public ConnectionFactory connectionFactory() {
    return ConnectionPoolFactory.create(
            ConnectionFactoryOptions.builder()
                    .option(DRIVER, "mariadb")
                    .option(HOST, "localhost")
                    .option(PORT, 3306)
                    .option(USER, "root")
                    .option(PASSWORD, "password")
                    .option(DATABASE, "reactive_db")
                    .option(MAX_SIZE, 10) // 커넥션 풀 크기 설정
                    .build()
    );
} 
```


