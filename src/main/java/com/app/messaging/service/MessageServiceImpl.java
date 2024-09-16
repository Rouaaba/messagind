package com.app.messaging.service;

import java.util.List;

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

    @Override
    public List<Message> getMessagesBetweenUsers(int senderId, int recipientId) {
        return messageRepository.findMessagesBySenderIdAndRecipientId(senderId, recipientId);
    }
}
