package com.chatpoc.domain.chat;

import com.chatpoc.domain.shared.Email;
import com.chatpoc.domain.shared.ParticipantName;

import java.util.Objects;

public class Participant {
    
    private final ParticipantName name;
    private final Email email;
    
    public Participant(ParticipantName name, Email email) {
        this.name = Objects.requireNonNull(name, "Participant name cannot be null");
        this.email = Objects.requireNonNull(email, "Participant email cannot be null");
    }
    
    public static Participant of(String name, String email) {
        return new Participant(
            new ParticipantName(name),
            new Email(email)
        );
    }
    
    public ParticipantName getName() {
        return name;
    }
    
    public Email getEmail() {
        return email;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Objects.equals(email, that.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
    
    @Override
    public String toString() {
        return "Participant{" +
                "name=" + name +
                ", email=" + email +
                '}';
    }
}