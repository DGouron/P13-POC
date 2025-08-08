package com.chatpoc.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Requête pour créer un nouveau chat")
public record CreateChatRequest(
    @Schema(description = "Nom du chat", example = "Mon Premier Chat", minLength = 3, maxLength = 100)
    @NotBlank(message = "Chat name is required")
    @Size(min = 3, max = 100, message = "Chat name must be between 3 and 100 characters")
    String chatName,
    
    @Schema(description = "Nom du créateur du chat", example = "John Doe", minLength = 2, maxLength = 50)
    @NotBlank(message = "Creator name is required")
    @Size(min = 2, max = 50, message = "Creator name must be between 2 and 50 characters")
    String creatorName,
    
    @Schema(description = "Email du créateur du chat", example = "john@example.com")
    @NotBlank(message = "Creator email is required")
    @Email(message = "Creator email must be valid")
    String creatorEmail
) {}