package com.chatpoc.domain.chat.events;

import com.chatpoc.domain.chat.Message;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class MessageSentEvent {
    
    private final UUID eventId;
    private final UUID chatId;
    private final Message message;
    private final LocalDateTime occurredAt;
    
    public MessageSentEvent(UUID chatId, Message message) {
        this.eventId = UUID.randomUUID();
        this.chatId = Objects.requireNonNull(chatId, "Chat id cannot be null");
        this.message = Objects.requireNonNull(message, "Message cannot be null");
        this.occurredAt = LocalDateTime.now();
    }
    
    public UUID getEventId() {
        return eventId;
    }
    
    public UUID getChatId() {
        return chatId;
    }
    
    public Message getMessage() {
        return message;
    }
    
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageSentEvent that = (MessageSentEvent) o;
        return Objects.equals(eventId, that.eventId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
    
    @Override
    public String toString() {
        return "MessageSentEvent{" +
                "eventId=" + eventId +
                ", chatId=" + chatId +
                ", message=" + message +
                ", occurredAt=" + occurredAt +
                '}';
    }
}