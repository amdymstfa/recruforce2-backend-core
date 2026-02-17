# RecruForce2 - Backend Core (Spring Boot)

Ce dépôt contient le **backend API REST** de RecruForce2, développé avec **Java Spring Boot**.  
Il constitue le cœur métier de la plateforme : gestion des utilisateurs, offres d'emploi, candidatures, entretiens, notifications et intégration avec le modèle IA.

---

## 🛠️ Stack Technologique

| Composant | Technologie | Version |
|-----------|-------------|---------|
| Langage | Java | 17+ |
| Framework | Spring Boot | 3.x |
| Sécurité | Spring Security + JWT | — |
| Base de données principale | PostgreSQL | 15+ |
| Base de données documents | MongoDB | 6+ |
| Migrations BDD | Liquibase | — |
| Orchestration IA | N8N | — |
| Containerisation | Docker + Docker Compose | — |
| Documentation API | OpenAPI / Swagger | 3.0 |

---

## 📐 Architecture Globale

RecruForce2 suit une architecture **en couches (Layered Architecture)** avec une séparation claire des responsabilités, combinée à une **base de données hybride** PostgreSQL + MongoDB.

```
recruforce2/
├── src/main/java/com/backend_core/recruforce2/
│   ├── config/          # Configuration (Security, JWT, CORS, MongoDB, OpenAPI)
│   ├── controller/      # Couche présentation - endpoints REST
│   ├── domain/
│   │   ├── entities/    # Entités JPA (PostgreSQL)
│   │   └── enums/       # Énumérations métier
│   ├── dto/
│   │   ├── request/     # Objets de requête (entrée API)
│   │   └── response/    # Objets de réponse (sortie API)
│   ├── exception/       # Gestion globale des exceptions
│   ├── mongo/
│   │   ├── document/    # Documents MongoDB (CVs parsés, logs IA)
│   │   └── repository/  # Repositories MongoDB
│   ├── repository/      # Repositories JPA (PostgreSQL)
│   ├── service/         # Logique métier
│   └── util/            # Utilitaires (JWT, helpers)
└── src/main/resources/
    ├── application.yml              # Config principale
    ├── application-dev.yml          # Config développement
    ├── application-prod.yml         # Config production
    ├── db/changelog/                # Migrations Liquibase
    └── templates/email/             # Templates emails
```

---

## 🗄️ Base de Données Hybride

RecruForce2 utilise **deux bases de données complémentaires** selon la nature des données.

### PostgreSQL — Données Structurées
Stocke toutes les données relationnelles et transactionnelles de l'application.

| Table | Description |
|-------|-------------|
| `users` | Comptes utilisateurs (Admin, Recruteur, Manager) |
| `skills` | Référentiel de compétences |
| `candidate_profiles` | Profils candidats structurés |
| `job_offers` | Offres d'emploi |
| `applications` | Candidatures associées offre/candidat |
| `interviews` | Entretiens planifiés (Soft Skills / Hard Skills) |
| `interview_slots` | Créneaux horaires disponibles |
| `interview_feedbacks` | Feedbacks post-entretien |
| `recruiter_availability` | Disponibilités des recruteurs/managers |
| `notifications` | Alertes et notifications système |
| `notification_preferences` | Préférences de notification par utilisateur |
| `prediction_results` | Résultats du module de prédiction IA |

### MongoDB — Données Non Structurées
Stocke les données semi-structurées issues du pipeline IA.

| Collection | Description |
|------------|-------------|
| `parsed_cvs` | CVs parsés par le modèle Python (JSON riche) |
| `ai_logs` | Logs des appels IA (matching, parsing, prédiction) |

---

## 📦 Modules Fonctionnels

### 🔐 Authentification & Sécurité
- Login / Register avec hachage bcrypt
- Authentification stateless via **JWT**
- Gestion des rôles : `ADMIN`, `RECRUTEUR`, `MANAGER`
- Filtre JWT sur chaque requête (`JwtAuthenticationFilter`)
- Protection CORS configurable

**Endpoints :** `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/refresh`

---

### 📋 Gestion des Offres d'Emploi
- CRUD complet des offres
- Statuts : `DRAFT`, `PUBLISHED`, `ARCHIVED`, `CLOSED`
- Types de contrat : `CDI`, `CDD`, `FREELANCE`, `STAGE`, `ALTERNANCE`
- Publication externe (LinkedIn via N8N)
- Tableau de bord des offres actives

**Endpoints :** `GET/POST /api/job-offers`, `GET/PUT/DELETE /api/job-offers/{id}`

---

### 👤 Gestion des Candidats
- Profil candidat détaillé (expériences, formations, compétences)
- Upload et parsing automatique du CV (PDF/DOCX)
- Consultation du profil structuré issu du parsing IA
- Score de matching avec une offre

**Endpoints :** `GET/POST /api/candidates`, `GET /api/candidates/{id}`, `POST /api/candidates/{id}/cv`

---

### 📨 Gestion des Candidatures
- Réception via formulaire web ou email (N8N)
- Association candidat ↔ offre
- Suivi des statuts : `RECEIVED`, `REVIEWED`, `QUALIFIED`, `REJECTED`, `INTERVIEW_SCHEDULED`, `HIRED`
- Score de matching automatique via le modèle IA Python
- Communication automatisée (accusé réception, notifications)

**Endpoints :** `GET/POST /api/applications`, `PUT /api/applications/{id}/status`

---

### 🗓️ Planification des Entretiens
- Deux phases : **Soft Skills** (compétences comportementales) puis **Hard Skills** (techniques)
- Gestion des créneaux de disponibilité (recruteurs/managers)
- Proposition et confirmation automatique de créneaux
- Envoi de confirmations et rappels par email

**Endpoints :** `GET/POST /api/interviews`, `POST /api/interviews/{id}/schedule`, `GET /api/interviews/slots`

---

### 📝 Feedback d'Entretiens
- Formulaires de feedback dédiés par phase (Soft / Hard Skills)
- Score global par candidat
- Historique complet des entretiens
- Synthèse pour aide à la décision finale

**Endpoints :** `POST /api/interviews/{id}/feedback`, `GET /api/interviews/{id}/feedback`

---

### 🔔 Notifications & Alertes
- Notifications en temps réel et par email
- Types : nouvelle candidature, action requise, entretien à venir, délai critique
- Préférences de notification par utilisateur

**Endpoints :** `GET /api/notifications`, `PUT /api/notifications/{id}/read`, `GET/PUT /api/notifications/preferences`

---

### 🤖 Module de Prédiction IA
- Appel au microservice Python (`recruforce2-ai-model`)
- Score de matching poste/CV
- Prédiction de réussite et d'intégration du candidat
- Stockage des résultats en PostgreSQL
- Logs des appels IA en MongoDB

**Endpoints :** `POST /api/predictions/match`, `GET /api/predictions/{applicationId}`

---

### 🛡️ Administration
- Gestion des utilisateurs et des rôles
- Paramètres généraux de la plateforme
- Tableau de bord global avec statistiques

**Endpoints :** `GET/POST/PUT/DELETE /api/admin/users`, `GET /api/admin/stats`

---

## 🔗 Intégrations Externes

### N8N — Orchestration des Workflows IA
Le fichier `n8n/workflows/cv-parsing-workflow.json` définit le workflow d'automatisation :
1. Réception d'un email avec CV en pièce jointe
2. Extraction et transmission au microservice Python
3. Création du profil candidat via l'API backend
4. Envoi de l'accusé de réception au candidat
5. Notification au recruteur

### Microservice IA Python (`recruforce2-ai-model`)
- **Parsing CV** : extraction structurée des informations (NLP, spaCy)
- **Matching Score** : comparaison profil/offre (scikit-learn)
- **Prédiction** : probabilité de réussite du candidat

Communication via appels HTTP REST entre le backend Spring Boot et le microservice Python.

---

## ⚙️ Installation & Lancement

### Prérequis
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 15+ (ou via Docker)
- MongoDB 6+ (ou via Docker)

### 1. Cloner le dépôt
```bash
git clone <repo-url>
cd recruforce2
```

### 2. Lancer avec Docker Compose (recommandé)
```bash
docker-compose up -d
```
Lance automatiquement : PostgreSQL, MongoDB, N8N et le backend Spring Boot.

### 3. Lancer en local (développement)
```bash
# Configurer les variables d'environnement
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml

# Lancer l'application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

L'API sera disponible sur : `http://localhost:8080`  
La documentation Swagger sur : `http://localhost:8080/swagger-ui.html`

---

## 🌍 Configuration des Environnements

| Fichier | Environnement | Usage |
|---------|--------------|-------|
| `application.yml` | Commun | Config partagée (port, Liquibase, JWT) |
| `application-dev.yml` | Développement | BDD locale, logs détaillés, CORS ouvert |
| `application-prod.yml` | Production | BDD sécurisée, logs réduits, HTTPS |
| `application-test.yml` | Tests | BDD en mémoire (H2), mocks |

### Variables d'environnement clés
```yaml
# PostgreSQL
POSTGRES_URL: jdbc:postgresql://localhost:5432/recruforce2
POSTGRES_USER: recruforce2_user
POSTGRES_PASSWORD: ****

# MongoDB
MONGO_URI: mongodb://localhost:27017/recruforce2

# JWT
JWT_SECRET: <secret-key-256-bits>
JWT_EXPIRATION: 86400000  # 24h en ms

# Microservice IA
AI_MODEL_URL: http://localhost:8000

# Email (SMTP)
MAIL_HOST: smtp.example.com
MAIL_PORT: 587
MAIL_USERNAME: noreply@recruforce2.com
MAIL_PASSWORD: ****
```

---

## 🗃️ Migrations Base de Données (Liquibase)

Les migrations sont versionnées dans `src/main/resources/db/changelog/changelog/` et s'exécutent automatiquement au démarrage.

| Fichier | Description |
|---------|-------------|
| `001` | Création table `users` |
| `002` | Création table `skills` |
| `003` | Création table `candidate_profiles` |
| `004` | Création table `job_offers` |
| `005` | Création table `applications` |
| `006` | Création table `notification_preferences` |
| `007` | Index de performance |
| `008` | Données initiales (seed) |
| `009` | Création table `interviews` |
| `010` | Création table `interview_slots` |
| `011` | Création table `interview_feedbacks` |
| `012` | Création table `recruiter_availability` |
| `013` | Création table `notifications` |
| `014` | Création table `prediction_results` |
| `015` | Index supplémentaires |

---

## 🔒 Sécurité

- **Authentification** : JWT stateless (access token + refresh token)
- **Autorisation** : Spring Security avec contrôle par rôles (`@PreAuthorize`)
- **Mots de passe** : Hachage BCrypt (force 12)
- **CORS** : Configuré via `CorsConfig.java`
- **HTTPS** : Obligatoire en production (TLS 1.2+)
- **Protection OWASP** : Validation stricte des entrées, protection XSS/CSRF
- **RGPD** : Droit à l'oubli, consentement, transparence IA

---

## 📊 Performances Cibles

| Opération | Temps cible |
|-----------|-------------|
| Chargement de page | < 3 secondes (90% des requêtes) |
| Opérations CRUD | < 2 secondes |
| Parsing de CV | < 5 secondes |
| Matching Poste-CV | < 3 secondes |

**Capacité** : 10 utilisateurs simultanés, 500 candidatures/mois, 50 offres actives.

---

## 🧪 Tests

```bash
# Lancer tous les tests
./mvnw test

# Tests avec rapport de couverture
./mvnw test jacoco:report
```

Les tests utilisent le profil `application-test.yml` avec une base H2 en mémoire.

---

## 🐳 Docker

### Structure Docker
```
docker/
├── mongo/       # Configuration MongoDB
└── postgres/    # Configuration PostgreSQL (init scripts)
```

### Build de l'image backend
```bash
docker build -t recruforce2-backend .
```

### Docker Compose (stack complète)
```bash
# Démarrer tous les services
docker-compose up -d

# Voir les logs
docker-compose logs -f backend

# Arrêter
docker-compose down
```

---

## 📚 Documentation API

La documentation interactive Swagger UI est disponible après démarrage :

- **Swagger UI** : `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON** : `http://localhost:8080/v3/api-docs`

---

## 🗂️ Rôles & Permissions

| Rôle | Permissions |
|------|-------------|
| `ADMIN` | Accès complet — gestion utilisateurs, paramètres, statistiques |
| `RECRUTEUR` | Gestion offres, candidatures, entretiens, feedback |
| `MANAGER` | Consultation candidats, saisie feedback Hard Skills |

---

## 🔗 Dépôts Liés

| Dépôt | Description |
|-------|-------------|
| `recruforce2-frontend` | Application Angular (interface utilisateur) |
| `recruforce2-ai-model` | Microservice Python (parsing CV, matching, prédiction) |

---

## 📖 Références

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security](https://docs.spring.io/spring-security/reference/index.html)
- [Liquibase](https://docs.liquibase.com/)
- [OpenAPI / Swagger](https://springdoc.org/)
- [RecruForce2 Frontend](../recruforce2-frontend)
- [RecruForce2 AI Model](../recruforce2-ai-model)
