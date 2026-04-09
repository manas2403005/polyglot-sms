package com.sms.smssender.controller;

import com.sms.smssender.model.SmsRequest;
import com.sms.smssender.service.SmsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/sms")
public class SmsController {

    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendSms(@RequestBody SmsRequest smsRequest) {
        String status = smsService.sendSms(smsRequest);
        return ResponseEntity.ok("SMS Status: " + status);
    }
}