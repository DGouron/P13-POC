package com.chatpoc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI chatPocOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chat POC Backend API")
                        .description("""
                                Backend REST API pour un système de chat temps réel avec notifications email asynchrones.
                                
                                ## Fonctionnalités principales
                                - **Chat temps réel** : WebSocket pour communication instantanée
                                - **Notifications email** : Envoi automatique d'emails après chaque message
                                - **Architecture DDD** : Domain-Driven Design avec séparation claire des couches
                                - **Base de données H2** : Stockage en mémoire pour le POC
                                
                                ## WebSocket
                                - **Endpoint** : `/ws`
                                - **Topic pattern** : `/topic/chat/{chatId}`
                                - **Protocole** : STOMP over SockJS
                                
                                ## Règles métier
                                - Chat : nom 3-100 caractères, max 50 participants
                                - Message : contenu 1-1000 caractères
                                - Participant : nom 2-50 caractères, email valide
                                
                                ## CORS
                                Tous les endpoints acceptent les requêtes cross-origin pour faciliter l'intégration frontend.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Équipe Chat POC")
                                .email("dev@chatpoc.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Serveur de développement"),
                        new Server()
                                .url("https://api.chatpoc.com")
                                .description("Serveur de production (exemple)")
                ));
    }
}