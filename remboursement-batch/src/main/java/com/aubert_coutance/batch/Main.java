package com.aubert_coutance.batch;

import com.aubert_coutance.batch.config.AppProperties;
import com.aubert_coutance.batch.config.DatabaseConfig;
import com.aubert_coutance.batch.dao.RemboursementDao;
import com.aubert_coutance.batch.service.BatchProcessor;
import com.aubert_coutance.batch.service.CsvParserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe principale du programme batch de traitement des remboursements.
 * 
 * Cette classe orchestrate l'ensemble du processus :
 * 1. Charge les configurations depuis le fichier database.properties
 * 2. Établit une connexion à la base de données PostgreSQL
 * 3. Initialise les services (parsing CSV, accès données)
 * 4. Lance le traitement batch des fichiers
 * 5. Gère les erreurs et enregistre les événements
 */
public class Main {
    
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * Point d'entrée du programme.
     * 
     * @param args Arguments de ligne de commande (non utilisés actuellement)
     */
    public static void main(String[] args) {
        LOGGER.info("========== Démarrage du traitement Batch ==========");

        try {
            // Étape 1 : Charger la configuration
            AppProperties appProperties = new AppProperties();

            // Étape 2 : Configurer la connexion à la base de données
            DatabaseConfig databaseConfig = new DatabaseConfig(appProperties);

            // Étape 3 : Établir la connexion et initialiser les services
            try (Connection connection = databaseConfig.getConnection()) {
                RemboursementDao dao = new RemboursementDao(connection);
                CsvParserService parserService = new CsvParserService();
                
                // Étape 4 : Créer et lancer le processeur batch
                BatchProcessor processor = new BatchProcessor(
                        appProperties.getSourceDirectory(),
                        appProperties.getArchiveDirectory(),
                        parserService,
                        dao
                );

                processor.processFiles();
                LOGGER.info("========== Traitement Batch terminé avec succès ==========");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur de connexion à la base de données PostgreSQL", e);
            System.exit(1);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur critique lors de l'exécution du batch", e);
            System.exit(1);
        }
    }
}