package com.mpole.reactiveapp.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/*
Kafka 컨슈머를 구현하여 Kafka에서 메시지를 읽고 처리합니다.
 */
@Slf4j
@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "user-topic", groupId = "group_id")
    public void consume(String message) {
        log.info("Received message: {}", message);
    }

}
