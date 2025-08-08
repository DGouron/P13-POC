package com.chatpoc.domain.shared;

public record ParticipantName(String value) {
    
    public ParticipantName {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Participant name cannot be null or empty");
        }
        
        String trimmedValue = value.trim();
        
        if (trimmedValue.length() < 2) {
            throw new IllegalArgumentException("Participant name must be at least 2 characters long");
        }
        
        if (trimmedValue.length() > 50) {
            throw new IllegalArgumentException("Participant name cannot exceed 50 characters");
        }
        
        value = trimmedValue;
    }
    
    @Override
    public String toString() {
        return value;
    }
}