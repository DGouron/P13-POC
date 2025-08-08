package com.chatpoc.domain.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantNameTest {
    
    @Test
    void shouldCreateValidParticipantName() {
        String validName = "John Doe";
        
        ParticipantName name = new ParticipantName(validName);
        
        assertEquals(validName, name.value());
        assertEquals(validName, name.toString());
    }
    
    @Test
    void shouldTrimWhitespace() {
        String nameWithSpaces = "  John Doe  ";
        
        ParticipantName name = new ParticipantName(nameWithSpaces);
        
        assertEquals("John Doe", name.value());
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldThrowExceptionForNullOrEmptyName(String invalidName) {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ParticipantName(invalidName)
        );
        
        assertEquals("Participant name cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionForTooShortName() {
        String shortName = "A";
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ParticipantName(shortName)
        );
        
        assertEquals("Participant name must be at least 2 characters long", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionForTooLongName() {
        String longName = "A".repeat(51);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ParticipantName(longName)
        );
        
        assertEquals("Participant name cannot exceed 50 characters", exception.getMessage());
    }
    
    @Test
    void shouldAcceptNameWithExactlyTwoCharacters() {
        String twoCharName = "AB";
        
        ParticipantName name = new ParticipantName(twoCharName);
        
        assertEquals(twoCharName, name.value());
    }
    
    @Test
    void shouldAcceptNameWithExactlyFiftyCharacters() {
        String fiftyCharName = "A".repeat(50);
        
        ParticipantName name = new ParticipantName(fiftyCharName);
        
        assertEquals(fiftyCharName, name.value());
    }
    
    @Test
    void shouldImplementEqualityBasedOnValue() {
        ParticipantName name1 = new ParticipantName("John Doe");
        ParticipantName name2 = new ParticipantName("John Doe");
        ParticipantName name3 = new ParticipantName("Jane Doe");
        
        assertEquals(name1, name2);
        assertNotEquals(name1, name3);
        assertEquals(name1.hashCode(), name2.hashCode());
    }
}