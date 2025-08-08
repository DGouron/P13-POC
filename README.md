# Chat POC Backend

Un backend de chat en temps réel et asynchrone développé avec Spring Boot et Domain-Driven Design (DDD).

## Fonctionnalités

- ✅ Chat en temps réel via WebSocket
- ✅ Notifications email asynchrones
- ✅ Architecture DDD (Domain-Driven Design)
- ✅ API REST complète
- ✅ Base de données H2 en mémoire
- ✅ Tests unitaires et d'intégration (>90% couverture)
- ✅ Pas d'authentification (POC)

## Architecture

### Structure DDD

```
src/main/java/com/chatpoc/
├── domain/           # Couche domaine
│   ├── chat/         # Agrégat Chat
│   └── shared/       # Value Objects partagés
├── application/      # Couche application
│   ├── commands/     # CQRS Commands
│   ├── queries/      # CQRS Queries
│   └── services/     # Services applicatifs
├── infrastructure/   # Couche infrastructure
│   ├── persistence/  # JPA/Repositories
│   ├── messaging/    # Email/WebSocket
│   └── web/          # Controllers REST
└── config/          # Configuration
```

### Modèle de domaine

- **Chat** (Aggregate Root) : Gère les conversations et les participants
- **Message** : Représente un message dans un chat
- **Participant** : Utilisateur participant à un chat
- **Email/ParticipantName** : Value Objects avec validation

## Démarrage rapide

### Prérequis

- Java 17+
- Maven 3.6+

### Installation et lancement

```bash
# Cloner et naviguer dans le projet
cd P13-POC

# Installer les dépendances
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

L'application sera accessible sur `http://localhost:8080`

### Accès aux interfaces

- **API Swagger UI** : `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON** : `http://localhost:8080/v3/api-docs`
- **Console H2** : `http://localhost:8080/h2-console`
  - URL : `jdbc:h2:mem:chatdb`
  - User : `sa`
  - Password : *(vide)*

## API REST

### Créer un chat

```bash
POST /api/chats
Content-Type: application/json

{
  "chatName": "Mon Premier Chat",
  "creatorName": "John Doe", 
  "creatorEmail": "john@example.com"
}
```

### Envoyer un message

```bash
POST /api/chats/{chatId}/messages
Content-Type: application/json

{
  "content": "Bonjour tout le monde !",
  "senderName": "Jane Smith",
  "senderEmail": "jane@example.com"
}
```

### Récupérer un chat

```bash
GET /api/chats/{chatId}
```

### Lister tous les chats

```bash
GET /api/chats
```

### Messages récents d'un chat

```bash
GET /api/chats/{chatId}/messages?limit=50
```

## WebSocket (Temps réel)

### Connexion WebSocket

```javascript
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    // S'abonner aux messages d'un chat
    stompClient.subscribe('/topic/chat/{chatId}', function(message) {
        const messageData = JSON.parse(message.body);
        console.log('Nouveau message:', messageData);
    });
});
```

## Email asynchrone

Les emails de confirmation sont envoyés automatiquement après chaque message.

**Configuration SMTP** (dans `application.yml`) :
```yaml
spring:
  mail:
    host: localhost
    port: 1025  # MailHog pour dev
```

Pour tester avec MailHog :
```bash
docker run -p 1025:1025 -p 8025:8025 mailhog/mailhog
```

Interface web : `http://localhost:8025`

## Tests

### Exécuter tous les tests

```bash
# Tests unitaires
mvn test

# Tests d'intégration
mvn integration-test

# Tous les tests avec couverture
mvn clean test jacoco:report
```

### Rapport de couverture

Après exécution des tests : `target/site/jacoco/index.html`

### Structure des tests

- **Tests unitaires** : Domain model et règles métier
- **Tests d'intégration** : Repository, Services, Controllers
- **Tests end-to-end** : API REST complète

## Règles métier implémentées

### Chat
- Nom entre 3 et 100 caractères
- Maximum 50 participants par chat
- Création automatique d'événements domaine

### Message  
- Contenu entre 1 et 1000 caractères
- Horodatage automatique
- Envoi d'événements pour notifications

### Participant
- Nom entre 2 et 50 caractères
- Email valide et normalisé
- Unicité par email dans un chat

### Email
- Validation format RFC
- Normalisation (minuscules, trim)
- Value Object immutable

## Événements du domaine

- **ChatCreatedEvent** : Déclenché à la création d'un chat
- **MessageSentEvent** : Déclenché à l'envoi d'un message

Ces événements triggent automatiquement :
- Notifications WebSocket temps réel
- Envoi d'emails asynchrones

## Technologies utilisées

- **Spring Boot 3.2** : Framework principal
- **Spring Data JPA** : Persistance
- **Spring WebSocket** : Temps réel
- **Spring Mail** : Emails asynchrones
- **H2 Database** : Base de données en mémoire
- **JUnit 5** : Tests unitaires
- **Mockito** : Mocking pour tests
- **TestContainers** : Tests d'intégration
- **Jacoco** : Couverture de code

## Développement

### Hot reload

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

### Profils

- **default** : Développement avec logs détaillés
- **test** : Tests avec configuration allégée

### Monitoring

Endpoints actuator disponibles :
- `GET /actuator/health` : Santé de l'application  
- `GET /actuator/info` : Informations sur l'application
- `GET /actuator/metrics` : Métriques

## Limitations (POC)

- Pas d'authentification/autorisation
- Base de données en mémoire uniquement
- Configuration email basique
- Pas de pagination avancée
- Pas de rate limiting

## Évolutions possibles

- [ ] Authentification JWT
- [ ] Base de données PostgreSQL
- [ ] Pagination et filtres avancés  
- [ ] Upload de fichiers
- [ ] Notifications push
- [ ] Rate limiting
- [ ] Cache Redis
- [ ] Monitoring avancé