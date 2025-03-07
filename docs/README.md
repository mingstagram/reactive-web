# Tech Review 문서 모음

이 디렉토리는 **Reactive App 프로젝트**에서 사용한 **기술 검토 내용**을 정리한 문서를 모아둔 곳입니다.  
각 문서는 특정 기술 스택의 설정 방법, 성능 최적화, 문제 해결 과정을 포함하고 있습니다.

## 📌 포함된 문서
- **[Kafka](tech-review/tech-review-kafka-setup.md)**
  - Kafka 설정 가이드를 정리한 문서
  - 프로듀서 및 컨슈머 설정 방법
  - Kafka 브로커 및 토픽 생성
  - application.yml을 통한 설정 예제 포함
  
- **[Redis](tech-review/tech-review-redis-setup.md)** 
  - Reactive App에서 Redis를 활용한 캐싱 및 세션 관리 적용
  - Redis 설정 (application.yml) 및 연결 설정 (RedisConfig.java)
  - 주요 트러블슈팅 (Redis 연결 오류, 방화벽 차단, @EnableCaching 누락 등)
  - Redis 서버 실행 및 방화벽 설정 확인 방법
  
- **[WebFlux & R2DBC](tech-review/tech-review-webflux-r2dbc.md)**
    - Spring WebFlux와 R2DBC를 활용한 비동기 데이터베이스 처리
    - 기존 JPA(Hibernate)와의 차이점 및 논블로킹 아키텍처 적용
    - R2DBC 설정 (pom.xml, application.yml, R2DBCConfig.java)
    - 커넥션 풀 설정 및 성능 최적화
    - 주요 트러블슈팅 (DB 연결 오류, 커넥션 타임아웃 등)
  
- **[Redis vs. Kafka](tech-review/tech-review-redis-vs-kafka.md)**
    - Redis와 Kafka의 차이점 및 사용 사례 비교
    - 메시지 브로커 vs 데이터 캐시로서의 차이점
    - 각각의 장단점 및 적절한 사용 시나리오
    - 프로젝트에서 어떤 경우에 Redis 또는 Kafka를 선택해야 하는지 설명

- **[JUnit](tech-review/tech-review-junit-setup.md)**
    - JUnit을 활용한 테스트 코드 작성 가이드
    - Reactive App에서 WebFlux 기반의 비동기 코드 테스트 방법
    - Mockito와 함께 사용하는 Mock 객체 생성 및 검증
    - Kafka 및 Redis 관련 서비스 테스트 코드 예제
    - 주요 트러블슈팅 (Mock 설정 문제, NPE 오류, 비동기 검증 실패 등)

---