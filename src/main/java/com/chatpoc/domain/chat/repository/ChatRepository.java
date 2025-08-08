package com.chatpoc.domain.chat.repository;

import com.chatpoc.domain.chat.Chat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository {
    
    Chat save(Chat chat);
    
    Optional<Chat> findById(UUID id);
    
    List<Chat> findAll();
    
    void deleteById(UUID id);
    
    boolean existsById(UUID id);
}