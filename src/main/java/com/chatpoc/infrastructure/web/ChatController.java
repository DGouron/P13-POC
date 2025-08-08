package com.chatpoc.infrastructure.web;

import com.chatpoc.application.commands.CreateChatCommand;
import com.chatpoc.application.commands.SendMessageCommand;
import com.chatpoc.application.queries.GetChatQuery;
import com.chatpoc.application.queries.GetRecentMessagesQuery;
import com.chatpoc.application.services.ChatService;
import com.chatpoc.domain.chat.Chat;
import com.chatpoc.domain.chat.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "*")
@Tag(name = "Chat Management", description = "API pour la gestion des chats et messages")
public class ChatController {
    
    private final ChatService chatService;
    
    public ChatController(ChatService chatService) {
        this.chatService = Objects.requireNonNull(chatService);
    }
    
    @PostMapping
    @Operation(
        summary = "Créer un nouveau chat",
        description = "Crée un nouveau chat avec un nom et un créateur. Le créateur devient automatiquement participant du chat."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Chat créé avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ChatDTO.class),
                examples = @ExampleObject(
                    name = "Chat créé",
                    value = """
                        {
                          "id": "123e4567-e89b-12d3-a456-426614174000",
                          "name": "Mon Premier Chat",
                          "participants": [
                            {
                              "name": "John Doe",
                              "email": "john@example.com"
                            }
                          ],
                          "messages": [],
                          "createdAt": "2023-12-01T10:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Données invalides",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erreur validation",
                    value = """
                        {
                          "message": "Chat name must be between 3 and 100 characters",
                          "timestamp": "2023-12-01T10:30:00"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<ChatDTO> createChat(
            @Valid @RequestBody 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Informations pour créer le chat",
                content = @Content(
                    examples = @ExampleObject(
                        name = "Création chat",
                        value = """
                            {
                              "chatName": "Mon Premier Chat",
                              "creatorName": "John Doe",
                              "creatorEmail": "john@example.com"
                            }
                            """
                    )
                )
            ) CreateChatRequest request) {
        CreateChatCommand command = new CreateChatCommand(
            request.chatName(),
            request.creatorName(),
            request.creatorEmail()
        );
        
        Chat chat = chatService.createChat(command);
        ChatDTO chatDTO = ChatDTO.fromDomain(chat);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(chatDTO);
    }
    
    @GetMapping("/{chatId}")
    @Operation(
        summary = "Récupérer un chat par ID",
        description = "Récupère les détails complets d'un chat incluant participants et messages"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Chat trouvé",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ChatDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Chat non trouvé"
        )
    })
    public ResponseEntity<ChatDTO> getChat(
            @Parameter(description = "ID unique du chat", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID chatId) {
        GetChatQuery query = new GetChatQuery(chatId);
        
        return chatService.getChat(query)
            .map(ChatDTO::fromDomain)
            .map(chatDTO -> ResponseEntity.ok(chatDTO))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(
        summary = "Lister tous les chats",
        description = "Récupère la liste de tous les chats existants"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Liste des chats",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ChatDTO.class, type = "array")
        )
    )
    public ResponseEntity<List<ChatDTO>> getAllChats() {
        List<Chat> chats = chatService.getAllChats();
        List<ChatDTO> chatDTOs = chats.stream()
            .map(ChatDTO::fromDomain)
            .toList();
        
        return ResponseEntity.ok(chatDTOs);
    }
    
    @PostMapping("/{chatId}/messages")
    @Operation(
        summary = "Envoyer un message",
        description = "Envoie un message dans un chat. Déclenche automatiquement les notifications WebSocket et email."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Message envoyé avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MessageDTO.class),
                examples = @ExampleObject(
                    name = "Message envoyé",
                    value = """
                        {
                          "id": "456e7890-e89b-12d3-a456-426614174000",
                          "content": "Bonjour tout le monde !",
                          "senderName": "Jane Smith",
                          "senderEmail": "jane@example.com",
                          "timestamp": "2023-12-01T10:35:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Chat non trouvé ou données invalides"
        )
    })
    public ResponseEntity<MessageDTO> sendMessage(
            @Parameter(description = "ID du chat", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID chatId,
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Contenu du message à envoyer",
                content = @Content(
                    examples = @ExampleObject(
                        name = "Envoi message",
                        value = """
                            {
                              "content": "Bonjour tout le monde !",
                              "senderName": "Jane Smith",
                              "senderEmail": "jane@example.com"
                            }
                            """
                    )
                )
            ) SendMessageRequest request) {
        
        SendMessageCommand command = new SendMessageCommand(
            chatId,
            request.content(),
            request.senderName(),
            request.senderEmail()
        );
        
        Message message = chatService.sendMessage(command);
        MessageDTO messageDTO = MessageDTO.fromDomain(message);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(messageDTO);
    }
    
    @GetMapping("/{chatId}/messages")
    @Operation(
        summary = "Récupérer les messages récents",
        description = "Récupère les N derniers messages d'un chat, triés par ordre chronologique"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Messages récupérés",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MessageDTO.class, type = "array")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Chat non trouvé"
        )
    })
    public ResponseEntity<List<MessageDTO>> getRecentMessages(
            @Parameter(description = "ID du chat", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID chatId,
            @Parameter(description = "Nombre maximum de messages à récupérer", example = "50")
            @RequestParam(defaultValue = "50") int limit) {
        
        GetRecentMessagesQuery query = new GetRecentMessagesQuery(chatId, limit);
        
        List<Message> messages = chatService.getRecentMessages(query);
        List<MessageDTO> messageDTOs = messages.stream()
            .map(MessageDTO::fromDomain)
            .toList();
        
        return ResponseEntity.ok(messageDTOs);
    }
}