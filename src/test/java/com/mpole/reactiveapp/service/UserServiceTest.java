package com.mpole.reactiveapp.service;

import com.mpole.reactiveapp.kafka.KafkaProducerService;
import com.mpole.reactiveapp.model.User;
import com.mpole.reactiveapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReactiveRedisTemplate<String, User> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, User> valueOperations;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    /**
     * 사용자를 데이터베이스에 저장하고 Redis에 캐시하는 기능을 검증합니다.
     * 시나리오: 새로운 사용자 객체를 생성하고, 이를 데이터베이스에 저장하고 Redis에 캐시하는 메서드를 호출합니다.
     * 기대 결과: 생성된 사용자 정보가 반환되고, 사용자 정보가 데이터베이스에 저장되며 Redis에 캐시됩니다.
     */
    @Test
    void createUser_ShouldSaveToDB_AndCacheInRedis() {
        // Given: 테스트를 위해 User 객체를 생성하고, 사용자 정보가 데이터베이스에 저장될 것임을 정의
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        when(userRepository.save(user)).thenReturn(Mono.just(user));
        when(valueOperations.set("user:1", user)).thenReturn(Mono.just(true));

        // Kafka 메시지 전송을 Mock 처리
        when(kafkaProducerService.sendMessage(anyString()))
                .thenReturn(Mono.empty());

        // When: 사용자 생성 메서드를 호출
        Mono<User> createdUserMono = userService.createUser(user);

        // Then: 생성된 사용자 정보가 예상한 대로 반환되는지 검증
        StepVerifier.create(createdUserMono)
                .expectNextMatches(createdUser -> {
                    assertEquals("John", createdUser.getName());
                    return true;
                })
                .verifyComplete();

        // 데이터베이스에 사용자 정보가 저장되었는지 확인
        verify(userRepository).save(user);
        // Redis에 사용자 정보가 캐시되었는지 확인
        verify(valueOperations).set("user:1", user);
        // Kafka 메시지 전송 검증
        verify(kafkaProducerService).sendMessage(anyString());

    }

    /**
     * Kafka Consumer가 메시지를 정상적으로 수신하는지 검증합니다.
     */
    void kafkaConsumer_ShouldReceiveUserCreationMessage() {
        // Given
        String message = "User Created: John";

//        // When
//        userService.consu
    }

    /**
     * 특정 ID를 가진 사용자의 정보를 조회하는 기능을 검증합니다.
     * 시나리오: Redis에 사용자 정보가 없을 때, 데이터베이스에서 조회하는지 확인합니다.
     * 기대 결과: 데이터베이스에서 사용자 정보를 조회한 후 Redis에 저장합니다.
     */
    @Test
    void getUserById_ShouldRetrieveFromDB_WhenNotInRedis() {
        // Given: 테스트할 사용자 객체 생성
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        when(valueOperations.get("user:1")).thenReturn(Mono.empty()); // Redis에서 사용자 없음
        when(userRepository.findById(1L)).thenReturn(Mono.just(user));
        when(valueOperations.set("user:1", user)).thenReturn(Mono.just(true)); // Redis에 저장 예정

        // When: 사용자 ID로 조회
        Mono<User> retrievedUserMono = userService.getUserById(1L);

        // Then: 반환된 값 검증
        StepVerifier.create(retrievedUserMono)
                .expectNextMatches(retrievedUser -> {
                    assertEquals("John", retrievedUser.getName());
                    return true;
                })
                .verifyComplete();

        // 데이터베이스에서 사용자 정보가 조회되었는지 확인
        verify(userRepository).findById(1L);
        // Redis에 사용자 정보가 캐시되었는지 확인
        verify(valueOperations).set("user:1", user);

    }

    /**
     * 사용자의 정보를 업데이트하는 기능을 검증합니다.
     * 시나리오: 기존 사용자의 정보를 업데이트하고, 데이터베이스 및 Redis에 저장합니다.
     * 기대 결과: 업데이트된 사용자 정보가 반환되고, 데이터베이스 및 Redis에 반영됩니다.
     */
    @Test
    void updateUser_ShouldUpdateInDB_AndCacheInRedis() {
        // Given: 기존 사용자 객체 생성
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated John");
        updatedUser.setEmail("updated@example.com");

        when(userRepository.save(any(User.class))).thenReturn(Mono.just(updatedUser));
        when(valueOperations.set(eq("user:1"), any(User.class))).thenReturn(Mono.just(true));

        // When: 사용자 업데이트 메서드 호출
        Mono<User> updatedUserMono = userService.updateUser(1L, updatedUser);

        // Then: 반환된 값 검증
        StepVerifier.create(updatedUserMono)
                .expectNextMatches(user -> {
                    assertEquals("Updated John", user.getName());
                    return true;
                })
                .expectComplete()
                .verify();

        // 데이터베이스에서 사용자 정보가 업데이트되었는지 확인
        verify(userRepository).save(any(User.class));
        // Redis에 사용자 정보가 업데이트 되었는지 확인
        verify(valueOperations).set(eq("user:1"), any(User.class));

    }

    /**
     * 사용자를 삭제하는 기능을 검증합니다.
     * 시나리오: 특정 ID를 가진 사용자를 삭제하고, 데이터베이스 및 Redis에서 제거합니다.
     * 기대 결과: 데이터베이스와 Redis에서 사용자 정보가 삭제됩니다.
     */
    @Test
    void deleteUser_ShouldRemoveFromDB_AndRedis() {
        // Given: Redis 및 데이터베이스에서 삭제될 사용자 ID
        when(valueOperations.delete("user:1")).thenReturn(Mono.just(true));
        when(userRepository.deleteById(1L)).thenReturn(Mono.empty());

        // When: 사용자 삭제 메서드 호출
        Mono<Void> deleteMono = userService.deleteUser(1L);

        // Then: 삭제가 정상적으로 수행되는지 검증
        StepVerifier.create(deleteMono)
                .verifyComplete();

        // Redis에서 삭제되었는지 확인
        verify(valueOperations).delete("user:1");
        // 데이터베이스에서 삭제되었는지 확인
        verify(userRepository).deleteById(1L);

    }



}
