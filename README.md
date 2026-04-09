# Polyglot Distributed SMS Service

A distributed SMS notification system built with two microservices communicating via Apache Kafka.

## Architecture
Client
│
▼
SMS Sender Service (Java/Spring Boot) - Port 8080
│
├── Checks Redis for blocked users
├── Mocks 3rd party SMS vendor call
└── Publishes event to Kafka
│
▼
Kafka Topic: sms-events
│
▼
SMS Store Service (GoLang) - Port 8081
│
├── Consumes Kafka events
├── Saves records to MongoDB
└── Provides SMS history API
## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | SMS Sender language |
| Spring Boot | 3.2.3 | SMS Sender framework |
| GoLang | 1.21+ | SMS Store language |
| Apache Kafka | 7.3.0 | Event streaming |
| Redis | latest | Blocked users list |
| MongoDB | latest | SMS record storage |
| Docker | latest | Infrastructure |

## Project Structure
polyglot-sms/
├── README.md
├── sms-sender/                          ← Java Spring Boot Service
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/sms/smssender/
│   │   │   │   ├── controller/
│   │   │   │   │   └── SmsController.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── SmsService.java
│   │   │   │   │   └── KafkaProducerService.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── SmsRequest.java
│   │   │   │   │   └── SmsEvent.java
│   │   │   │   └── SmsSenderApplication.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   │       └── java/com/sms/smssender/
│   │           └── SmsSenderApplicationTests.java
│   ├── docker-compose.yml
│   └── pom.xml
└── sms-store/                           ← GoLang Service
├── main.go
├── main_test.go
├── go.mod
└── go.sum

## Prerequisites

Before running the project, make sure you have installed:

- **Java 17** - Download from https://adoptium.net
- **Go 1.21+** - Download from https://go.dev/dl
- **Docker Desktop** - Download from https://docker.com

## Setup and Running

### Step 1 - Clone the Repository

```bash
git clone git@github.com:manas2403005/polyglot-sms.git
cd polyglot-sms
```

### Step 2 - Start Infrastructure (Docker)

```bash
cd sms-sender
docker compose up -d
```

Verify all containers are running:
```bash
docker ps
```

You should see these 4 containers:
- mongodb (port 27017)
- redis (port 6379)
- zookeeper (port 2181)
- kafka (port 9092)

### Step 3 - Start SMS Sender (Java)

Open a new terminal:
```bash
cd sms-sender
./mvnw spring-boot:run
```

Wait for this message:
Tomcat started on port 8080
Started SmsSenderApplication

### Step 4 - Start SMS Store (GoLang)

Open another new terminal:
```bash
cd sms-store
go run main.go
```

Wait for this message:
Connected to MongoDB!
SMS Store running on port 8081...
Listening to Kafka topic: sms-events...

## API Endpoints

### SMS Sender Service (Port 8080)

#### Send SMS

POST http://localhost:8080/v1/sms/send
Content-Type: application/json
{
"phoneNumber": "9876543210",
"message": "Your OTP is 1234"
}

**Success Response (200):**
SMS Status: SUCCESS
**Blocked Response (200):**
SMS Status: BLOCKED

### SMS Store Service (Port 8081)

#### Get SMS History by Phone Number
GET http://localhost:8081/v1/user/{phoneNumber}/messages
**Response (200):**
```json
[
  {
    "phoneNumber": "9876543210",
    "message": "Your OTP is 1234",
    "status": "SUCCESS",
    "createdAt": "2026-04-06T12:34:25.61Z"
  }
]
```

## Managing Blocked Users

### Block a phone number:
```bash
docker exec -it redis redis-cli
SET blocked:{phoneNumber} true
exit
```

### Unblock a phone number:
```bash
docker exec -it redis redis-cli
DEL blocked:{phoneNumber}
exit
```

### View all blocked numbers:
```bash
docker exec -it redis redis-cli
KEYS blocked:*
exit
```

## End-to-End Demonstration

### Step 1 - Block a number:
```bash
docker exec -it redis redis-cli
SET blocked:1111111111 true
exit
```

### Step 2 - Try sending to blocked number:
Send POST request to http://localhost:8080/v1/sms/send:
```json
{
  "phoneNumber": "1111111111",
  "message": "This should be blocked!"
}
```
Expected: SMS Status: BLOCKED

### Step 3 - Send to unblocked number:
Send POST request to http://localhost:8080/v1/sms/send:
```json
{
  "phoneNumber": "9999999999",
  "message": "Hello! Your OTP is 1234"
}
```
Expected: SMS Status: SUCCESS

### Step 4 - Check GoLang service logs:
In the GoLang terminal you should see:
Received event from Kafka: {"phoneNumber":"9999999999","message":"Hello! Your OTP is 1234","status":"SUCCESS"}
Saved SMS record to MongoDB: 9999999999

### Step 5 - Retrieve SMS history:
Open browser or send GET request to:
http://localhost:8081/v1/user/9999999999/messages

Expected:
```json
[
  {
    "phoneNumber": "9999999999",
    "message": "Hello! Your OTP is 1234",
    "status": "SUCCESS",
    "createdAt": "2026-04-06T12:34:25.61Z"
  }
]
```

### Step 6 - Verify blocked number has no history:
http://localhost:8081/v1/user/1111111111/messages

Expected: []

## Running Tests

### Java Tests:
```bash
cd sms-sender
./mvnw test
```

### GoLang Tests:
```bash
cd sms-store
go test ./...
```

## Stopping the Services

### Stop Spring Boot:
Press Ctrl+C in the Spring Boot terminal

### Stop GoLang:
Press Ctrl+C in the GoLang terminal

### Stop Docker containers:
```bash
cd sms-sender
docker compose down
```