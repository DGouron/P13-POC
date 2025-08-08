package com.chatpoc.infrastructure.web;

import com.chatpoc.domain.chat.Message;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Représentation d'un message dans un chat")
public record MessageDTO(
    @Schema(description = "Identifiant unique du message", example = "456e7890-e89b-12d3-a456-426614174000")
    String id,
    
    @Schema(description = "Contenu du message", example = "Bonjour tout le monde !")
    String content,
    
    @Schema(description = "Nom de l'expéditeur", example = "Jane Smith")
    String senderName,
    
    @Schema(description = "Email de l'expéditeur", example = "jane@example.com")
    String senderEmail,
    
    @Schema(description = "Date et heure d'envoi du message", example = "2023-12-01T10:35:00")
    LocalDateTime timestamp
) {
    public static MessageDTO fromDomain(Message message) {
        return new MessageDTO(
            message.getId().toString(),
            message.getContent(),
            message.getSender().getName().value(),
            message.getSender().getEmail().value(),
            message.getTimestamp()
        );
    }
}