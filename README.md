# Batch Remboursement

**Date : 12/05/2025**

## Objectif

Application Java/Maven qui scrute un dossier pour les fichiers `users_YYYYMMDDHHmmss.csv`, parse les lignes CSV, applique un upsert PostgreSQL en fonction de `ID_Remboursement`, puis archive les fichiers traités.

## Structure minimale

- `remboursement-batch/pom.xml` : configuration Maven
- `src/main/java/.../Main.java` : point d'entrée
- `src/main/java/.../service/BatchProcessor.java` : orchestration du batch
- `src/main/java/.../service/CsvParserService.java` : parsing CSV et extraction du timestamp
- `src/main/java/.../dao/RemboursementDao.java` : persistance PostgreSQL
- `src/main/java/.../config/AppProperties.java` : lecture des propriétés
- `src/main/java/.../config/DatabaseConfig.java` : création de la connexion
- `src/main/java/.../model/Remboursement.java` : modèle métier
- `src/main/resources/database.properties` : configuration externe
- `src/test/java/...` : tests unitaires

## Prérequis

- Java 17
- Maven
- PostgreSQL

## Configuration

Modifier `src/main/resources/database.properties` :

```properties
db.url=jdbc:postgresql://localhost:5432/postgres
db.user=postgres
db.password=monmotdepasse
batch.dossier.source=C:/batch/input/
batch.dossier.archive=C:/batch/archive/
```

## Schéma de base de données

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

## Format CSV

Nom du fichier : `users_YYYYMMDDHHmmss.csv`

Colonnes dans l'ordre :

`Numero_Securite_Sociale,Nom,Prenom,Date_Naissance,Numero_Telephone,E_Mail,ID_Remboursement,Code_Soin,Montant_Remboursement`

Exemple :

```csv
12345678901234,Dupont,Jean,1990-05-15,0612345678,jean@example.com,REM001,SOIN001,150.50
```

## Utilisation

```bash
cd remboursement-batch
mvn clean package
java -jar target/remboursement-batch-1.0-SNAPSHOT.jar
```

## Fonctionnement

- Recherche des fichiers `users_*.csv` dans le répertoire source
- Parsing CSV et validation des données
- Extraction du timestamp à partir du nom de fichier
- Upsert PostgreSQL sur `id_remboursement`
- Déplacement du fichier vers le répertoire d'archive

## Tests

```bash
mvn test
```

Tests unitaires présents pour :
- parsing CSV
- lecture des propriétés
- modèle métier

## Choix techniques

- Java 17
- Maven
- OpenCSV pour le parsing
- PostgreSQL JDBC
- JUnit 5
- UPSERT PostgreSQL (`ON CONFLICT`) pour gérer insert/update
