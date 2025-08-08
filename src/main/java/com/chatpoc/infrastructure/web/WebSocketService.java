package com.chatpoc.infrastructure.web;

import com.chatpoc.domain.chat.events.MessageSentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class WebSocketService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = Objects.requireNonNull(messagingTemplate);
    }
    
    @EventListener
    public void handleMessageSentEvent(MessageSentEvent event) {
        Objects.requireNonNull(event, "MessageSentEvent cannot be null");
        
        try {
            MessageDTO messageDTO = new MessageDTO(
                event.getMessage().getId().toString(),
                event.getMessage().getContent(),
                event.getMessage().getSender().getName().value(),
                event.getMessage().getSender().getEmail().value(),
                event.getMessage().getTimestamp()
            );
            
            String destination = "/topic/chat/" + event.getChatId();
            messagingTemplate.convertAndSend(destination, messageDTO);
            
            logger.info("Message WebSocket envoyé vers {} pour le message {}", 
                destination, event.getMessage().getId());
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi du message WebSocket pour le message {}: {}", 
                event.getMessage().getId(), e.getMessage(), e);
        }
    }
}