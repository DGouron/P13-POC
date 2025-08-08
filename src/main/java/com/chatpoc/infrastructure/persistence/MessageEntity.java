package com.chatpoc.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class MessageEntity {
    
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;
    
    @Column(name = "content", nullable = false, length = 1000)
    private String content;
    
    @Column(name = "sender_name", nullable = false, length = 50)
    private String senderName;
    
    @Column(name = "sender_email", nullable = false, length = 255)
    private String senderEmail;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chat;
    
    protected MessageEntity() {}
    
    public MessageEntity(UUID id, String content, String senderName, String senderEmail, LocalDateTime timestamp, ChatEntity chat) {
        this.id = id;
        this.content = content;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.timestamp = timestamp;
        this.chat = chat;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getSenderEmail() {
        return senderEmail;
    }
    
    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public ChatEntity getChat() {
        return chat;
    }
    
    public void setChat(ChatEntity chat) {
        this.chat = chat;
    }
}