package com.sms.smssender.controller;

import com.sms.smssender.model.SmsRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/sms")
public class SmsController {

    @PostMapping("/send")
    public ResponseEntity<String> sendSms(@RequestBody SmsRequest smsRequest) {
        System.out.println("Received request to send SMS to: "
                + smsRequest.getPhoneNumber());
        System.out.println("Message: " + smsRequest.getMessage());

        return ResponseEntity.ok("SMS sent successfully!");
    }
}