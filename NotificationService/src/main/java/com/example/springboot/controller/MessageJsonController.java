package com.example.springboot.controller;

import com.example.springboot.dto.User;
import com.example.springboot.publisher.RabbitMQJsonProducer;
import com.example.springboot.publisher.RabbitMQProducer; // 1. Import this
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // 2. Change to wildcard

@RestController
@RequestMapping("/api/v1")
public class MessageJsonController {

    private RabbitMQJsonProducer jsonProducer;
    private RabbitMQProducer producer; // 3. Add the string producer

    // 4. Update the constructor to get both producers
    public MessageJsonController(RabbitMQJsonProducer jsonProducer, RabbitMQProducer producer) {
        this.jsonProducer = jsonProducer;
        this.producer = producer;
    }

    // 5. Add the endpoint for sending plain text messages
    // http://localhost:8082/api/v1/publish-text?message=hello
    @GetMapping("/publish-text")
    public ResponseEntity<String> sendStringMessage(@RequestParam("message") String message) {
        producer.sendMessage(message);
        return ResponseEntity.ok("Message sent to RabbitMQ...");
    }

    // 6. Keep your original endpoint for JSON (I renamed the path for clarity)
    // http://localhost:8082/api/v1/publish-json
    @PostMapping("/publish-json")
    public ResponseEntity<String> sendJsonMessage(@RequestBody User user) {
        jsonProducer.sendJsonMessage(user);
        return ResponseEntity.ok("Json message sent to RabbitMQ...");
    }
}