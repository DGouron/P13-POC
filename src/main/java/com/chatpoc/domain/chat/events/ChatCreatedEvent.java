package com.chatpoc.domain.chat.events;

import com.chatpoc.domain.chat.Participant;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ChatCreatedEvent {
    
    private final UUID eventId;
    private final UUID chatId;
    private final String chatName;
    private final Participant creator;
    private final LocalDateTime occurredAt;
    
    public ChatCreatedEvent(UUID chatId, String chatName, Participant creator) {
        this.eventId = UUID.randomUUID();
        this.chatId = Objects.requireNonNull(chatId, "Chat id cannot be null");
        this.chatName = Objects.requireNonNull(chatName, "Chat name cannot be null");
        this.creator = Objects.requireNonNull(creator, "Creator cannot be null");
        this.occurredAt = LocalDateTime.now();
    }
    
    public UUID getEventId() {
        return eventId;
    }
    
    public UUID getChatId() {
        return chatId;
    }
    
    public String getChatName() {
        return chatName;
    }
    
    public Participant getCreator() {
        return creator;
    }
    
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatCreatedEvent that = (ChatCreatedEvent) o;
        return Objects.equals(eventId, that.eventId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
    
    @Override
    public String toString() {
        return "ChatCreatedEvent{" +
                "eventId=" + eventId +
                ", chatId=" + chatId +
                ", chatName='" + chatName + '\'' +
                ", creator=" + creator +
                ", occurredAt=" + occurredAt +
                '}';
    }
}