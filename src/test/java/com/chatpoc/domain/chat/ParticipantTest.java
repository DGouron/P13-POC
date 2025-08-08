package com.chatpoc.domain.chat;

import com.chatpoc.domain.shared.Email;
import com.chatpoc.domain.shared.ParticipantName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {
    
    @Test
    void shouldCreateParticipantWithValidNameAndEmail() {
        ParticipantName name = new ParticipantName("John Doe");
        Email email = new Email("john@example.com");
        
        Participant participant = new Participant(name, email);
        
        assertEquals(name, participant.getName());
        assertEquals(email, participant.getEmail());
    }
    
    @Test
    void shouldCreateParticipantUsingStaticFactoryMethod() {
        String nameStr = "John Doe";
        String emailStr = "john@example.com";
        
        Participant participant = Participant.of(nameStr, emailStr);
        
        assertEquals(nameStr, participant.getName().value());
        assertEquals(emailStr, participant.getEmail().value());
    }
    
    @Test
    void shouldThrowExceptionForNullName() {
        Email email = new Email("john@example.com");
        
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Participant(null, email)
        );
        
        assertEquals("Participant name cannot be null", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionForNullEmail() {
        ParticipantName name = new ParticipantName("John Doe");
        
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Participant(name, null)
        );
        
        assertEquals("Participant email cannot be null", exception.getMessage());
    }
    
    @Test
    void shouldImplementEqualityBasedOnEmail() {
        ParticipantName name1 = new ParticipantName("John Doe");
        ParticipantName name2 = new ParticipantName("Johnny Doe");
        Email email = new Email("john@example.com");
        
        Participant participant1 = new Participant(name1, email);
        Participant participant2 = new Participant(name2, email);
        
        assertEquals(participant1, participant2);
        assertEquals(participant1.hashCode(), participant2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualIfEmailsDiffer() {
        ParticipantName name = new ParticipantName("John Doe");
        Email email1 = new Email("john@example.com");
        Email email2 = new Email("john@other.com");
        
        Participant participant1 = new Participant(name, email1);
        Participant participant2 = new Participant(name, email2);
        
        assertNotEquals(participant1, participant2);
    }
    
    @Test
    void shouldHaveReadableToStringRepresentation() {
        Participant participant = Participant.of("John Doe", "john@example.com");
        
        String toString = participant.toString();
        
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("john@example.com"));
    }
}