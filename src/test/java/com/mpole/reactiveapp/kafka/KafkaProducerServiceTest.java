package com.mpole.reactiveapp.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Test
    void sendMessage_ShouldCallKafkaTemplateSend() {
        // Given
        String topic = "user-topic";
        String message = "User Created: John";

        // When
        kafkaProducerService.sendMessage(message).block();

        // Then
        verify(kafkaTemplate, times(1)).send(eq(topic), eq(message));
    }

}
