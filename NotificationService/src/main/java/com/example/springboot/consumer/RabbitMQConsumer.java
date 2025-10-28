package com.example.springboot.consumer;

import com.example.springboot.service.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RabbitMQConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    // Regex to find "(OwnerID: 123)" in the message string
    private static final Pattern OWNER_ID_PATTERN = Pattern.compile(".*\\(OwnerID: (\\d+)\\).*");

    private final UserServiceClient userServiceClient;

    public RabbitMQConsumer(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consume(String message){
        LOGGER.info(String.format("Received Message -> %s", message));

//        // Parse the OwnerID from the message
//        Matcher matcher = OWNER_ID_PATTERN.matcher(message);
//
//        if (matcher.matches()) {
//            try {
//                String idString = matcher.group(1);
//                Long ownerId = Long.parseLong(idString);
//
//                // Call the UserServiceClient
//                String email = userServiceClient.getEmailByUserId(ownerId);
//
//                if (email != null) {
//                    LOGGER.info("Fetched email for OwnerID {}: {}", ownerId, email);
//                    //
//                    // TODO: Call your real EmailService here
//                    // emailService.sendNotification(email, "New Notification", message);
//                    //
//                } else {
//                    LOGGER.warn("Could not find email for OwnerID: {}", ownerId);
//                }
//            } catch (Exception e) {
//                LOGGER.error("Error processing message: {}", e.getMessage());
//            }
//        } else {
//            LOGGER.warn("Could not find OwnerID in message: {}", message);
//        }
    }
}