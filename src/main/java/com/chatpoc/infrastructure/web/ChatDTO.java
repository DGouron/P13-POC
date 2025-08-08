package com.chatpoc.infrastructure.web;

import com.chatpoc.domain.chat.Chat;
import com.chatpoc.domain.chat.Participant;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Schema(description = "Représentation d'un chat avec ses participants et messages")
public record ChatDTO(
    @Schema(description = "Identifiant unique du chat", example = "123e4567-e89b-12d3-a456-426614174000")
    String id,
    
    @Schema(description = "Nom du chat", example = "Mon Premier Chat")
    String name,
    
    @Schema(description = "Liste des participants du chat")
    Set<ParticipantDTO> participants,
    
    @Schema(description = "Liste des messages du chat")
    List<MessageDTO> messages,
    
    @Schema(description = "Date et heure de création du chat", example = "2023-12-01T10:30:00")
    LocalDateTime createdAt
) {
    public static ChatDTO fromDomain(Chat chat) {
        Set<ParticipantDTO> participantDTOs = chat.getParticipants().stream()
            .map(ParticipantDTO::fromDomain)
            .collect(java.util.stream.Collectors.toSet());
        
        List<MessageDTO> messageDTOs = chat.getMessages().stream()
            .map(MessageDTO::fromDomain)
            .toList();
        
        return new ChatDTO(
            chat.getId().toString(),
            chat.getName(),
            participantDTOs,
            messageDTOs,
            chat.getCreatedAt()
        );
    }
}