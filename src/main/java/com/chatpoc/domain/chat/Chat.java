package com.chatpoc.domain.chat;

import com.chatpoc.domain.chat.events.ChatCreatedEvent;
import com.chatpoc.domain.chat.events.MessageSentEvent;

import java.time.LocalDateTime;
import java.util.*;

public class Chat {
    
    private final UUID id;
    private final String name;
    private final Set<Participant> participants;
    private final List<Message> messages;
    private final LocalDateTime createdAt;
    private final List<Object> domainEvents;
    
    private Chat(UUID id, String name, Participant creator, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id, "Chat id cannot be null");
        this.name = validateName(name);
        this.participants = new HashSet<>();
        this.participants.add(Objects.requireNonNull(creator, "Creator cannot be null"));
        this.messages = new ArrayList<>();
        this.createdAt = Objects.requireNonNull(createdAt, "Created date cannot be null");
        this.domainEvents = new ArrayList<>();
        
        this.domainEvents.add(new ChatCreatedEvent(this.id, this.name, creator));
    }
    
    public static Chat create(String name, Participant creator) {
        return new Chat(UUID.randomUUID(), name, creator, LocalDateTime.now());
    }
    
    public static Chat reconstruct(UUID id, String name, Set<Participant> participants, 
                                 List<Message> messages, LocalDateTime createdAt) {
        Chat chat = new Chat(id, name, participants.iterator().next(), createdAt);
        chat.participants.clear();
        chat.participants.addAll(participants);
        chat.messages.addAll(messages);
        chat.domainEvents.clear();
        return chat;
    }
    
    public Message sendMessage(String content, Participant sender) {
        if (!participants.contains(sender)) {
            addParticipant(sender);
        }
        
        Message message = Message.create(content, sender);
        messages.add(message);
        
        domainEvents.add(new MessageSentEvent(this.id, message));
        
        return message;
    }
    
    public void addParticipant(Participant participant) {
        Objects.requireNonNull(participant, "Participant cannot be null");
        
        if (participants.size() >= 50) {
            throw new IllegalStateException("Chat cannot have more than 50 participants");
        }
        
        participants.add(participant);
    }
    
    public List<Message> getRecentMessages(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        
        int startIndex = Math.max(0, messages.size() - limit);
        return new ArrayList<>(messages.subList(startIndex, messages.size()));
    }
    
    public boolean hasParticipant(Participant participant) {
        return participants.contains(participant);
    }
    
    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Chat name cannot be null or empty");
        }
        
        String trimmedName = name.trim();
        
        if (trimmedName.length() < 3) {
            throw new IllegalArgumentException("Chat name must be at least 3 characters long");
        }
        
        if (trimmedName.length() > 100) {
            throw new IllegalArgumentException("Chat name cannot exceed 100 characters");
        }
        
        return trimmedName;
    }
    
    public UUID getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Set<Participant> getParticipants() {
        return Collections.unmodifiableSet(participants);
    }
    
    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    public void clearDomainEvents() {
        domainEvents.clear();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return Objects.equals(id, chat.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", participantsCount=" + participants.size() +
                ", messagesCount=" + messages.size() +
                ", createdAt=" + createdAt +
                '}';
    }
}