package com.chatpoc.infrastructure.web;

import com.chatpoc.domain.chat.Participant;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Représentation d'un participant dans un chat")
public record ParticipantDTO(
    @Schema(description = "Nom du participant", example = "John Doe")
    String name,
    
    @Schema(description = "Email du participant", example = "john@example.com")
    String email
) {
    public static ParticipantDTO fromDomain(Participant participant) {
        return new ParticipantDTO(
            participant.getName().value(),
            participant.getEmail().value()
        );
    }
}