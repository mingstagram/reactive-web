# 📝 Redis 설정 검토 보고서

## 1️⃣ 개요
- Reactive App 프로젝트에서 **Redis를 활용한 캐싱 및 세션 관리 아키텍처** 검토
- **도입 이유**: 빠른 데이터 접근, 데이터 일관성 유지, 세션 관리
* * *
## 2️⃣ 현재 설정
```yaml
spring:
  redis:
    host: 172.25.54.33
    port: 6379
    password: ""
    timeout: 5000ms
```
* * *
## 3️⃣ 트러블슈팅
### 🔹 이슈 1: `Cannot connect to Redis` 오류 발생
**원인**: Redis 서버가 실행되지 않았거나 방화벽에 의해 차단됨
**해결책**:
* Redis가 실행 중인지 확인
```
redis-cli ping
```
응답이 PONG이 나오지 않으면 Redis를 실행
```
systemctl start redis
```
* 방화벽이 Redis 포트를 차단하고 있는지 확인 후 해제
```
sudo ufw allow 6379/tcp
```
* * *
### 🔹 이슈 2: `Cannot connect to Redis` 오류 발생
**원인**: `@EnableCaching` 또는 `RedisTemplate` 설정이 누락됨
**해결책**:
* Spring Boot 애플리케이션에서 캐싱을 활성화
```java
@Configuration
@EnableCaching
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return template;
    }
}

```

