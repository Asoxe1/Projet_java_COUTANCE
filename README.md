# Projet Batch Remboursement

**Auteurs :** Alexandre COUTANCE, Quentin AUBERT
**Formation :** Élèves-ingénieurs DE2, Télécom Saint-Étienne

---

## 1. Objectif du Projet

Ce projet consiste en l'élaboration d'un programme batch Java/Maven automatisant l'intégration de données de santé. Le système est conçu pour :

* Surveiller un répertoire source à la recherche de fichiers CSV horodatés (`users_YYYYMMDDHHmmss.csv`).
* Extraire et valider les informations de remboursement.
* Réaliser une opération d'"Upsert" (insertion ou mise à jour) dans une base de données PostgreSQL en fonction de l'identifiant de remboursement.
* Déplacer les fichiers traités vers un répertoire d'archivage sécurisé.

## 2. Structure de l'Application

Le projet est organisé selon une architecture modulaire pour séparer les responsabilités :

* `com.aubert_coutance.batch.Main` : Orchestrateur et point d'entrée.
* `service.BatchProcessor` : Gère le cycle de vie du traitement (lecture dossier, archivage).
* `service.CsvParserService` : Analyse les fichiers CSV à l'aide d'expressions régulières pour la validation des noms et des dates.
* `dao.RemboursementDao` : Gère la persistance avec des transactions SQL optimisées (clauses `ON CONFLICT` et traitements par lots/batch).
* `config` : Classes dédiées au chargement des propriétés système et à la configuration JDBC.

## 3. Choix Techniques

L'application repose sur les standards industriels suivants :

* **Java 17 (LTS)** : Pour les performances et la modernité des API (Time, NIO.2).
* **Maven** : Pour la gestion rigoureuse des dépendances et du cycle de build.
* **OpenCSV** : Bibliothèque robuste pour le parsing de fichiers plats.
* **PostgreSQL Driver** : Pour une interaction directe et performante avec la base relationnelle.
* **JUnit 5** : Validation de la logique métier par des tests unitaires automatisés.

## 4. Configuration et Déploiement

### Prérequis

* Installation de Java 17 et Maven.
* Accès à une instance PostgreSQL.

### Schéma de Base de Données

Le script suivant permet d'initialiser la table nécessaire :

```sql
CREATE TABLE remboursement (
  numero_securite_sociale VARCHAR(15) NOT NULL,
  nom VARCHAR(100) NOT NULL,
  prenom VARCHAR(100) NOT NULL,
  date_naissance DATE NOT NULL,
  numero_telephone VARCHAR(20),
  e_mail VARCHAR(150),
  id_remboursement VARCHAR(50) PRIMARY KEY,
  code_soin VARCHAR(50) NOT NULL,
  montant_remboursement DECIMAL(10, 2) NOT NULL,
  timestamp_fichier TIMESTAMP NOT NULL
);

```

### Paramétrage

Le fichier `src/main/resources/database.properties` doit être ajusté selon votre environnement :

```properties
db.url=jdbc:postgresql://localhost:5432/postgres
db.user=postgres
db.password=monmotdepasse
batch.dossier.source=C:/batch/input/
batch.dossier.archive=C:/batch/archive/

```

## 5. Utilisation

### Compilation et Lancement

Utilisez les commandes Maven standard à la racine du projet :

```bash
mvn clean package
java -jar target/remboursement-batch-1.0-SNAPSHOT.jar

```

### Exécution des Tests

Pour lancer la suite de tests unitaires :

```bash
mvn test

```
