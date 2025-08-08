package com.chatpoc.domain.chat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {
    
    private final Participant sender = Participant.of("John Doe", "john@example.com");
    
    @Test
    void shouldCreateMessageWithValidContent() {
        String content = "Hello, world!";
        
        Message message = Message.create(content, sender);
        
        assertNotNull(message.getId());
        assertEquals(content, message.getContent());
        assertEquals(sender, message.getSender());
        assertNotNull(message.getTimestamp());
        assertTrue(message.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
    
    @Test
    void shouldReconstructMessageWithAllFields() {
        UUID id = UUID.randomUUID();
        String content = "Hello, world!";
        LocalDateTime timestamp = LocalDateTime.now();
        
        Message message = Message.reconstruct(id, content, sender, timestamp);
        
        assertEquals(id, message.getId());
        assertEquals(content, message.getContent());
        assertEquals(sender, message.getSender());
        assertEquals(timestamp, message.getTimestamp());
    }
    
    @Test
    void shouldTrimWhitespaceFromContent() {
        String contentWithSpaces = "  Hello, world!  ";
        
        Message message = Message.create(contentWithSpaces, sender);
        
        assertEquals("Hello, world!", message.getContent());
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldThrowExceptionForNullOrEmptyContent(String invalidContent) {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Message.create(invalidContent, sender)
        );
        
        assertEquals("Message content cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionForTooLongContent() {
        String longContent = "A".repeat(1001);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Message.create(longContent, sender)
        );
        
        assertEquals("Message content cannot exceed 1000 characters", exception.getMessage());
    }
    
    @Test
    void shouldAcceptContentWithExactlyThousandCharacters() {
        String thousandCharContent = "A".repeat(1000);
        
        Message message = Message.create(thousandCharContent, sender);
        
        assertEquals(thousandCharContent, message.getContent());
    }
    
    @Test
    void shouldThrowExceptionForNullSender() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Message.create("Hello", null)
        );
        
        assertEquals("Message sender cannot be null", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionForNullId() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Message.reconstruct(null, "Hello", sender, LocalDateTime.now())
        );
        
        assertEquals("Message id cannot be null", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionForNullTimestamp() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Message.reconstruct(UUID.randomUUID(), "Hello", sender, null)
        );
        
        assertEquals("Message timestamp cannot be null", exception.getMessage());
    }
    
    @Test
    void shouldImplementEqualityBasedOnId() {
        UUID id = UUID.randomUUID();
        Message message1 = Message.reconstruct(id, "Hello", sender, LocalDateTime.now());
        Message message2 = Message.reconstruct(id, "Different content", sender, LocalDateTime.now());
        
        assertEquals(message1, message2);
        assertEquals(message1.hashCode(), message2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualIfIdsDiffer() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();
        
        Message message1 = Message.reconstruct(id1, "Hello", sender, timestamp);
        Message message2 = Message.reconstruct(id2, "Hello", sender, timestamp);
        
        assertNotEquals(message1, message2);
    }
    
    @Test
    void shouldHaveReadableToStringRepresentation() {
        Message message = Message.create("Hello, world!", sender);
        
        String toString = message.toString();
        
        assertTrue(toString.contains("Hello, world!"));
        assertTrue(toString.contains(message.getId().toString()));
    }
}