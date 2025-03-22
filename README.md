# 🚀 Reactive App 프로젝트

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4+-green?style=flat)
![WebFlux](https://img.shields.io/badge/WebFlux-Reactive-blue?style=flat)
![Kafka](https://img.shields.io/badge/Kafka-Message%20Queue-orange?style=flat)
![Redis](https://img.shields.io/badge/Redis-Cache-red?style=flat)
![JUnit](https://img.shields.io/badge/JUnit-Testing-yellow?style=flat)

## 📌 프로젝트 개요
이 프로젝트는 **Spring WebFlux + R2DBC + Kafka + Redis**를 활용한 **비동기 Reactive 애플리케이션**입니다.  
Kafka를 사용하여 **이벤트 스트리밍**, Redis를 활용하여 **고속 캐싱 및 세션 관리**, WebFlux와 R2DBC를 통해 **비동기 데이터 처리**를 구현했습니다.

✅ **주요 목표:**
- **비동기 아키텍처**를 활용하여 고성능 서비스 구현
- **Kafka 메시지 큐**를 활용한 이벤트 기반 시스템 구축
- **Redis를 활용한 캐싱 최적화**
- **WebFlux + R2DBC 기반으로 비동기 데이터베이스 처리**
- **JUnit & Mockito 기반의 테스트 코드 적용**

---

## 📌 기술 스택
- **Backend:** Spring Boot (WebFlux, R2DBC)
- **Database:** MariaDB (Reactive R2DBC)
- **Message Queue:** Apache Kafka
- **Cache & Session:** Redis
- **Testing:** JUnit5, Mockito, Spring Boot Test
- **Build Tool:** Maven
- **Containerization:** Docker & Docker Compose

---

## 📌 주요 기능
### ✅ **1. Kafka 기반 메시징 시스템**
- Kafka를 활용한 **이벤트 스트리밍 아키텍처**
- **Producer & Consumer** 로직 구현

### ✅ **2. Redis 기반 캐싱 & 세션 관리**
- 사용자 데이터 캐싱
- Redis를 활용한 세션 저장

### ✅ **3. WebFlux + R2DBC 기반 비동기 데이터 처리**
- Reactive API 기반으로 빠른 응답 처리
- R2DBC를 활용한 **Non-blocking DB 처리**

### ✅ **4. JUnit 기반의 테스트 코드**
- `Mockito`를 활용한 Mock 테스트
- `StepVerifier`를 이용한 WebFlux 비동기 검증 

---

## 📌 기술 검토 문서
| 문서명                                                              | 설명            | 
|------------------------------------------------------------------|---------------|
| [Kafka 설정 검토](docs/tech-review/tech-review-kafka-setup.md)       | Kafka 설정 및 문제 해결            | 
| [Redis 설정 검토](docs/tech-review/tech-review-redis-setup.md)      | Redis 설정 및 캐싱 최적화           |
| [WebFlux + R2DBC](docs/tech-review/tech-review-webflux-r2dbc.md) | 비동기 데이터 처리 아키텍처       |
| [Kafka vs Redis 비교](docs/tech-review/tech-review-redis-vs-kafka.md) | Kafka와 Redis의 차이점 및 활용 사례     |
| [JUnit 테스트 검토](docs/tech-review/tech-review-junit-setup.md) | JUnit 기반의 테스트 코드 작성 및 문제 해결 |

---

## 📌 Contact
#### ✉️ Email: amgkim21@gmail.com
#### 📌 GitHub: [github.com/mingstagram](https://github.com/mingstagram)
#### 📌 Tech Blog: [mingucci.tistory.com](https://mingucci.tistory.com)
