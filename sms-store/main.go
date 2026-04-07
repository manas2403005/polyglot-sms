package main

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"time"

	"github.com/segmentio/kafka-go"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

// SmsRecord represents an SMS record in MongoDB
type SmsRecord struct {
	PhoneNumber string    `json:"phoneNumber" bson:"phoneNumber"`
	Message     string    `json:"message" bson:"message"`
	Status      string    `json:"status" bson:"status"`
	CreatedAt   time.Time `json:"createdAt" bson:"createdAt"`
}

var mongoClient *mongo.Client
var smsCollection *mongo.Collection

func main() {
	// Connect to MongoDB
	connectMongoDB()

	// Start Kafka consumer in background
	go consumeKafkaEvents()

	// Start HTTP server
	http.HandleFunc("/v1/user/", getMessagesByUserId)
	fmt.Println("SMS Store running on port 8081...")
	log.Fatal(http.ListenAndServe(":8081", nil))
}

func connectMongoDB() {
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	client, err := mongo.Connect(ctx, options.Client().ApplyURI("mongodb://localhost:27017"))
	if err != nil {
		log.Fatal("MongoDB connection error:", err)
	}

	mongoClient = client
	smsCollection = client.Database("smsdb").Collection("messages")
	fmt.Println("Connected to MongoDB!")
}

func consumeKafkaEvents() {
	reader := kafka.NewReader(kafka.ReaderConfig{
		Brokers: []string{"localhost:9092"},
		Topic:   "sms-events",
		GroupID: "sms-store-group",
	})

	fmt.Println("Listening to Kafka topic: sms-events...")

	for {
		msg, err := reader.ReadMessage(context.Background())
		if err != nil {
			log.Println("Kafka read error:", err)
			continue
		}

		fmt.Println("Received event from Kafka:", string(msg.Value))

		var record SmsRecord
		if err := json.Unmarshal(msg.Value, &record); err != nil {
			log.Println("JSON parse error:", err)
			continue
		}

		record.CreatedAt = time.Now()
		saveToMongoDB(record)
	}
}

func saveToMongoDB(record SmsRecord) {
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	_, err := smsCollection.InsertOne(ctx, record)
	if err != nil {
		log.Println("MongoDB insert error:", err)
		return
	}

	fmt.Println("Saved SMS record to MongoDB:", record.PhoneNumber)
}

func getMessagesByUserId(w http.ResponseWriter, r *http.Request) {
	phoneNumber := r.URL.Path[len("/v1/user/"):]
	phoneNumber = phoneNumber[:len(phoneNumber)-len("/messages")]

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	cursor, err := smsCollection.Find(ctx, bson.M{"phoneNumber": phoneNumber})
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	defer cursor.Close(ctx)

	var records []SmsRecord
	if err := cursor.All(ctx, &records); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(records)
}