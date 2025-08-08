package com.chatpoc.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "chats")
public class ChatEntity {
    
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MessageEntity> messages = new HashSet<>();
    
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ParticipantEntity> participants = new HashSet<>();
    
    protected ChatEntity() {}
    
    public ChatEntity(UUID id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Set<MessageEntity> getMessages() {
        return messages;
    }
    
    public void setMessages(Set<MessageEntity> messages) {
        this.messages = messages;
    }
    
    public Set<ParticipantEntity> getParticipants() {
        return participants;
    }
    
    public void setParticipants(Set<ParticipantEntity> participants) {
        this.participants = participants;
    }
}