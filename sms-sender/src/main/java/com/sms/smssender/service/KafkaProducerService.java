package com.sms.smssender.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.smssender.model.SmsEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishSmsEvent(SmsEvent smsEvent) {
        try {
            String eventJson = objectMapper.writeValueAsString(smsEvent);
            kafkaTemplate.send("sms-events", eventJson);
            System.out.println("Published SMS event to Kafka: " + eventJson);
        } catch (Exception e) {
            System.out.println("Error publishing to Kafka: " + e.getMessage());
        }
    }
}
