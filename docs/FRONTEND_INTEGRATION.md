# Guide d'intégration Frontend

## 🔧 Problème de connexion résolu

Le problème que tu rencontres vient du fait que ton frontend essaie d'accéder à `/chat` au lieu des endpoints API.

## ✅ URLs correctes à utiliser

### API REST
```typescript
// ❌ FAUX
const API_URL = 'http://localhost:8080/chat';

// ✅ CORRECT
const API_URL = 'http://localhost:8080/api/chats';
```

### WebSocket
```typescript
// ✅ CORRECT
const WS_URL = 'http://localhost:8080/ws';
```

## 🚀 Configuration Angular

### 1. Service HTTP
```typescript
// src/app/services/chat.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private readonly API_URL = 'http://localhost:8080/api/chats';

  constructor(private http: HttpClient) {}

  // Créer un chat
  createChat(data: CreateChatRequest): Observable<ChatDTO> {
    return this.http.post<ChatDTO>(this.API_URL, data);
  }

  // Récupérer tous les chats
  getAllChats(): Observable<ChatDTO[]> {
    return this.http.get<ChatDTO[]>(this.API_URL);
  }

  // Récupérer un chat
  getChat(chatId: string): Observable<ChatDTO> {
    return this.http.get<ChatDTO>(`${this.API_URL}/${chatId}`);
  }

  // Envoyer un message
  sendMessage(chatId: string, data: SendMessageRequest): Observable<MessageDTO> {
    return this.http.post<MessageDTO>(`${this.API_URL}/${chatId}/messages`, data);
  }

  // Récupérer les messages
  getMessages(chatId: string, limit: number = 50): Observable<MessageDTO[]> {
    return this.http.get<MessageDTO[]>(`${this.API_URL}/${chatId}/messages?limit=${limit}`);
  }
}

// Types
export interface CreateChatRequest {
  chatName: string;
  creatorName: string;
  creatorEmail: string;
}

export interface SendMessageRequest {
  content: string;
  senderName: string;
  senderEmail: string;
}

export interface ChatDTO {
  id: string;
  name: string;
  participants: ParticipantDTO[];
  messages: MessageDTO[];
  createdAt: string;
}

export interface MessageDTO {
  id: string;
  content: string;
  senderName: string;
  senderEmail: string;
  timestamp: string;
}

export interface ParticipantDTO {
  name: string;
  email: string;
}
```

### 2. Service WebSocket
```typescript
// src/app/services/websocket.service.ts
import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { BehaviorSubject, Observable } from 'rxjs';
import { MessageDTO } from './chat.service';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: Client;
  private readonly WS_URL = 'http://localhost:8080/ws';
  private connectionStatus = new BehaviorSubject<boolean>(false);
  
  constructor() {
    this.initializeWebSocket();
  }

  private initializeWebSocket(): void {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(this.WS_URL),
      onConnect: () => {
        console.log('✅ Connected to WebSocket');
        this.connectionStatus.next(true);
      },
      onDisconnect: () => {
        console.log('❌ Disconnected from WebSocket');
        this.connectionStatus.next(false);
      },
      onStompError: (frame) => {
        console.error('❌ STOMP error:', frame);
      }
    });
  }

  connect(): void {
    this.stompClient.activate();
  }

  disconnect(): void {
    this.stompClient.deactivate();
  }

  subscribeToChat(chatId: string): Observable<MessageDTO> {
    return new Observable(observer => {
      const subscription = this.stompClient.subscribe(
        `/topic/chat/${chatId}`,
        (message) => {
          const messageData: MessageDTO = JSON.parse(message.body);
          observer.next(messageData);
        }
      );

      // Cleanup function
      return () => subscription.unsubscribe();
    });
  }

  isConnected(): Observable<boolean> {
    return this.connectionStatus.asObservable();
  }
}
```

### 3. Installation des dépendances
```bash
npm install @stomp/stompjs sockjs-client
npm install --save-dev @types/sockjs-client
```

### 4. Module HTTP
```typescript
// src/app/app.module.ts
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';

@NgModule({
  imports: [
    BrowserModule,
    HttpClientModule, // ✅ Important pour les appels HTTP
    // ... autres modules
  ],
  // ...
})
export class AppModule { }
```

## 🧪 Test de connexion

### 1. Test simple dans le composant
```typescript
// src/app/app.component.ts
import { Component, OnInit } from '@angular/core';
import { ChatService } from './services/chat.service';
import { WebSocketService } from './services/websocket.service';

@Component({
  selector: 'app-root',
  template: `
    <div>
      <h1>Chat POC Frontend</h1>
      <button (click)="testConnection()">Test API</button>
      <button (click)="testWebSocket()">Test WebSocket</button>
      <div *ngIf="apiStatus">API Status: {{apiStatus}}</div>
      <div *ngIf="wsStatus">WebSocket Status: {{wsStatus}}</div>
    </div>
  `
})
export class AppComponent implements OnInit {
  apiStatus = '';
  wsStatus = '';

  constructor(
    private chatService: ChatService,
    private wsService: WebSocketService
  ) {}

  ngOnInit() {
    // Connecter WebSocket au démarrage
    this.wsService.connect();
    
    // Surveiller le statut de connexion
    this.wsService.isConnected().subscribe(connected => {
      this.wsStatus = connected ? '✅ Connecté' : '❌ Déconnecté';
    });
  }

  testConnection() {
    this.chatService.getAllChats().subscribe({
      next: (chats) => {
        this.apiStatus = `✅ API OK - ${chats.length} chats trouvés`;
        console.log('Chats:', chats);
      },
      error: (error) => {
        this.apiStatus = `❌ API ERROR: ${error.message}`;
        console.error('Erreur API:', error);
      }
    });
  }

  testWebSocket() {
    console.log('Test WebSocket - vérifiez la console');
  }
}
```

## 🔍 Débogage

### 1. Vérifier les URLs dans la console du navigateur
```javascript
// Dans la console du navigateur
fetch('http://localhost:8080/api/chats')
  .then(r => r.json())
  .then(console.log);
```

### 2. Vérifier CORS
```javascript
// Si tu as des erreurs CORS, regarde dans la console Network
// Les URLs autorisées sont : localhost:4200, localhost:3000, localhost:8081
```

### 3. Test Postman/cURL
```bash
# Test API
curl http://localhost:8080/api/chats

# Créer un chat
curl -X POST http://localhost:8080/api/chats \
  -H "Content-Type: application/json" \
  -d '{"chatName":"Test","creatorName":"John","creatorEmail":"john@test.com"}'
```

## ⚠️ Points importants

1. **Port différent** : Ton Angular doit tourner sur un port différent (ex: 4200)
2. **URLs correctes** : Utilise `/api/chats` et non `/chat`
3. **CORS configuré** : Pour les ports 4200, 3000, 8081
4. **WebSocket** : Endpoint `/ws` avec STOMP over SockJS

## 🆘 Si ça ne marche toujours pas

1. Redémarre le backend : `mvn spring-boot:run`
2. Vérifie que ton Angular est sur `http://localhost:4200`
3. Ouvre la console Network du navigateur pour voir les erreurs
4. Teste d'abord avec Swagger UI : `http://localhost:8080/swagger-ui.html`