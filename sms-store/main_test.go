package main

import (
	"testing"
)

func TestSmsRecord_Fields(t *testing.T) {
	record := SmsRecord{
		PhoneNumber: "9876543210",
		Message:     "Hello!",
		Status:      "SUCCESS",
	}

	if record.PhoneNumber != "9876543210" {
		t.Errorf("Expected 9876543210, got %s", record.PhoneNumber)
	}

	if record.Message != "Hello!" {
		t.Errorf("Expected Hello!, got %s", record.Message)
	}

	if record.Status != "SUCCESS" {
		t.Errorf("Expected SUCCESS, got %s", record.Status)
	}
}

func TestSmsRecord_BlockedStatus(t *testing.T) {
	record := SmsRecord{
		PhoneNumber: "1111111111",
		Message:     "Test",
		Status:      "BLOCKED",
	}

	if record.Status != "BLOCKED" {
		t.Errorf("Expected BLOCKED, got %s", record.Status)
	}
}

func TestSmsRecord_EmptyFields(t *testing.T) {
	record := SmsRecord{}

	if record.PhoneNumber != "" {
		t.Errorf("Expected empty phone number")
	}

	if record.Message != "" {
		t.Errorf("Expected empty message")
	}
}
