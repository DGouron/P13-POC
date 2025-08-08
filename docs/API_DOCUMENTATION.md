# Documentation Technique API Chat POC

## Vue d'ensemble

Cette API REST permet de gérer un système de chat temps réel avec notifications email asynchrones. Elle est développée avec Spring Boot et suit les principes du Domain-Driven Design (DDD).

## Accès à la documentation

### Swagger UI (Interface interactive)
- **URL** : `http://localhost:8080/swagger-ui/index.html`
- **Description** : Interface graphique pour tester directement les endpoints
- **Fonctionnalités** : Exécution de requêtes, visualisation des réponses, validation des schémas

### OpenAPI JSON
- **URL** : `http://localhost:8080/v3/api-docs`
- **Description** : Spécification OpenAPI 3.0 au format JSON
- **Usage** : Import dans des outils comme Postman, Insomnia, ou génération de clients

## Architecture de l'API

### Endpoints principaux

```
POST   /api/chats                     # Créer un chat
GET    /api/chats                     # Lister tous les chats
GET    /api/chats/{chatId}            # Récupérer un chat
POST   /api/chats/{chatId}/messages   # Envoyer un message
GET    /api/chats/{chatId}/messages   # Récupérer les messages récents
```

### Structure des données

#### Chat
```json
{
  "id": "uuid",
  "name": "string(3-100)",
  "participants": [
    {
      "name": "string(2-50)",
      "email": "email"
    }
  ],
  "messages": [...],
  "createdAt": "datetime"
}
```

#### Message
```json
{
  "id": "uuid",
  "content": "string(1-1000)",
  "senderName": "string(2-50)",
  "senderEmail": "email",
  "timestamp": "datetime"
}
```

## WebSocket (Temps réel)

### Configuration
- **Endpoint** : `/ws`
- **Protocole** : STOMP over SockJS
- **Format** : JSON

### Utilisation

#### Connexion JavaScript
```javascript
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // S'abonner aux messages d'un chat spécifique
    stompClient.subscribe('/topic/chat/{chatId}', function(message) {
        const messageData = JSON.parse(message.body);
        console.log('Nouveau message reçu:', messageData);
        // Traiter le message...
    });
});
```

#### Format des messages WebSocket
```json
{
  "id": "456e7890-e89b-12d3-a456-426614174000",
  "content": "Bonjour tout le monde !",
  "senderName": "Jane Smith",
  "senderEmail": "jane@example.com",
  "timestamp": "2023-12-01T10:35:00"
}
```

### Topics disponibles
- `/topic/chat/{chatId}` : Messages temps réel pour un chat spécifique

## Notifications Email

### Déclenchement automatique
Les emails sont envoyés automatiquement lors de l'envoi d'un message via l'événement `MessageSentEvent`.

### Configuration SMTP
```yaml
spring:
  mail:
    host: localhost
    port: 1025
    username: ""
    password: ""
```

### Contenu des emails
- **Destinataire** : Expéditeur du message (confirmation)
- **Sujet** : "Nouveau message dans le chat"
- **Corps** : Détails du message envoyé avec horodatage

## Règles de validation

### Chat
- **Nom** : 3-100 caractères, obligatoire
- **Créateur** : Nom et email valides obligatoires
- **Participants** : Maximum 50 par chat

### Message
- **Contenu** : 1-1000 caractères, obligatoire
- **Expéditeur** : Nom (2-50 chars) et email valide obligatoires

### Participant
- **Nom** : 2-50 caractères, obligatoire
- **Email** : Format valide, normalisé (minuscules, trim)

## Codes de réponse HTTP

### Succès
- `200 OK` : Récupération réussie
- `201 Created` : Création réussie

### Erreurs
- `400 Bad Request` : Données invalides ou chat non trouvé
- `404 Not Found` : Ressource non trouvée
- `500 Internal Server Error` : Erreur serveur

### Format des erreurs
```json
{
  "message": "Description de l'erreur",
  "timestamp": "2023-12-01T10:30:00",
  "path": "/api/chats",
  "status": 400
}
```

## CORS

L'API accepte les requêtes cross-origin de toutes les origines (`*`) pour faciliter l'intégration frontend.

```java
@CrossOrigin(origins = "*")
```

## Exemples d'utilisation

### 1. Créer un chat
```bash
curl -X POST http://localhost:8080/api/chats \
  -H "Content-Type: application/json" \
  -d '{
    "chatName": "Mon Premier Chat",
    "creatorName": "John Doe",
    "creatorEmail": "john@example.com"
  }'
```

### 2. Envoyer un message
```bash
curl -X POST http://localhost:8080/api/chats/{chatId}/messages \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Bonjour tout le monde !",
    "senderName": "Jane Smith",
    "senderEmail": "jane@example.com"
  }'
```

### 3. Récupérer les messages récents
```bash
curl http://localhost:8080/api/chats/{chatId}/messages?limit=20
```

## Intégration Frontend Angular

### Service Angular exemple
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8080/api/chats';

  constructor(private http: HttpClient) {}

  createChat(chatData: any): Observable<any> {
    return this.http.post(this.apiUrl, chatData);
  }

  sendMessage(chatId: string, messageData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/${chatId}/messages`, messageData);
  }

  getMessages(chatId: string, limit: number = 50): Observable<any> {
    return this.http.get(`${this.apiUrl}/${chatId}/messages?limit=${limit}`);
  }
}
```

### WebSocket Angular
```typescript
import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: Client;

  connect(): void {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      onConnect: () => {
        console.log('Connected to WebSocket');
      }
    });
    
    this.stompClient.activate();
  }

  subscribeToChat(chatId: string, callback: (message: any) => void): void {
    this.stompClient.subscribe(`/topic/chat/${chatId}`, (message) => {
      callback(JSON.parse(message.body));
    });
  }
}
```

## Monitoring et Health Checks

### Endpoints Actuator
- `GET /actuator/health` : État de santé de l'application
- `GET /actuator/info` : Informations sur l'application
- `GET /actuator/metrics` : Métriques de performance

## Limitations (POC)

- Pas d'authentification/autorisation
- Base de données H2 en mémoire (données perdues au redémarrage)
- Configuration email basique (pas de templates HTML)
- Pas de rate limiting
- Pas de pagination avancée pour les messages

## Évolutions possibles

1. **Sécurité** : JWT, OAuth2, validation des permissions
2. **Persistance** : PostgreSQL, Redis pour le cache
3. **Performance** : Pagination cursor-based, compression
4. **Monitoring** : Logs structurés, métriques business
5. **Fonctionnalités** : Upload fichiers, réactions, mentions