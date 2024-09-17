package com.app.messaging.controller;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
import com.app.messaging.domain.MessageRequest; // Adjust the package if necessary

@RestController
@RequestMapping("messages")
public class MessageController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody MessageRequest messageRequest) {
        User sender = userService.getCurrentAuthenticatedUser();
        User recipient = userService.findByEmailOrUsername(messageRequest.getRecipient());

        if (recipient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipient not found");
        }

        Message message = new Message();
        message.setSenderId(sender.getId());
        message.setRecipientId(recipient.getId());
        message.setContent(messageRequest.getContent());

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

    @GetMapping("/conversation")
    public ResponseEntity<List<String>> getConversation(@RequestParam("email") String email) {
        try {
            User sender = userService.getCurrentAuthenticatedUser();
            User recipient = userService.findByEmailOrUsername(email);

            if (recipient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            if (sender == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            List<Message> messages = messageService.getMessagesBetweenUsers(sender.getId(), recipient.getId());

            if (messages == null || messages.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            List<String> formattedMessages = messages.stream()
                .sorted(Comparator.comparing(Message::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(message -> {
                    String senderUsername = message.getSender() != null ? message.getSender().getUsername() : "Unknown";
                    String content = message.getContent() != null ? message.getContent() : "No content";
                    LocalDateTime timestamp = message.getTimestamp();
                    String formattedTimestamp = (timestamp != null) ? timestamp.toString() : "No timestamp";

                    return senderUsername + ": " + content + " (" + formattedTimestamp + ")";
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(formattedMessages);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
