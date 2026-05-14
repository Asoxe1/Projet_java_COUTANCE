# Batch Remboursement

## Objectif

Projet Java/Maven qui lit des fichiers CSV de type `users_YYYYMMDDHHmmss.csv`, insère ou met à jour les données en base PostgreSQL et archive les fichiers traités.

## Contenu du projet

- `remboursement-batch/pom.xml` : configuration Maven et dépendances
- `remboursement-batch/src/main/java/com/aubert_coutance/batch/Main.java` : point d'entrée
- `remboursement-batch/src/main/java/com/aubert_coutance/batch/service/BatchProcessor.java` : traitement des fichiers
- `remboursement-batch/src/main/java/com/aubert_coutance/batch/service/CsvParserService.java` : parsing CSV et extraction du timestamp
- `remboursement-batch/src/main/java/com/aubert_coutance/batch/dao/RemboursementDao.java` : persistence en base
- `remboursement-batch/src/main/java/com/aubert_coutance/batch/config/AppProperties.java` : lecture des propriétés
- `remboursement-batch/src/main/java/com/aubert_coutance/batch/config/DatabaseConfig.java` : connexion JDBC
- `remboursement-batch/src/main/java/com/aubert_coutance/batch/model/Remboursement.java` : modèle métier
- `remboursement-batch/src/main/resources/database.properties` : paramètres de configuration
- `remboursement-batch/src/test/java` : tests unitaires

## Prérequis

- Java 17
- Maven
- PostgreSQL

## Configuration

Modifier le fichier `remboursement-batch/src/main/resources/database.properties` avec les paramètres de connexion et les répertoires :

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

## Format du fichier CSV

Nom du fichier : `users_YYYYMMDDHHmmss.csv`

Colonnes attendues dans l'ordre :

`Numero_Securite_Sociale,Nom,Prenom,Date_Naissance,Numero_Telephone,E_Mail,ID_Remboursement,Code_Soin,Montant_Remboursement`

Exemple d’enregistrement :

```csv
12345678901234,Dupont,Jean,1990-05-15,0612345678,jean@example.com,REM001,SOIN001,150.50
```

## Lancement

```bash
cd remboursement-batch
mvn clean package
java -jar target/remboursement-batch-1.0-SNAPSHOT.jar
```

## Fonctionnement du batch

- Recherche des fichiers `users_*.csv` dans le répertoire source
- Lecture et validation des lignes CSV
- Extraction du timestamp depuis le nom du fichier
- Insertion ou mise à jour des enregistrements en base PostgreSQL
- Déplacement du fichier vers le répertoire d’archive

## Tests

```bash
cd remboursement-batch
mvn test
```

## Choix techniques

- Java 17
- Maven
- OpenCSV pour le parsing CSV
- JDBC PostgreSQL
- JUnit 5 pour les tests unitaires
