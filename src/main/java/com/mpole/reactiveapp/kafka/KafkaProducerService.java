package com.mpole.reactiveapp.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/*
Kafka 프로듀서를 구현하여 메시지를 Kafka 토픽에 전송합니다.
 */
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "user-topic";

    public Mono<Void> sendMessage(String message) {
        return Mono.fromRunnable(() -> kafkaTemplate.send(TOPIC, message))
                .doOnNext(result -> System.out.println("🔹 Kafka 메시지 전송 완료: " + message))
                .then();
    }

}
