package com.mpole.reactiveapp.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class KafkaConsumerServiceTest {

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Test
    void consume_ShouldProcessReceivedMessage() {
        // Given
        String receivedMessage = "User Created: John";

        // When
        kafkaConsumerService.consume(receivedMessage);

        // Then
        System.out.println("✅ Kafka Consumer 테스트 성공: " + receivedMessage);
    }

}
