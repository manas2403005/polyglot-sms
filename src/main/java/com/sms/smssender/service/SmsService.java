package com.sms.smssender.service;

import com.sms.smssender.model.SmsRequest;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    public String sendSms(SmsRequest smsRequest) {

        // Step 1: Check if user is blocked (Redis - coming soon!)
        System.out.println("Checking if user is blocked...");

        // Step 2: Mock SMS send to 3rd party
        System.out.println("Sending SMS to: " + smsRequest.getPhoneNumber());
        System.out.println("Message: " + smsRequest.getMessage());

        // Mock result - randomly SUCCESS or FAIL
        String status = "SUCCESS";
        System.out.println("SMS Status: " + status);

        return status;
    }
}