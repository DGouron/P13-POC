package com.chatpoc.infrastructure.persistence;

import com.chatpoc.domain.chat.Chat;
import com.chatpoc.domain.chat.Message;
import com.chatpoc.domain.chat.Participant;
import com.chatpoc.domain.chat.repository.ChatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(ChatRepositoryImpl.class)
@ActiveProfiles("test")
class ChatRepositoryImplIntegrationTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private ChatRepository chatRepository;
    
    @Test
    void shouldSaveAndRetrieveChat() {
        Participant creator = Participant.of("John Doe", "john@example.com");
        Chat chat = Chat.create("Test Chat", creator);
        
        Chat savedChat = chatRepository.save(chat);
        entityManager.flush();
        entityManager.clear();
        
        Optional<Chat> retrievedChat = chatRepository.findById(savedChat.getId());
        
        assertTrue(retrievedChat.isPresent());
        assertEquals(savedChat.getId(), retrievedChat.get().getId());
        assertEquals("Test Chat", retrievedChat.get().getName());
        assertEquals(1, retrievedChat.get().getParticipants().size());
        assertTrue(retrievedChat.get().hasParticipant(creator));
    }
    
    @Test
    void shouldSaveChatWithMessages() {
        Participant creator = Participant.of("John Doe", "john@example.com");
        Participant otherUser = Participant.of("Jane Smith", "jane@example.com");
        Chat chat = Chat.create("Test Chat", creator);
        
        Message message1 = chat.sendMessage("Hello everyone!", creator);
        Message message2 = chat.sendMessage("Hi there!", otherUser);
        
        Chat savedChat = chatRepository.save(chat);
        entityManager.flush();
        entityManager.clear();
        
        Optional<Chat> retrievedChat = chatRepository.findById(savedChat.getId());
        
        assertTrue(retrievedChat.isPresent());
        Chat retrieved = retrievedChat.get();
        
        assertEquals(2, retrieved.getMessages().size());
        assertEquals(2, retrieved.getParticipants().size());
        
        List<Message> messages = retrieved.getMessages();
        assertTrue(messages.stream().anyMatch(m -> m.getContent().equals("Hello everyone!")));
        assertTrue(messages.stream().anyMatch(m -> m.getContent().equals("Hi there!")));
        
        assertTrue(retrieved.hasParticipant(creator));
        assertTrue(retrieved.hasParticipant(otherUser));
    }
    
    @Test
    void shouldUpdateExistingChat() {
        Participant creator = Participant.of("John Doe", "john@example.com");
        Chat chat = Chat.create("Original Chat", creator);
        
        Chat savedChat = chatRepository.save(chat);
        entityManager.flush();
        entityManager.clear();
        
        Optional<Chat> retrievedChat = chatRepository.findById(savedChat.getId());
        assertTrue(retrievedChat.isPresent());
        
        Chat chatToUpdate = retrievedChat.get();
        Participant newUser = Participant.of("Jane Smith", "jane@example.com");
        chatToUpdate.sendMessage("New message", newUser);
        
        Chat updatedChat = chatRepository.save(chatToUpdate);
        entityManager.flush();
        entityManager.clear();
        
        Optional<Chat> finalChat = chatRepository.findById(updatedChat.getId());
        assertTrue(finalChat.isPresent());
        
        Chat finalRetrieved = finalChat.get();
        assertEquals(1, finalRetrieved.getMessages().size());
        assertEquals(2, finalRetrieved.getParticipants().size());
        assertEquals("New message", finalRetrieved.getMessages().get(0).getContent());
    }
    
    @Test
    void shouldReturnEmptyWhenChatNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        
        Optional<Chat> result = chatRepository.findById(nonExistentId);
        
        assertFalse(result.isPresent());
    }
    
    @Test
    void shouldFindAllChats() {
        Participant user1 = Participant.of("User 1", "user1@example.com");
        Participant user2 = Participant.of("User 2", "user2@example.com");
        
        Chat chat1 = Chat.create("Chat 1", user1);
        Chat chat2 = Chat.create("Chat 2", user2);
        
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        entityManager.flush();
        
        List<Chat> allChats = chatRepository.findAll();
        
        assertEquals(2, allChats.size());
        assertTrue(allChats.stream().anyMatch(c -> c.getName().equals("Chat 1")));
        assertTrue(allChats.stream().anyMatch(c -> c.getName().equals("Chat 2")));
    }
    
    @Test
    void shouldDeleteChatById() {
        Participant creator = Participant.of("John Doe", "john@example.com");
        Chat chat = Chat.create("Test Chat", creator);
        
        Chat savedChat = chatRepository.save(chat);
        entityManager.flush();
        
        assertTrue(chatRepository.existsById(savedChat.getId()));
        
        chatRepository.deleteById(savedChat.getId());
        entityManager.flush();
        
        assertFalse(chatRepository.existsById(savedChat.getId()));
        Optional<Chat> deletedChat = chatRepository.findById(savedChat.getId());
        assertFalse(deletedChat.isPresent());
    }
    
    @Test
    void shouldCheckIfChatExists() {
        Participant creator = Participant.of("John Doe", "john@example.com");
        Chat chat = Chat.create("Test Chat", creator);
        
        Chat savedChat = chatRepository.save(chat);
        entityManager.flush();
        
        assertTrue(chatRepository.existsById(savedChat.getId()));
        assertFalse(chatRepository.existsById(UUID.randomUUID()));
    }
}