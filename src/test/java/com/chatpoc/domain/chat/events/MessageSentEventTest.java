package com.chatpoc.domain.chat.events;

import com.chatpoc.domain.chat.Message;
import com.chatpoc.domain.chat.Participant;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MessageSentEventTest {
    
    private final Participant sender = Participant.of("John Doe", "john@example.com");
    private final Message message = Message.create("Hello, world!", sender);
    private final UUID chatId = UUID.randomUUID();
    
    @Test
    void shouldCreateEventWithChatIdAndMessage() {
        MessageSentEvent event = new MessageSentEvent(chatId, message);
        
        assertNotNull(event.getEventId());
        assertEquals(chatId, event.getChatId());
        assertEquals(message, event.getMessage());
        assertNotNull(event.getOccurredAt());
        assertTrue(event.getOccurredAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
    
    @Test
    void shouldThrowExceptionForNullChatId() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new MessageSentEvent(null, message)
        );
        
        assertEquals("Chat id cannot be null", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionForNullMessage() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new MessageSentEvent(chatId, null)
        );
        
        assertEquals("Message cannot be null", exception.getMessage());
    }
    
    @Test
    void shouldImplementEqualityBasedOnEventId() {
        MessageSentEvent event1 = new MessageSentEvent(chatId, message);
        MessageSentEvent event2 = new MessageSentEvent(chatId, message);
        
        assertNotEquals(event1, event2);
        assertNotEquals(event1.getEventId(), event2.getEventId());
    }
    
    @Test
    void shouldHaveReadableToStringRepresentation() {
        MessageSentEvent event = new MessageSentEvent(chatId, message);
        
        String toString = event.toString();
        
        assertTrue(toString.contains(event.getEventId().toString()));
        assertTrue(toString.contains(chatId.toString()));
        assertTrue(toString.contains("MessageSentEvent"));
    }
}