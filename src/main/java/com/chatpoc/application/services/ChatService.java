package com.chatpoc.application.services;

import com.chatpoc.application.commands.CreateChatCommand;
import com.chatpoc.application.commands.SendMessageCommand;
import com.chatpoc.application.queries.GetChatQuery;
import com.chatpoc.application.queries.GetRecentMessagesQuery;
import com.chatpoc.domain.chat.Chat;
import com.chatpoc.domain.chat.Message;
import com.chatpoc.domain.chat.Participant;
import com.chatpoc.domain.chat.repository.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class ChatService {
    
    private final ChatRepository chatRepository;
    private final DomainEventPublisher eventPublisher;
    
    public ChatService(ChatRepository chatRepository, DomainEventPublisher eventPublisher) {
        this.chatRepository = Objects.requireNonNull(chatRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }
    
    public Chat createChat(CreateChatCommand command) {
        Objects.requireNonNull(command, "CreateChatCommand cannot be null");
        
        Participant creator = Participant.of(command.creatorName(), command.creatorEmail());
        Chat chat = Chat.create(command.chatName(), creator);
        
        Chat savedChat = chatRepository.save(chat);
        
        publishDomainEvents(savedChat);
        
        return savedChat;
    }
    
    public Message sendMessage(SendMessageCommand command) {
        Objects.requireNonNull(command, "SendMessageCommand cannot be null");
        
        Chat chat = chatRepository.findById(command.chatId())
            .orElseThrow(() -> new IllegalArgumentException("Chat not found with id: " + command.chatId()));
        
        Participant sender = Participant.of(command.senderName(), command.senderEmail());
        Message message = chat.sendMessage(command.content(), sender);
        
        Chat savedChat = chatRepository.save(chat);
        
        publishDomainEvents(savedChat);
        
        return message;
    }
    
    @Transactional(readOnly = true)
    public Optional<Chat> getChat(GetChatQuery query) {
        Objects.requireNonNull(query, "GetChatQuery cannot be null");
        return chatRepository.findById(query.chatId());
    }
    
    @Transactional(readOnly = true)
    public List<Message> getRecentMessages(GetRecentMessagesQuery query) {
        Objects.requireNonNull(query, "GetRecentMessagesQuery cannot be null");
        
        Chat chat = chatRepository.findById(query.chatId())
            .orElseThrow(() -> new IllegalArgumentException("Chat not found with id: " + query.chatId()));
        
        return chat.getRecentMessages(query.limit());
    }
    
    @Transactional(readOnly = true)
    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }
    
    private void publishDomainEvents(Chat chat) {
        chat.getDomainEvents().forEach(eventPublisher::publish);
        chat.clearDomainEvents();
    }
}