package com.app.messaging.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.messaging.domain.Message;
import com.app.messaging.domain.MessageRequest;
import com.app.messaging.domain.User;
import com.app.messaging.service.MessageService;
import com.app.messaging.service.UserService;

@RestController
    @RequestMapping("messages")
    public class MessageController {

        @Autowired
        private UserService userService;  // To retrieve users
        @Autowired
        private MessageService messageService;  // To save messages

        @PostMapping("/send")
        public ResponseEntity<String> sendMessage(@RequestBody MessageRequest messageRequest) {
            // Get the current logged-in user (the sender)
            User sender = userService.getCurrentAuthenticatedUser();  // Fetch current user (already logged in)

            // Find the recipient by email or username
            User recipient = userService.findByEmailOrUsername(messageRequest.getRecipient());
            if (recipient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipient not found");
            }

            // Create a new Message entity
            Message message = new Message();
            message.setSenderId(sender.getId());
            message.setRecipientId(recipient.getId());
            message.setContent(messageRequest.getContent());

            // Save the message
            messageService.save(message);

            return ResponseEntity.ok("Message sent successfully");
        }

        @GetMapping("/history")
        public ResponseEntity<List<Message>> getMessageHistory(@RequestParam("email") String email) {
            User sender = userService.getCurrentAuthenticatedUser();
            User recipient = userService.findByEmailOrUsername(email);
            if (recipient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            List<Message> messages = messageService.getMessagesBetweenUsers(sender.getId(), recipient.getId());
            return ResponseEntity.ok(messages);
        }
}