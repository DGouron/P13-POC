# Guide Debug WebSocket

## 🔍 Problème identifié

L'erreur `400 BAD_REQUEST` sur `/ws` vient d'un problème de configuration SockJS/STOMP.

## ✅ Configuration Frontend corrigée

### 1. Service WebSocket Angular (VERSION CORRIGÉE)

```typescript
// src/app/services/websocket.service.ts
import { Injectable } from '@angular/core';
import { Client, StompConfig } from '@stomp/stompjs';
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
    const stompConfig: StompConfig = {
      // Use SockJS for better compatibility
      webSocketFactory: () => new SockJS(this.WS_URL),
      
      // Connection callbacks
      onConnect: (frame) => {
        console.log('✅ Connected to WebSocket:', frame);
        this.connectionStatus.next(true);
      },
      
      onDisconnect: (frame) => {
        console.log('❌ Disconnected from WebSocket:', frame);
        this.connectionStatus.next(false);
      },
      
      onStompError: (frame) => {
        console.error('❌ STOMP error:', frame);
        this.connectionStatus.next(false);
      },
      
      onWebSocketError: (error) => {
        console.error('❌ WebSocket error:', error);
        this.connectionStatus.next(false);
      },
      
      // Heartbeat
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      
      // Reconnect
      reconnectDelay: 5000,
      
      // Debug
      debug: (str) => {
        console.log('🔍 STOMP Debug:', str);
      }
    };

    this.stompClient = new Client(stompConfig);
  }

  connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.stompClient.connected) {
        resolve();
        return;
      }

      const originalOnConnect = this.stompClient.onConnect;
      const originalOnStompError = this.stompClient.onStompError;

      this.stompClient.onConnect = (frame) => {
        originalOnConnect?.(frame);
        resolve();
      };

      this.stompClient.onStompError = (frame) => {
        originalOnStompError?.(frame);
        reject(new Error(`STOMP error: ${frame.headers.message}`));
      };

      this.stompClient.activate();
    });
  }

  disconnect(): void {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
  }

  subscribeToChat(chatId: string): Observable<MessageDTO> {
    return new Observable(observer => {
      if (!this.stompClient.connected) {
        observer.error(new Error('WebSocket not connected'));
        return;
      }

      const subscription = this.stompClient.subscribe(
        `/topic/chat/${chatId}`,
        (message) => {
          try {
            const messageData: MessageDTO = JSON.parse(message.body);
            console.log('📨 Received message:', messageData);
            observer.next(messageData);
          } catch (error) {
            console.error('Error parsing message:', error);
            observer.error(error);
          }
        }
      );

      // Cleanup function
      return () => {
        subscription.unsubscribe();
        console.log(`🔌 Unsubscribed from chat ${chatId}`);
      };
    });
  }

  // Test method
  sendTestMessage(): void {
    if (this.stompClient.connected) {
      this.stompClient.publish({
        destination: '/app/test',
        body: 'Hello from Angular!'
      });
    }
  }

  isConnected(): Observable<boolean> {
    return this.connectionStatus.asObservable();
  }
}
```

### 2. Utilisation dans le composant

```typescript
// src/app/components/chat.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { WebSocketService } from '../services/websocket.service';
import { ChatService } from '../services/chat.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-chat',
  template: `
    <div>
      <h2>Chat Component</h2>
      <div>WebSocket Status: {{ wsStatus }}</div>
      <button (click)="testConnection()">Test WebSocket</button>
      <button (click)="testAPI()">Test API</button>
      
      <div *ngIf="messages.length > 0">
        <h3>Messages:</h3>
        <div *ngFor="let message of messages">
          <strong>{{message.senderName}}:</strong> {{message.content}}
        </div>
      </div>
    </div>
  `
})
export class ChatComponent implements OnInit, OnDestroy {
  wsStatus = 'Disconnected';
  messages: any[] = [];
  private subscriptions: Subscription[] = [];

  constructor(
    private wsService: WebSocketService,
    private chatService: ChatService
  ) {}

  async ngOnInit() {
    // Subscribe to connection status
    this.subscriptions.push(
      this.wsService.isConnected().subscribe(connected => {
        this.wsStatus = connected ? '✅ Connected' : '❌ Disconnected';
      })
    );

    try {
      // Connect to WebSocket
      await this.wsService.connect();
      console.log('WebSocket connected successfully');
      
      // Test subscription (replace with actual chat ID)
      // this.subscribeToChat('your-chat-id-here');
      
    } catch (error) {
      console.error('Failed to connect to WebSocket:', error);
    }
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.wsService.disconnect();
  }

  testConnection() {
    this.wsService.sendTestMessage();
  }

  testAPI() {
    this.chatService.getAllChats().subscribe({
      next: (chats) => {
        console.log('✅ API working:', chats);
      },
      error: (error) => {
        console.error('❌ API error:', error);
      }
    });
  }

  subscribeToChat(chatId: string) {
    this.subscriptions.push(
      this.wsService.subscribeToChat(chatId).subscribe({
        next: (message) => {
          this.messages.push(message);
        },
        error: (error) => {
          console.error('WebSocket subscription error:', error);
        }
      })
    );
  }
}
```

## 🧪 Tests à effectuer

### 1. Test dans la console du navigateur
```javascript
// Test SockJS manuellement
const socket = new SockJS('http://localhost:8080/ws');
socket.onopen = () => console.log('✅ SockJS connected');
socket.onerror = (error) => console.error('❌ SockJS error:', error);
socket.onclose = () => console.log('🔌 SockJS closed');
```

### 2. Test endpoint info
```bash
curl http://localhost:8080/ws/info
```

### 3. Vérifier les logs backend
Redémarre le backend et regarde les logs au démarrage pour voir si WebSocket se configure correctement.

## 🔧 Si ça ne marche toujours pas

### Alternative: WebSocket natif (sans SockJS)
```typescript
// Service WebSocket alternatif (sans SockJS)
private initializeNativeWebSocket(): void {
  const ws = new WebSocket('ws://localhost:8080/ws');
  
  ws.onopen = () => {
    console.log('✅ Native WebSocket connected');
    this.connectionStatus.next(true);
  };
  
  ws.onerror = (error) => {
    console.error('❌ Native WebSocket error:', error);
    this.connectionStatus.next(false);
  };
  
  ws.onmessage = (event) => {
    console.log('📨 Received:', event.data);
  };
}
```

## 📝 Checklist de debug

- [ ] Backend redémarré après changement config
- [ ] Console Network du navigateur vérifiée
- [ ] Test `curl http://localhost:8080/ws/info`
- [ ] Logs backend vérifiés
- [ ] Frontend sur port différent (4200)
- [ ] Pas de proxy/firewall qui bloque WebSocket

La configuration corrigée devrait résoudre le problème 400 BAD_REQUEST ! 🎯