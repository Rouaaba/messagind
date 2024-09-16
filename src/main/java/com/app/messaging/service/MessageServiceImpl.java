package com.app.messaging.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.messaging.domain.Message;
import com.app.messaging.repo.MessageRepo;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepo messageRepository;

    @Override
    public void save(Message message) {
        messageRepository.save(message);
    }


    public List<Message> getMessagesBetweenUsers(int senderId, int recipientId) {
    List<Message> messages = messageRepository.findMessagesBetweenUsers(senderId, recipientId);
    return messages.stream()
                   .sorted(Comparator.comparing(
                       message -> message.getTimestamp() != null ? message.getTimestamp() : LocalDateTime.MIN))
                   .collect(Collectors.toList());
}

    
    
}
