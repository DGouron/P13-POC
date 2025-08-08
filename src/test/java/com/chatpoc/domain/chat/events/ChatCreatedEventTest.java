package com.chatpoc.domain.chat.events;

import com.chatpoc.domain.chat.Participant;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChatCreatedEventTest {
    
    private final UUID chatId = UUID.randomUUID();
    private final String chatName = "Test Chat";
    private final Participant creator = Participant.of("John Doe", "john@example.com");
    
    @Test
    void shouldCreateEventWithAllFields() {
        ChatCreatedEvent event = new ChatCreatedEvent(chatId, chatName, creator);
        
        assertNotNull(event.getEventId());
        assertEquals(chatId, event.getChatId());
        assertEquals(chatName, event.getChatName());
        assertEquals(creator, event.getCreator());
        assertNotNull(event.getOccurredAt());
        assertTrue(event.getOccurredAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
    
    @Test
    void shouldThrowExceptionForNullChatId() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new ChatCreatedEvent(null, chatName, creator)
        );
        
        assertEquals("Chat id cannot be null", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionForNullChatName() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new ChatCreatedEvent(chatId, null, creator)
        );
        
        assertEquals("Chat name cannot be null", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionForNullCreator() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new ChatCreatedEvent(chatId, chatName, null)
        );
        
        assertEquals("Creator cannot be null", exception.getMessage());
    }
    
    @Test
    void shouldImplementEqualityBasedOnEventId() {
        ChatCreatedEvent event1 = new ChatCreatedEvent(chatId, chatName, creator);
        ChatCreatedEvent event2 = new ChatCreatedEvent(chatId, chatName, creator);
        
        assertNotEquals(event1, event2);
        assertNotEquals(event1.getEventId(), event2.getEventId());
    }
    
    @Test
    void shouldHaveReadableToStringRepresentation() {
        ChatCreatedEvent event = new ChatCreatedEvent(chatId, chatName, creator);
        
        String toString = event.toString();
        
        assertTrue(toString.contains(event.getEventId().toString()));
        assertTrue(toString.contains(chatId.toString()));
        assertTrue(toString.contains(chatName));
        assertTrue(toString.contains("ChatCreatedEvent"));
    }
}