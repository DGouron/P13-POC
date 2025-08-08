package com.chatpoc.infrastructure.messaging;

import com.chatpoc.domain.chat.events.MessageSentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = Objects.requireNonNull(mailSender);
    }
    
    @Async
    @EventListener
    public void handleMessageSentEvent(MessageSentEvent event) {
        Objects.requireNonNull(event, "MessageSentEvent cannot be null");
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.getMessage().getSender().getEmail().value());
            message.setSubject("Nouveau message dans le chat");
            message.setText(String.format(
                "Bonjour %s,\n\n" +
                "Votre message a été envoyé avec succès dans le chat :\n\n" +
                "\"%s\"\n\n" +
                "Envoyé le : %s\n\n" +
                "Cordialement,\nL'équipe Chat POC",
                event.getMessage().getSender().getName().value(),
                event.getMessage().getContent(),
                event.getMessage().getTimestamp()
            ));
            
            mailSender.send(message);
            
            logger.info("Email de confirmation envoyé à {} pour le message {}", 
                event.getMessage().getSender().getEmail().value(), 
                event.getMessage().getId());
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email pour le message {}: {}", 
                event.getMessage().getId(), e.getMessage(), e);
        }
    }
}