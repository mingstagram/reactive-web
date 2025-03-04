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

    public Mono<Void> sendMassage(String message) {
        return Mono.fromRunnable(() -> kafkaTemplate.send(TOPIC, message));
    }

}
