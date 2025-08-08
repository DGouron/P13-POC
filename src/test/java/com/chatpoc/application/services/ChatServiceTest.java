package com.chatpoc.application.services;

import com.chatpoc.application.commands.CreateChatCommand;
import com.chatpoc.application.commands.SendMessageCommand;
import com.chatpoc.application.queries.GetChatQuery;
import com.chatpoc.application.queries.GetRecentMessagesQuery;
import com.chatpoc.domain.chat.Chat;
import com.chatpoc.domain.chat.Message;
import com.chatpoc.domain.chat.Participant;
import com.chatpoc.domain.chat.repository.ChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    
    @Mock
    private ChatRepository chatRepository;
    
    @Mock
    private DomainEventPublisher eventPublisher;
    
    private ChatService chatService;
    
    @BeforeEach
    void setUp() {
        chatService = new ChatService(chatRepository, eventPublisher);
    }
    
    @Test
    void shouldCreateChatSuccessfully() {
        CreateChatCommand command = new CreateChatCommand("Test Chat", "John Doe", "john@example.com");
        Chat savedChat = Chat.create("Test Chat", Participant.of("John Doe", "john@example.com"));
        
        when(chatRepository.save(any(Chat.class))).thenReturn(savedChat);
        
        Chat result = chatService.createChat(command);
        
        assertNotNull(result);
        assertEquals("Test Chat", result.getName());
        assertTrue(result.hasParticipant(Participant.of("John Doe", "john@example.com")));
        
        verify(chatRepository).save(any(Chat.class));
        verify(eventPublisher, atLeastOnce()).publish(any());
    }
    
    @Test
    void shouldThrowExceptionForNullCreateChatCommand() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> chatService.createChat(null)
        );
        
        assertEquals("CreateChatCommand cannot be null", exception.getMessage());
    }
    
    @Test
    void shouldSendMessageSuccessfully() {
        UUID chatId = UUID.randomUUID();
        SendMessageCommand command = new SendMessageCommand(chatId, "Hello!", "John Doe", "john@example.com");
        Chat existingChat = Chat.create("Test Chat", Participant.of("Jane Smith", "jane@example.com"));
        
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(existingChat));
        when(chatRepository.save(any(Chat.class))).thenReturn(existingChat);
        
        Message result = chatService.sendMessage(command);
        
        assertNotNull(result);
        assertEquals("Hello!", result.getContent());
        assertEquals("John Doe", result.getSender().getName().value());
        assertEquals("john@example.com", result.getSender().getEmail().value());
        
        verify(chatRepository).findById(chatId);
        verify(chatRepository).save(existingChat);
        verify(eventPublisher, atLeastOnce()).publish(any());
    }
    
    @Test
    void shouldThrowExceptionWhenChatNotFoundForSendMessage() {
        UUID chatId = UUID.randomUUID();
        SendMessageCommand command = new SendMessageCommand(chatId, "Hello!", "John Doe", "john@example.com");
        
        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> chatService.sendMessage(command)
        );
        
        assertEquals("Chat not found with id: " + chatId, exception.getMessage());
        verify(chatRepository).findById(chatId);
        verifyNoMoreInteractions(chatRepository);
        verifyNoInteractions(eventPublisher);
    }
    
    @Test
    void shouldThrowExceptionForNullSendMessageCommand() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> chatService.sendMessage(null)
        );
        
        assertEquals("SendMessageCommand cannot be null", exception.getMessage());
    }
    
    @Test
    void shouldGetChatSuccessfully() {
        UUID chatId = UUID.randomUUID();
        GetChatQuery query = new GetChatQuery(chatId);
        Chat existingChat = Chat.create("Test Chat", Participant.of("John Doe", "john@example.com"));
        
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(existingChat));
        
        Optional<Chat> result = chatService.getChat(query);
        
        assertTrue(result.isPresent());
        assertEquals(existingChat, result.get());
        
        verify(chatRepository).findById(chatId);
    }
    
    @Test
    void shouldReturnEmptyWhenChatNotFound() {
        UUID chatId = UUID.randomUUID();
        GetChatQuery query = new GetChatQuery(chatId);
        
        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());
        
        Optional<Chat> result = chatService.getChat(query);
        
        assertFalse(result.isPresent());
        
        verify(chatRepository).findById(chatId);
    }
    
    @Test
    void shouldThrowExceptionForNullGetChatQuery() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> chatService.getChat(null)
        );
        
        assertEquals("GetChatQuery cannot be null", exception.getMessage());
    }
    
    @Test
    void shouldGetRecentMessagesSuccessfully() {
        UUID chatId = UUID.randomUUID();
        GetRecentMessagesQuery query = new GetRecentMessagesQuery(chatId, 10);
        Chat existingChat = Chat.create("Test Chat", Participant.of("John Doe", "john@example.com"));
        existingChat.sendMessage("Message 1", Participant.of("John Doe", "john@example.com"));
        existingChat.sendMessage("Message 2", Participant.of("John Doe", "john@example.com"));
        
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(existingChat));
        
        List<Message> result = chatService.getRecentMessages(query);
        
        assertEquals(2, result.size());
        assertEquals("Message 1", result.get(0).getContent());
        assertEquals("Message 2", result.get(1).getContent());
        
        verify(chatRepository).findById(chatId);
    }
    
    @Test
    void shouldThrowExceptionWhenChatNotFoundForGetRecentMessages() {
        UUID chatId = UUID.randomUUID();
        GetRecentMessagesQuery query = new GetRecentMessagesQuery(chatId, 10);
        
        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> chatService.getRecentMessages(query)
        );
        
        assertEquals("Chat not found with id: " + chatId, exception.getMessage());
        verify(chatRepository).findById(chatId);
    }
    
    @Test
    void shouldThrowExceptionForNullGetRecentMessagesQuery() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> chatService.getRecentMessages(null)
        );
        
        assertEquals("GetRecentMessagesQuery cannot be null", exception.getMessage());
    }
    
    @Test
    void shouldGetAllChatsSuccessfully() {
        List<Chat> expectedChats = List.of(
            Chat.create("Chat 1", Participant.of("User 1", "user1@example.com")),
            Chat.create("Chat 2", Participant.of("User 2", "user2@example.com"))
        );
        
        when(chatRepository.findAll()).thenReturn(expectedChats);
        
        List<Chat> result = chatService.getAllChats();
        
        assertEquals(expectedChats, result);
        verify(chatRepository).findAll();
    }
}