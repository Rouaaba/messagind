package com.app.messaging.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.messaging.domain.Message;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, Integer> {
    @Query("SELECT m FROM Message m WHERE (m.senderId = :userId1 AND m.recipientId = :userId2) OR (m.senderId = :userId2 AND m.recipientId = :userId1) ORDER BY m.timestamp ASC")
    List<Message> findMessagesBetweenUsers(@Param("userId1") int userId1, @Param("userId2") int userId2);
}


