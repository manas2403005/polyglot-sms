package com.sms.smssender;

import com.sms.smssender.model.SmsRequest;
import com.sms.smssender.model.SmsEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SmsSenderApplicationTests {

	@Test
	void testSmsRequest_GettersSetters() {
		SmsRequest request = new SmsRequest();
		request.setPhoneNumber("9876543210");
		request.setMessage("Hello!");

		assertEquals("9876543210", request.getPhoneNumber());
		assertEquals("Hello!", request.getMessage());
	}

	@Test
	void testSmsRequest_Constructor() {
		SmsRequest request = new SmsRequest("9876543210", "Hello!");

		assertEquals("9876543210", request.getPhoneNumber());
		assertEquals("Hello!", request.getMessage());
	}

	@Test
	void testSmsEvent_Constructor() {
		SmsEvent event = new SmsEvent("9876543210", "Hello!", "SUCCESS");

		assertEquals("9876543210", event.getPhoneNumber());
		assertEquals("Hello!", event.getMessage());
		assertEquals("SUCCESS", event.getStatus());
	}

	@Test
	void testSmsEvent_GettersSetters() {
		SmsEvent event = new SmsEvent();
		event.setPhoneNumber("9876543210");
		event.setMessage("Hello!");
		event.setStatus("SUCCESS");

		assertEquals("9876543210", event.getPhoneNumber());
		assertEquals("Hello!", event.getMessage());
		assertEquals("SUCCESS", event.getStatus());
	}
}