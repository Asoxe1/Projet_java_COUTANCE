# Architecture et Diagrammes de Flux

## 1. Architecture générale

### Modèle en couches

```
┌─────────────────────────────────────────────────────────┐
│                    APPLICATION                          │
│                     Main.java                           │
├─────────────────────────────────────────────────────────┤
│                   ORCHESTRATION                         │
│                 BatchProcessor                          │
│              (Coordination du flux)                     │
├──────────────────────┬──────────────────────────────────┤
│                      │                                  │
│   MÉTIER/SERVICE     │      PERSISTANCE                │
│                      │                                  │
│  CsvParserService   │   RemboursementDao              │
│  - Parse CSV        │   - Upsert BD                   │
│  - Valide données   │   - Batch processing            │
│  - Extrait données  │   - Prepared statements         │
│                      │                                  │
├──────────────────────┴──────────────────────────────────┤
│                   CONFIGURATION                         │
│  AppProperties    +    DatabaseConfig                  │
│  (Charger props)     (Gérer connexions)               │
├─────────────────────────────────────────────────────────┤
│                     MODÈLE                              │
│                  Remboursement                          │
│              (Représentation des données)              │
├─────────────────────────────────────────────────────────┤
│              BASE DE DONNÉES POSTGRESQL                 │
│                  Table remboursement                    │
└─────────────────────────────────────────────────────────┘
```

## 2. Diagramme de flux du batch

```
┌──────────────────────┐
│   Démarrage          │
│   Main.main()        │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────────────────────────┐
│ 1. Charger AppProperties                 │
│    - database.properties                 │
│    - db.url, db.user, db.password       │
│    - batch.dossier.source/archive       │
└──────────┬───────────────────────────────┘
           │
           ▼
┌──────────────────────────────────────────┐
│ 2. Établir DatabaseConfig                │
│    - Connexion PostgreSQL                │
│    - Try-with-resources                 │
└──────────┬───────────────────────────────┘
           │
           ▼
┌──────────────────────────────────────────┐
│ 3. Initialiser les services              │
│    - RemboursementDao                    │
│    - CsvParserService                    │
│    - BatchProcessor                      │
└──────────┬───────────────────────────────┘
           │
           ▼
┌──────────────────────────────────────────┐
│ 4. BatchProcessor.processFiles()         │
│    - Scruter répertoire source           │
│    - Glob : users_*.csv                  │
└──────────┬───────────────────────────────┘
           │
           ▼
    ┌──────────────────┐
    │ Pour chaque      │
    │ fichier trouvé   │
    └────┬─────────────┘
         │
         ▼
    ┌──────────────────────────────────────┐
    │ 5. Parser le fichier CSV             │
    │    - CsvParserService.parseFile()   │
    │    - OpenCSV.CSVReader              │
    │    - Valider chaque ligne           │
    │    - Extraire timestamp du nom      │
    │    - Retourner List<Remboursement> │
    └────┬─────────────────────────────────┘
         │
         ▼
    ┌──────────────────────────────────────┐
    │ 6. Insérer/Mettre à jour en BD      │
    │    - RemboursementDao.upsert()      │
    │    - SQL : INSERT ... ON CONFLICT   │
    │    - PreparedStatement.addBatch()   │
    │    - executeBatch()                 │
    └────┬─────────────────────────────────┘
         │
         ▼
    ┌──────────────────────────────────────┐
    │ 7. Archiver le fichier               │
    │    - Files.move()                    │
    │    - source → archive               │
    │    - REPLACE_EXISTING               │
    └────┬─────────────────────────────────┘
         │
         ▼
    ┌──────────────────────────────────────┐
    │ Fichier suivant ?                    │
    │ NON → Fin du batch                  │
    │ OUI → Retour à l'étape 5            │
    └──────────────────────────────────────┘
           │
           ▼
┌──────────────────────────────────────────┐
│ 8. Fermer connexion (try-with-resources)│
│    - Ressources libérées automatiquement│
└──────────┬───────────────────────────────┘
           │
           ▼
┌──────────────────────┐
│   Fin du batch       │
│   Exit code 0        │
└──────────────────────┘
```

## 3. Diagramme détaillé du parsing CSV

```
┌─────────────────────────────────┐
│ parseFile(Path filePath)        │
└────────┬────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────┐
│ 1. Extraire timestamp du nom de fichier        │
│    Regex : users_(\d{14})\.csv                 │
│    Format : users_YYYYMMDDHHmmss.csv           │
│    Exemple : users_20240115143000.csv          │
│            → 2024-01-15 14:30:00               │
└────────┬────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────┐
│ 2. Ouvrir le fichier avec CSVReader             │
│    - FileReader(filePath)                       │
│    - CSVReader(fileReader)                      │
└────────┬────────────────────────────────────────┘
         │
         ▼
    ┌──────────────────────┐
    │ 3. Pour chaque ligne │
    │    du fichier        │
    └──┬───────────────────┘
       │
       ▼
    ┌──────────────────────────────────────────┐
    │ 4. Valider la ligne                      │
    │    - Nombre de colonnes ≥ 9             │
    │    - Ignorer l'en-tête                  │
    │    - Trim() les espaces                 │
    └──┬───────────────────────────────────────┘
       │
       ▼
    ┌──────────────────────────────────────────┐
    │ 5. Parser chaque colonne                 │
    │    [0] : Numéro SS → String            │
    │    [1] : Nom → String                   │
    │    [2] : Prénom → String                │
    │    [3] : Date naiss. → LocalDate        │
    │         Format : yyyy-MM-dd             │
    │    [4] : Téléphone → String             │
    │    [5] : Email → String                 │
    │    [6] : ID Remb. → String (clé)       │
    │    [7] : Code soin → String             │
    │    [8] : Montant → BigDecimal           │
    │         Précision 10,2                  │
    └──┬───────────────────────────────────────┘
       │
       ├─ Erreur de parsing ?
       │  ├─ DateTimeParseException
       │  ├─ NumberFormatException
       │  └─ Autre Exception
       │      │
       │      ▼
       │  Log WARNING et ignorer la ligne
       │
       └─ Succès
          │
          ▼
       ┌──────────────────────────────────────┐
       │ 6. Créer objet Remboursement        │
       │    - Assigner tous les champs       │
       │    - Assigner le timestamp fichier  │
       │    - Ajouter à la List              │
       └──┬───────────────────────────────────┘
          │
          └─ Ligne suivante ?
             NON → Fin du fichier
             OUI → Retour à l'étape 3
                │
                ▼
       ┌──────────────────────────────────────┐
       │ 7. Logger le résumé                  │
       │    "Parsing terminé : N enregistrement(s)"
       │    Retourner la List<Remboursement> │
       └──────────────────────────────────────┘
```

## 4. Diagramme du UPSERT PostgreSQL

```
INSERT INTO remboursement (...)
VALUES (?, ?, ?, ...)
ON CONFLICT (id_remboursement) DO UPDATE SET ...

       │
       ▼
┌─────────────────────────────────────┐
│ PostgreSQL vérifie l'ID             │
└────┬────────────────────────────────┘
     │
     ├─ ID n'existe pas ?
     │  │
     │  ▼
     │  INSERT : Créer nouvelle ligne
     │
     └─ ID existe déjà ?
        │
        ▼
        UPDATE : Mettre à jour la ligne existante
        - numero_securite_sociale
        - nom, prenom
        - date_naissance
        - numero_telephone, e_mail
        - code_soin
        - montant_remboursement
        - timestamp_fichier
```

## 5. Structure des données : Remboursement

```
┌───────────────────────────────────────┐
│        Remboursement                  │
├───────────────────────────────────────┤
│ - numeroSecuriteSociale : String      │
│ - nom : String                        │
│ - prenom : String                     │
│ - dateNaissance : LocalDate           │
│ - numeroTelephone : String            │
│ - eMail : String                      │
│ - idRemboursement : String (KEY)      │
│ - codeSoin : String                   │
│ - montantRemboursement : BigDecimal   │
│ - timestampFichier : LocalDateTime    │
├───────────────────────────────────────┤
│ equals() : basé sur idRemboursement   │
│ hashCode() : basé sur idRemboursement │
│ toString() : représentation complète  │
└───────────────────────────────────────┘
```

## 6. Gestion des erreurs

```
┌────────────────────────────────────┐
│ Erreur détectée                    │
└────┬───────────────────────────────┘
     │
     ├─ IOException (fichier)
     │  └─ Log SEVERE + continue batch
     │
     ├─ CsvValidationException
     │  └─ Log SEVERE + skip fichier
     │
     ├─ SQLException (base données)
     │  └─ Log SEVERE + ne pas archiver
     │
     ├─ DateTimeParseException
     │  └─ Log WARNING + skip ligne
     │
     ├─ NumberFormatException
     │  └─ Log WARNING + skip ligne
     │
     └─ IllegalArgumentException
        └─ Log SEVERE + ne pas traiter
```

## 7. Cycle de vie d'une connexion

```
┌──────────────────────────────────┐
│ main() crée connection           │
│ try-with-resources               │
└────┬─────────────────────────────┘
     │
     ▼
┌──────────────────────────────────┐
│ DatabaseConfig.getConnection()   │
│ DriverManager.getConnection()    │
│ URL : jdbc:postgresql://...      │
│ User : postgres                  │
│ Password : ***                   │
└────┬─────────────────────────────┘
     │
     ▼
┌──────────────────────────────────┐
│ Initialiser services avec conn.  │
│ - RemboursementDao               │
│ - BatchProcessor                 │
└────┬─────────────────────────────┘
     │
     ▼
┌──────────────────────────────────┐
│ Traitement du batch (try block)  │
│ - Parsing                        │
│ - Insertions/Updates             │
│ - Archivage                      │
└────┬─────────────────────────────┘
     │
     ▼
┌──────────────────────────────────┐
│ Fin du try block                 │
│ try-with-resources ferme auto    │
│ connection.close()               │
│ Ressources libérées              │
└──────────────────────────────────┘
```

## 8. Pipeline de dépendances

```
Main
 ├─ AppProperties
 │   └─ database.properties
 │
 ├─ DatabaseConfig
 │   └─ AppProperties
 │
 ├─ RemboursementDao
 │   └─ Connection (remboursement)
 │
 ├─ CsvParserService
 │   └─ OpenCSV (parsing)
 │
 └─ BatchProcessor
     ├─ CsvParserService
     ├─ RemboursementDao
     └─ File I/O
```

## 9. Flux de traitement avec exemple

```
Input CSV : users_20240115143000.csv
─────────────────────────────────────
Numero_Securite_Sociale,Nom,Prenom,Date_Naissance,...
12345678901234,Dupont,Jean,1990-05-15,...,REM001,...,150.50

              ↓ PARSE
              
Remboursement {
  numeroSecuriteSociale: "12345678901234"
  nom: "Dupont"
  prenom: "Jean"
  dateNaissance: 1990-05-15
  idRemboursement: "REM001"
  montantRemboursement: 150.50
  timestampFichier: 2024-01-15 14:30:00
}

              ↓ UPSERT
              
PostgreSQL INSERT/UPDATE
avec clé primaire idRemboursement

              ↓ ARCHIVE
              
File moved from:
  C:/batch/input/users_20240115143000.csv
to:
  C:/batch/archive/users_20240115143000.csv
```

## 10. Dépendances transverses

```
java.util.logging (Logging)
        ↑
        ├─ Main
        ├─ BatchProcessor
        ├─ CsvParserService
        └─ RemboursementDao

java.sql (Database)
        ↑
        ├─ DatabaseConfig
        └─ RemboursementDao

java.nio.file (File I/O)
        ↑
        ├─ AppProperties (classpath)
        └─ BatchProcessor (scan/archive)

java.time (Temporal)
        ↑
        ├─ Remboursement
        ├─ CsvParserService
        └─ RemboursementDao

java.math (Decimal)
        ↑
        ├─ Remboursement
        └─ RemboursementDao
```

---

**Version** : 1.0
**Date** : Janvier 2024
