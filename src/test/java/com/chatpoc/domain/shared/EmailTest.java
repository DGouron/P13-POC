package com.chatpoc.domain.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {
    
    @Test
    void shouldCreateValidEmail() {
        String validEmail = "test@example.com";
        
        Email email = new Email(validEmail);
        
        assertEquals(validEmail, email.value());
        assertEquals(validEmail, email.toString());
    }
    
    @Test
    void shouldNormalizeEmailToLowerCase() {
        String upperCaseEmail = "Test@EXAMPLE.COM";
        
        Email email = new Email(upperCaseEmail);
        
        assertEquals("test@example.com", email.value());
    }
    
    @Test
    void shouldTrimWhitespace() {
        String emailWithSpaces = "  test@example.com  ";
        
        Email email = new Email(emailWithSpaces);
        
        assertEquals("test@example.com", email.value());
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldThrowExceptionForNullOrEmptyEmail(String invalidEmail) {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Email(invalidEmail)
        );
        
        assertEquals("Email cannot be null or empty", exception.getMessage());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "invalid-email",
        "@example.com",
        "test@",
        "test.example.com",
        "test@.com",
        "test@com",
        "test@example.",
        "test..test@example.com"
    })
    void shouldThrowExceptionForInvalidEmailFormat(String invalidEmail) {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Email(invalidEmail)
        );
        
        assertTrue(exception.getMessage().startsWith("Invalid email format:"));
    }
    
    @Test
    void shouldImplementEqualityBasedOnValue() {
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("test@example.com");
        Email email3 = new Email("other@example.com");
        
        assertEquals(email1, email2);
        assertNotEquals(email1, email3);
        assertEquals(email1.hashCode(), email2.hashCode());
    }
}