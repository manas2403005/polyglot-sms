
\# Polyglot Distributed SMS Service

\## Architecture
Two microservices communicating via Kafka:
\- **SMS Sender** (Java/Spring Boot) - Port 8080
\- **SMS Store** (GoLang) - Port 8081

\## Tech Stack
| Technology | Purpose |
|---|---|
| Java 17 + Spring Boot 3.2.3 | SMS Sender Service |
| GoLang | SMS Store Service |
| Apache Kafka | Event streaming between services |
| Redis | Blocked users list |
| MongoDB | SMS record storage |
| Docker | Running infrastructure |

\## Prerequisites
\- Java 17
\- Go 1.21+
\- Docker Desktop

\## How to Run

\### Step 1 - Start Infrastructure
Open terminal and run:
cd sms-sender/sms-sender
docker compose up -d

\### Step 2 - Start SMS Sender (Java)
cd sms-sender/sms-sender
./mvnw spring-boot:run

\### Step 3 - Start SMS Store (GoLang)
cd sms-store
go run main.go

\## API Endpoints

\### Send SMS - POST http://localhost:8080/v1/sms/send
Request body:
{
"phoneNumber": "9876543210",
"message": "Your OTP is 1234"
}

Response: SMS Status: SUCCESS or SMS Status: BLOCKED

\### Get SMS History - GET http://localhost:8081/v1/user/{phoneNumber}/messages

\## Managing Block List

Block a user:
docker exec -it redis redis-cli
SET blocked:{phoneNumber} true

Unblock a user:
docker exec -it redis redis-cli
DEL blocked:{phoneNumber}

View all blocked numbers:
docker exec -it redis redis-cli
KEYS blocked:*

\## End-to-End Flow
1. Client calls POST /v1/sms/send
2. Java service checks Redis block list
3. If not blocked, mocks SMS send to 3P vendor
4. Java service publishes event to Kafka
5. GoLang service consumes event from Kafka
6. GoLang service saves record to MongoDB
7. Client retrieves history via GET /v1/user/{phoneNumber}/messages

---

