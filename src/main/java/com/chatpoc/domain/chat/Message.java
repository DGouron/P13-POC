package com.chatpoc.domain.chat;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Message {
    
    private final UUID id;
    private final String content;
    private final Participant sender;
    private final LocalDateTime timestamp;
    
    private Message(UUID id, String content, Participant sender, LocalDateTime timestamp) {
        this.id = Objects.requireNonNull(id, "Message id cannot be null");
        this.content = validateContent(content);
        this.sender = Objects.requireNonNull(sender, "Message sender cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Message timestamp cannot be null");
    }
    
    public static Message create(String content, Participant sender) {
        return new Message(
            UUID.randomUUID(),
            content,
            sender,
            LocalDateTime.now()
        );
    }
    
    public static Message reconstruct(UUID id, String content, Participant sender, LocalDateTime timestamp) {
        return new Message(id, content, sender, timestamp);
    }
    
    private String validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be null or empty");
        }
        
        String trimmedContent = content.trim();
        
        if (trimmedContent.length() > 1000) {
            throw new IllegalArgumentException("Message content cannot exceed 1000 characters");
        }
        
        return trimmedContent;
    }
    
    public UUID getId() {
        return id;
    }
    
    public String getContent() {
        return content;
    }
    
    public Participant getSender() {
        return sender;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", sender=" + sender +
                ", timestamp=" + timestamp +
                '}';
    }
}