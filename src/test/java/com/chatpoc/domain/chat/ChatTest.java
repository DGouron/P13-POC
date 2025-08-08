package com.chatpoc.domain.chat;

import com.chatpoc.domain.chat.events.ChatCreatedEvent;
import com.chatpoc.domain.chat.events.MessageSentEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ChatTest {
    
    private final Participant creator = Participant.of("John Doe", "john@example.com");
    private final Participant otherParticipant = Participant.of("Jane Smith", "jane@example.com");
    
    @Test
    void shouldCreateChatWithValidNameAndCreator() {
        String chatName = "Test Chat";
        
        Chat chat = Chat.create(chatName, creator);
        
        assertNotNull(chat.getId());
        assertEquals(chatName, chat.getName());
        assertTrue(chat.getParticipants().contains(creator));
        assertEquals(1, chat.getParticipants().size());
        assertTrue(chat.getMessages().isEmpty());
        assertNotNull(chat.getCreatedAt());
        assertTrue(chat.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
    
    @Test
    void shouldCreateChatCreatedEventWhenChatIsCreated() {
        String chatName = "Test Chat";
        
        Chat chat = Chat.create(chatName, creator);
        
        List<Object> domainEvents = chat.getDomainEvents();
        assertEquals(1, domainEvents.size());
        assertTrue(domainEvents.get(0) instanceof ChatCreatedEvent);
        
        ChatCreatedEvent event = (ChatCreatedEvent) domainEvents.get(0);
        assertEquals(chat.getId(), event.getChatId());
        assertEquals(chatName, event.getChatName());
        assertEquals(creator, event.getCreator());
    }
    
    @Test
    void shouldReconstructChatWithAllFields() {
        UUID id = UUID.randomUUID();
        String name = "Test Chat";
        Set<Participant> participants = Set.of(creator, otherParticipant);
        List<Message> messages = List.of(Message.create("Hello", creator));
        LocalDateTime createdAt = LocalDateTime.now();
        
        Chat chat = Chat.reconstruct(id, name, participants, messages, createdAt);
        
        assertEquals(id, chat.getId());
        assertEquals(name, chat.getName());
        assertEquals(participants, chat.getParticipants());
        assertEquals(messages.size(), chat.getMessages().size());
        assertEquals(createdAt, chat.getCreatedAt());
        assertTrue(chat.getDomainEvents().isEmpty());
    }
    
    @Test
    void shouldSendMessageFromExistingParticipant() {
        Chat chat = Chat.create("Test Chat", creator);
        String messageContent = "Hello, everyone!";
        
        Message message = chat.sendMessage(messageContent, creator);
        
        assertNotNull(message);
        assertEquals(messageContent, message.getContent());
        assertEquals(creator, message.getSender());
        assertTrue(chat.getMessages().contains(message));
        assertEquals(1, chat.getMessages().size());
    }
    
    @Test
    void shouldAddParticipantWhenSendingMessageFromNewUser() {
        Chat chat = Chat.create("Test Chat", creator);
        String messageContent = "Hello, I'm new!";
        
        Message message = chat.sendMessage(messageContent, otherParticipant);
        
        assertNotNull(message);
        assertEquals(messageContent, message.getContent());
        assertEquals(otherParticipant, message.getSender());
        assertTrue(chat.getParticipants().contains(otherParticipant));
        assertEquals(2, chat.getParticipants().size());
    }
    
    @Test
    void shouldCreateMessageSentEventWhenMessageIsSent() {
        Chat chat = Chat.create("Test Chat", creator);
        chat.clearDomainEvents();
        String messageContent = "Hello, everyone!";
        
        Message message = chat.sendMessage(messageContent, creator);
        
        List<Object> domainEvents = chat.getDomainEvents();
        assertEquals(1, domainEvents.size());
        assertTrue(domainEvents.get(0) instanceof MessageSentEvent);
        
        MessageSentEvent event = (MessageSentEvent) domainEvents.get(0);
        assertEquals(chat.getId(), event.getChatId());
        assertEquals(message, event.getMessage());
    }
    
    @Test
    void shouldAddParticipantToChat() {
        Chat chat = Chat.create("Test Chat", creator);
        
        chat.addParticipant(otherParticipant);
        
        assertTrue(chat.getParticipants().contains(otherParticipant));
        assertEquals(2, chat.getParticipants().size());
    }
    
    @Test
    void shouldNotAddDuplicateParticipant() {
        Chat chat = Chat.create("Test Chat", creator);
        
        chat.addParticipant(creator);
        
        assertEquals(1, chat.getParticipants().size());
    }
    
    @Test
    void shouldThrowExceptionWhenAddingMoreThanFiftyParticipants() {
        Chat chat = Chat.create("Test Chat", creator);
        
        for (int i = 1; i < 50; i++) {
            chat.addParticipant(Participant.of("User" + i, "user" + i + "@example.com"));
        }
        
        Participant fiftiethParticipant = Participant.of("User50", "user50@example.com");
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> chat.addParticipant(fiftiethParticipant)
        );
        
        assertEquals("Chat cannot have more than 50 participants", exception.getMessage());
    }
    
    @Test
    void shouldGetRecentMessagesWithLimit() {
        Chat chat = Chat.create("Test Chat", creator);
        
        for (int i = 1; i <= 10; i++) {
            chat.sendMessage("Message " + i, creator);
        }
        
        List<Message> recentMessages = chat.getRecentMessages(5);
        
        assertEquals(5, recentMessages.size());
        assertEquals("Message 6", recentMessages.get(0).getContent());
        assertEquals("Message 10", recentMessages.get(4).getContent());
    }
    
    @Test
    void shouldGetAllMessagesWhenLimitExceedsMessageCount() {
        Chat chat = Chat.create("Test Chat", creator);
        chat.sendMessage("Message 1", creator);
        chat.sendMessage("Message 2", creator);
        
        List<Message> recentMessages = chat.getRecentMessages(10);
        
        assertEquals(2, recentMessages.size());
    }
    
    @Test
    void shouldThrowExceptionForNonPositiveLimit() {
        Chat chat = Chat.create("Test Chat", creator);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> chat.getRecentMessages(0)
        );
        
        assertEquals("Limit must be positive", exception.getMessage());
    }
    
    @Test
    void shouldCheckIfParticipantExists() {
        Chat chat = Chat.create("Test Chat", creator);
        
        assertTrue(chat.hasParticipant(creator));
        assertFalse(chat.hasParticipant(otherParticipant));
        
        chat.addParticipant(otherParticipant);
        assertTrue(chat.hasParticipant(otherParticipant));
    }
    
    @Test
    void shouldClearDomainEvents() {
        Chat chat = Chat.create("Test Chat", creator);
        assertFalse(chat.getDomainEvents().isEmpty());
        
        chat.clearDomainEvents();
        
        assertTrue(chat.getDomainEvents().isEmpty());
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldThrowExceptionForInvalidChatName(String invalidName) {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Chat.create(invalidName, creator)
        );
        
        assertEquals("Chat name cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionForTooShortChatName() {
        String shortName = "AB";
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Chat.create(shortName, creator)
        );
        
        assertEquals("Chat name must be at least 3 characters long", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionForTooLongChatName() {
        String longName = "A".repeat(101);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Chat.create(longName, creator)
        );
        
        assertEquals("Chat name cannot exceed 100 characters", exception.getMessage());
    }
    
    @Test
    void shouldAcceptChatNameWithExactlyThreeCharacters() {
        String threCharName = "ABC";
        
        Chat chat = Chat.create(threCharName, creator);
        
        assertEquals(threCharName, chat.getName());
    }
    
    @Test
    void shouldAcceptChatNameWithExactlyHundredCharacters() {
        String hundredCharName = "A".repeat(100);
        
        Chat chat = Chat.create(hundredCharName, creator);
        
        assertEquals(hundredCharName, chat.getName());
    }
    
    @Test
    void shouldImplementEqualityBasedOnId() {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        
        Chat chat1 = Chat.reconstruct(id, "Chat 1", Set.of(creator), List.of(), createdAt);
        Chat chat2 = Chat.reconstruct(id, "Chat 2", Set.of(otherParticipant), List.of(), createdAt);
        
        assertEquals(chat1, chat2);
        assertEquals(chat1.hashCode(), chat2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualIfIdsDiffer() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        
        Chat chat1 = Chat.reconstruct(id1, "Chat", Set.of(creator), List.of(), createdAt);
        Chat chat2 = Chat.reconstruct(id2, "Chat", Set.of(creator), List.of(), createdAt);
        
        assertNotEquals(chat1, chat2);
    }
}