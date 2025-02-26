package com.mpole.reactiveapp.service;

import com.mpole.reactiveapp.model.User;
import com.mpole.reactiveapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReactiveRedisTemplate<String, User> redisTemplate;

    /**
     * 사용자를 생성하고 Redis에 해당 사용자 정보를 저장합니다.
     *
     * @param user 생성할 사용자 객체
     * @return 생성된 사용자 정보를 포함하는 Mono
     */
    public Mono<User> createUser(User user) {
        return userRepository.save(user) // 데이터베이스에 사용자 저장
                // Redis에 사용자 정보를 "user:{id}" 형식으로 저장
                .flatMap(savedUser -> redisTemplate.opsForValue().set("user:" + savedUser.getId(), savedUser)
                        .thenReturn(savedUser)); // 저장된 사용자 정보를 반환
    }

    /**
     * 모든 사용자의 정보를 조회합니다.
     *
     * @return 모든 사용자 정보를 포함하는 Flux
     */
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 특정 ID를 가진 사용자의 정보를 조회합니다.
     * 먼저 Redis에서 해당 사용자를 찾고, 없으면 데이터베이스에서 조회합니다.
     *
     * @param id 조회할 사용자 ID
     * @return 사용자의 정보를 포함하는 Mono
     */
    public Mono<User> getUserById(Long id) {
        // Redis에서 사용자 정보를 "user:{id}" 형식으로 조회
        return redisTemplate.opsForValue().get("user:" + id)
                .switchIfEmpty(userRepository.findById(id) // Redis에 없을 경우 데이터베이스에서 조회
                            // Redis에 조회된 사용자 정보를 "user:{id}" 형식으로 저장
                            .flatMap(user -> redisTemplate.opsForValue().set("user:" + id, user)
                                    .thenReturn(user))); // 조회된 사용자 정보를 반환
    }

    /**
     * 특정 ID를 가진 사용자의 정보를 업데이트합니다.
     *
     * @param id 업데이트할 사용자 ID
     * @param user 업데이트할 사용자 객체
     * @return 업데이트된 사용자 정보를 포함하는 Mono
     */
    public Mono<User> updateUser(Long id, User user) {
        user.setId(id);
        return userRepository.save(user)
                .doOnNext(updatedUser -> System.out.println("✅ DB 저장 완료: " + updatedUser))
                .flatMap(updatedUser -> {
                    System.out.println("✅ Redis 저장 시작: " + updatedUser);
                    return redisTemplate.opsForValue().set("user:" + id, updatedUser)
                            .doOnSuccess(success -> System.out.println("✅ Redis 저장 완료: " + success))
                            .thenReturn(updatedUser);
                });
    }

    /**
     * 특정 ID를 가진 사용자를 삭제합니다.
     * Redis에서 해당 사용자 정보를 삭제하고, 데이터베이스에서도 삭제합니다.
     *
     * @param id 삭제할 사용자 ID
     * @return 삭제 작업을 완료한 Mono<Void>
     */
    public Mono<Void> deleteUser(Long id) {
        // Redis에서 사용자 정보를 "user:{id}" 형식으로 삭제
        return redisTemplate.opsForValue().delete("user:" + id)  // Redis에서 삭제하고
                .then(userRepository.deleteById(id));  // 데이터베이스에서 사용자 삭제
    }

}
