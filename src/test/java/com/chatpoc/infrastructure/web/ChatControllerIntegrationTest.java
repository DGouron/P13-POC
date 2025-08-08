package com.chatpoc.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class ChatControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void shouldCreateChatSuccessfully() throws Exception {
        CreateChatRequest request = new CreateChatRequest(
            "Integration Test Chat",
            "John Doe",
            "john@example.com"
        );
        
        mockMvc.perform(post("/api/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test Chat"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.participants", hasSize(1)))
                .andExpect(jsonPath("$.participants[0].name").value("John Doe"))
                .andExpect(jsonPath("$.participants[0].email").value("john@example.com"))
                .andExpect(jsonPath("$.messages", hasSize(0)))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }
    
    @Test
    void shouldReturnBadRequestForInvalidChatCreation() throws Exception {
        CreateChatRequest request = new CreateChatRequest(
            "AB", // Too short
            "",   // Empty name
            "invalid-email" // Invalid email
        );
        
        mockMvc.perform(post("/api/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldSendMessageToChatSuccessfully() throws Exception {
        CreateChatRequest createRequest = new CreateChatRequest(
            "Test Chat",
            "John Doe",
            "john@example.com"
        );
        
        String createResponse = mockMvc.perform(post("/api/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        ChatDTO createdChat = objectMapper.readValue(createResponse, ChatDTO.class);
        
        SendMessageRequest messageRequest = new SendMessageRequest(
            "Hello, this is a test message!",
            "Jane Smith",
            "jane@example.com"
        );
        
        mockMvc.perform(post("/api/chats/{chatId}/messages", createdChat.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Hello, this is a test message!"))
                .andExpect(jsonPath("$.senderName").value("Jane Smith"))
                .andExpect(jsonPath("$.senderEmail").value("jane@example.com"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
    
    @Test
    void shouldReturnNotFoundForNonExistentChat() throws Exception {
        SendMessageRequest messageRequest = new SendMessageRequest(
            "Hello, world!",
            "John Doe",
            "john@example.com"
        );
        
        mockMvc.perform(post("/api/chats/{chatId}/messages", "00000000-0000-0000-0000-000000000000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldGetChatById() throws Exception {
        CreateChatRequest createRequest = new CreateChatRequest(
            "Test Chat",
            "John Doe",
            "john@example.com"
        );
        
        String createResponse = mockMvc.perform(post("/api/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        ChatDTO createdChat = objectMapper.readValue(createResponse, ChatDTO.class);
        
        mockMvc.perform(get("/api/chats/{chatId}", createdChat.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdChat.id()))
                .andExpect(jsonPath("$.name").value("Test Chat"))
                .andExpect(jsonPath("$.participants", hasSize(1)));
    }
    
    @Test
    void shouldReturnNotFoundForNonExistentChatById() throws Exception {
        mockMvc.perform(get("/api/chats/{chatId}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void shouldGetAllChats() throws Exception {
        CreateChatRequest request1 = new CreateChatRequest("Chat 1", "User 1", "user1@example.com");
        CreateChatRequest request2 = new CreateChatRequest("Chat 2", "User 2", "user2@example.com");
        
        mockMvc.perform(post("/api/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(post("/api/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(get("/api/chats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Chat 1", "Chat 2")));
    }
    
    @Test
    void shouldGetRecentMessages() throws Exception {
        CreateChatRequest createRequest = new CreateChatRequest(
            "Test Chat",
            "John Doe",
            "john@example.com"
        );
        
        String createResponse = mockMvc.perform(post("/api/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        ChatDTO createdChat = objectMapper.readValue(createResponse, ChatDTO.class);
        
        SendMessageRequest message1 = new SendMessageRequest("Message 1", "John Doe", "john@example.com");
        SendMessageRequest message2 = new SendMessageRequest("Message 2", "Jane Smith", "jane@example.com");
        
        mockMvc.perform(post("/api/chats/{chatId}/messages", createdChat.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message1)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(post("/api/chats/{chatId}/messages", createdChat.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message2)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(get("/api/chats/{chatId}/messages", createdChat.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].content", containsInAnyOrder("Message 1", "Message 2")));
    }
}