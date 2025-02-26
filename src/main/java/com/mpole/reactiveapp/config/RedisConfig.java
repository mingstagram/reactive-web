package com.mpole.reactiveapp.config;

import com.mpole.reactiveapp.model.User;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    @Primary // 기본 RedisConnectionFactory로 지정
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(Environment env) {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        String redisHost = env.getProperty("spring.redis.host", "localhost");
        int port = env.getProperty("spring.redis.port", Integer.class, 6379);
        String password = env.getProperty("spring.redis.password", "");

        redisConfig.setHostName(redisHost);
        redisConfig.setPort(port);
        redisConfig.setPassword(password);
        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public ReactiveRedisTemplate<String, User> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<User> serializer = new Jackson2JsonRedisSerializer<>(User.class);

        return new ReactiveRedisTemplate<>(
                factory,
                RedisSerializationContext
                        .<String, User>newSerializationContext(new StringRedisSerializer())
                        .value(serializer)
                        .key(new StringRedisSerializer())
                        .build()
        );
    }

}
