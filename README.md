# Recruforce2 - Backend Core 🚀

Cœur du système de recrutement hybride (PostgreSQL/MongoDB). Ce service gère l'authentification, les offres, les candidatures et la planification des entretiens.

## 🏗️ Architecture
- **Framework:** Spring Boot 3
- **Bases de données:** PostgreSQL (Relationnel) & MongoDB (Documents CV)
- **Migration:** Liquibase
- **Sécurité:** Spring Security + JWT

## ⚙️ Configuration Locale
1. Créez un fichier `.env` à la racine (basé sur `.env.example`).
2. Lancez les bases de données via Docker :
   ```bash
   docker-compose up -d

    Lancez l'application :
    Bash

    mvn spring-boot:run -Dspring-boot.run.profiles=dev

🌿 Stratégie de Branches

    main : Production stable.

    develop : Branche d'intégration.

    feature/T-ID-nom : Développement de fonctionnalités.


---

### 2. Repo : `recruforce2-ai-model-service` (Python/FastAPI)
Ce dépôt est dédié au traitement du langage naturel (NLP) et au parsing de CV.

```markdown
# Recruforce2 - AI Model Service 🧠

Service d'intelligence artificielle dédié au parsing de CV et au calcul du score de matching entre candidats et offres.

## 🛠️ Stack Technique
- **Framework:** FastAPI
- **NLP:** SpaCy / PyMuPDF
- **Langue supportée:** Français / Anglais

## 🚀 Installation
1. Créer un environnement virtuel :
   ```bash
   python -m venv venv
   source venv/bin/activate

    Installer les dépendances :
    Bash

    pip install -r requirements.txt
    python -m spacy download fr_core_news_md

    Lancer le service :
    Bash

    uvicorn main:app --reload --port 5000

📡 Endpoints Clés

    POST /parse-cv : Extrait les données structurées d'un PDF.

    POST /match-score : Calcule la pertinence candidat/offre.


---

### 3. Repo : `recruforce2-frontend` (Angular)
L'interface utilisateur pour les recruteurs et les candidats.

```markdown
# Recruforce2 - Frontend Portal 💻

Interface utilisateur moderne pour la gestion du recrutement, développée avec Angular.

## 🎨 Technologies
- **Framework:** Angular 17+
- **Style:** Tailwind CSS / Angular Material
- **State Management:** RxJS

## 🏃 Démarrage Rapide
1. Installer les dépendances :
   ```bash
   npm install

    Lancer le serveur de développement :
    Bash

    ng serve

    Accès : http://localhost:4200

🔗 Intégration

Le frontend communique avec le Backend Core (port 8080). Assurez-vous que le backend tourne pour l'authentification.


---

### 💡 Le fichier `.env.example` (À mettre dans chaque repo)

Pour que tes collaborateurs (ou ton futur "moi") sachent quelles variables configurer sans voir tes secrets sur GitHub, crée un fichier `.env.example` dans chaque repo :

```env
# --- DATABASE ---
DB_URL=jdbc:postgresql://localhost:5432/db_name
DB_USER=votre_user
DB_PASSWORD=votre_password

# --- SECURITY ---
JWT_SECRET=generer_une_cle_longue_ici
