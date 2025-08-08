package com.chatpoc.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Requête pour envoyer un message dans un chat")
public record SendMessageRequest(
    @Schema(description = "Contenu du message", example = "Bonjour tout le monde !", maxLength = 1000)
    @NotBlank(message = "Message content is required")
    @Size(max = 1000, message = "Message content cannot exceed 1000 characters")
    String content,
    
    @Schema(description = "Nom de l'expéditeur", example = "Jane Smith", minLength = 2, maxLength = 50)
    @NotBlank(message = "Sender name is required")
    @Size(min = 2, max = 50, message = "Sender name must be between 2 and 50 characters")
    String senderName,
    
    @Schema(description = "Email de l'expéditeur", example = "jane@example.com")
    @NotBlank(message = "Sender email is required")
    @Email(message = "Sender email must be valid")
    String senderEmail
) {}