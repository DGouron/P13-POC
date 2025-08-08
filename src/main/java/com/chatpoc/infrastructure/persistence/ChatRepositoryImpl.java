package com.chatpoc.infrastructure.persistence;

import com.chatpoc.domain.chat.Chat;
import com.chatpoc.domain.chat.Message;
import com.chatpoc.domain.chat.Participant;
import com.chatpoc.domain.chat.repository.ChatRepository;
import com.chatpoc.domain.shared.Email;
import com.chatpoc.domain.shared.ParticipantName;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ChatRepositoryImpl implements ChatRepository {
    
    private final JpaChatRepository jpaChatRepository;
    
    public ChatRepositoryImpl(JpaChatRepository jpaChatRepository) {
        this.jpaChatRepository = Objects.requireNonNull(jpaChatRepository);
    }
    
    @Override
    public Chat save(Chat chat) {
        ChatEntity chatEntity = toEntity(chat);
        ChatEntity savedEntity = jpaChatRepository.save(chatEntity);
        return toDomain(savedEntity);
    }
    
    @Override
    public Optional<Chat> findById(UUID id) {
        return jpaChatRepository.findByIdWithDetails(id)
            .map(this::toDomain);
    }
    
    @Override
    public List<Chat> findAll() {
        return jpaChatRepository.findAll().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(UUID id) {
        jpaChatRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return jpaChatRepository.existsById(id);
    }
    
    private ChatEntity toEntity(Chat chat) {
        ChatEntity chatEntity = jpaChatRepository.findById(chat.getId())
            .orElse(new ChatEntity(chat.getId(), chat.getName(), chat.getCreatedAt()));
        
        chatEntity.setName(chat.getName());
        chatEntity.setCreatedAt(chat.getCreatedAt());
        
        Set<ParticipantEntity> participantEntities = chat.getParticipants().stream()
            .map(participant -> new ParticipantEntity(
                participant.getName().value(),
                participant.getEmail().value(),
                chatEntity
            ))
            .collect(Collectors.toSet());
        
        chatEntity.getParticipants().clear();
        chatEntity.getParticipants().addAll(participantEntities);
        
        Set<MessageEntity> messageEntities = chat.getMessages().stream()
            .map(message -> new MessageEntity(
                message.getId(),
                message.getContent(),
                message.getSender().getName().value(),
                message.getSender().getEmail().value(),
                message.getTimestamp(),
                chatEntity
            ))
            .collect(Collectors.toSet());
        
        chatEntity.getMessages().clear();
        chatEntity.getMessages().addAll(messageEntities);
        
        return chatEntity;
    }
    
    private Chat toDomain(ChatEntity chatEntity) {
        Set<Participant> participants = chatEntity.getParticipants().stream()
            .map(participantEntity -> new Participant(
                new ParticipantName(participantEntity.getName()),
                new Email(participantEntity.getEmail())
            ))
            .collect(Collectors.toSet());
        
        List<Message> messages = chatEntity.getMessages().stream()
            .map(messageEntity -> Message.reconstruct(
                messageEntity.getId(),
                messageEntity.getContent(),
                new Participant(
                    new ParticipantName(messageEntity.getSenderName()),
                    new Email(messageEntity.getSenderEmail())
                ),
                messageEntity.getTimestamp()
            ))
            .sorted(Comparator.comparing(Message::getTimestamp))
            .collect(Collectors.toList());
        
        return Chat.reconstruct(
            chatEntity.getId(),
            chatEntity.getName(),
            participants,
            messages,
            chatEntity.getCreatedAt()
        );
    }
}