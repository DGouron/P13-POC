package com.chatpoc.infrastructure.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.Objects;

@Controller
public class WebSocketController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = Objects.requireNonNull(messagingTemplate);
    }
    
    @GetMapping("/ws/info")
    @ResponseBody
    public String getWebSocketInfo() {
        return """
            {
              "status": "WebSocket endpoint active",
              "endpoint": "/ws",
              "topics": ["/topic/chat/{chatId}"],
              "timestamp": "%s"
            }
            """.formatted(LocalDateTime.now());
    }
    
    @MessageMapping("/test")
    @SendTo("/topic/test")
    public String testMessage(String message) {
        logger.info("Received test message: {}", message);
        return "Echo: " + message + " at " + LocalDateTime.now();
    }
    
    public void sendTestMessage() {
        messagingTemplate.convertAndSend("/topic/test", 
            "Test message from server at " + LocalDateTime.now());
    }
}