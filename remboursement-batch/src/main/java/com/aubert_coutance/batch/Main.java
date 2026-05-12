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
 * Cette classe orchestre l'ensemble du processus : chargement de la configuration,
 * connexion à la base de données, initialisation des services et exécution du traitement.
 */
public class Main {
    
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * Point d'entrée du programme.
     * 
     * @param args Arguments de ligne de commande (non utilisés actuellement)
     */
    public static void main(String[] args) {
        LOGGER.info("Démarrage du traitement batch.");

        try {
            AppProperties appProperties = new AppProperties();
            DatabaseConfig databaseConfig = new DatabaseConfig(appProperties);

            try (Connection connection = databaseConfig.getConnection()) {
                RemboursementDao dao = new RemboursementDao(connection);
                CsvParserService parserService = new CsvParserService();
                
                BatchProcessor processor = new BatchProcessor(
                        appProperties.getSourceDirectory(),
                        appProperties.getArchiveDirectory(),
                        parserService,
                        dao
                );

                processor.processFiles();
                LOGGER.info("Traitement batch terminé avec succès.");
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