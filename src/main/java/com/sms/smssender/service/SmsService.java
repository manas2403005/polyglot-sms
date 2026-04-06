package com.sms.smssender.service;

import com.sms.smssender.model.SmsEvent;
import com.sms.smssender.model.SmsRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaProducerService kafkaProducerService;

    public SmsService(RedisTemplate<String, String> redisTemplate,
                      KafkaProducerService kafkaProducerService) {
        this.redisTemplate = redisTemplate;
        this.kafkaProducerService = kafkaProducerService;
    }

    public String sendSms(SmsRequest smsRequest) {

        String phoneNumber = smsRequest.getPhoneNumber();

        // Step 1: Check if user is blocked in Redis
        Boolean isBlocked = redisTemplate.hasKey("blocked:" + phoneNumber);

        if (Boolean.TRUE.equals(isBlocked)) {
            System.out.println("User " + phoneNumber + " is BLOCKED!");
            return "BLOCKED";
        }

        // Step 2: Mock SMS send to 3rd party
        System.out.println("Sending SMS to: " + phoneNumber);
        System.out.println("Message: " + smsRequest.getMessage());
        String status = "SUCCESS";

        // Step 3: Publish event to Kafka
        SmsEvent smsEvent = new SmsEvent(phoneNumber, smsRequest.getMessage(), status);
        kafkaProducerService.publishSmsEvent(smsEvent);

        System.out.println("SMS Status: " + status);
        return status;
    }
}