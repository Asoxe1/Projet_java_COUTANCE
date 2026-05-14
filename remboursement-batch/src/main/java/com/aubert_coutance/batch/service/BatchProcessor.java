package com.aubert_coutance.batch.service;

import com.aubert_coutance.batch.dao.RemboursementDao;
import com.aubert_coutance.batch.model.Remboursement;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service orchestrant l'ensemble du processus batch.
 * 
 * Cette classe est le cœur du programme batch et gère la recherche des fichiers CSV,
 * l'analyse des données, la persistance en base et l'archivage des fichiers traités.
 */
public class BatchProcessor {

    private static final Logger LOGGER = Logger.getLogger(BatchProcessor.class.getName());

    private final String sourceDir;
    private final String archiveDir;
    private final CsvParserService parserService;
    private final RemboursementDao dao;

    /**
     * Constructeur du processeur batch.
     * 
     * @param sourceDir Chemin du répertoire source contenant les fichiers CSV à traiter
     * @param archiveDir Chemin du répertoire d'archivage pour les fichiers traités
     * @param parserService Service de parsing CSV injecté
     * @param dao DAO pour les opérations de base de données injecté
     */
    public BatchProcessor(String sourceDir, String archiveDir, CsvParserService parserService, RemboursementDao dao) {
        this.sourceDir = sourceDir;
        this.archiveDir = archiveDir;
        this.parserService = parserService;
        this.dao = dao;
    }

    /**
     * Lance l'exécution complète du batch.
     * 
     * Cette méthode crée les répertoires source et d'archive si nécessaire, recherche les fichiers
     * CSV au format "users_*.csv", traite chaque fichier et archive les fichiers traités.
     */
    public void processFiles() {
        Path sourcePath = Paths.get(sourceDir);
        Path archivePath = Paths.get(archiveDir);

        try {
            // Créer les répertoires s'ils n'existent pas
            if (!Files.exists(sourcePath)) {
                Files.createDirectories(sourcePath);
                LOGGER.info("Répertoire source créé : " + sourcePath);
            }
            if (!Files.exists(archivePath)) {
                Files.createDirectories(archivePath);
                LOGGER.info("Répertoire d'archive créé : " + archivePath);
            }

            // Scruter le répertoire source avec un filtre pour les fichiers CSV
            int fileCount = 0;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcePath, "users_*.csv")) {
                for (Path entry : stream) {
                    fileCount++;
                    processSingleFile(entry, archivePath);
                }
            }
            
            if (fileCount == 0) {
                LOGGER.info("Aucun fichier à traiter dans le répertoire source.");
            } else {
                LOGGER.info("Traitement terminé : " + fileCount + " fichier(s) traité(s).");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'accès aux répertoires source ou archive.", e);
        }
    }

    /**
     * Traite un fichier CSV unique.
     * 
     * Le traitement inclut l'analyse du fichier, l'application d'un upsert en base de données
     * puis l'archivage du fichier si le traitement est réussi. En cas d'erreur, le fichier n'est pas archivé
     * afin de permettre un retraitement ultérieur.
     * 
     * @param filePath Chemin du fichier à traiter
     * @param archivePath Chemin du répertoire d'archive
     */
    private void processSingleFile(Path filePath, Path archivePath) {
        String fileName = filePath.getFileName().toString();
        LOGGER.info("Début du traitement du fichier : " + fileName);
        
        try {
            List<Remboursement> remboursements = parserService.parseFile(filePath);
            
            if (remboursements.isEmpty()) {
                LOGGER.warning("Aucun enregistrement valide trouvé dans le fichier : " + fileName);
                archiveFile(filePath, archivePath, fileName);
                return;
            }

            dao.upsert(remboursements);
            LOGGER.info(remboursements.size() + " enregistrement(s) inséré(s)/mis à jour pour " + fileName);

            archiveFile(filePath, archivePath, fileName);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur I/O lors de la lecture du fichier : " + fileName, e);
        } catch (CsvValidationException e) {
            LOGGER.log(Level.SEVERE, "Erreur de validation CSV pour le fichier : " + fileName, e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'insertion en base de données pour : " + fileName, e);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Format de fichier invalide (impossible d'extraire le timestamp) : " + fileName, e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue lors du traitement de : " + fileName, e);
        }
    }

    /**
     * Archive un fichier en le déplaçant vers le répertoire d'archive.
     * 
     * @param filePath Chemin source du fichier
     * @param archivePath Répertoire d'archive cible
     * @param fileName Nom du fichier pour les logs
     */
    private void archiveFile(Path filePath, Path archivePath, String fileName) {
        try {
            Path destinationPath = archivePath.resolve(filePath.getFileName());
            Files.move(filePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Fichier archivé avec succès : " + destinationPath);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'archivage du fichier : " + fileName, e);
        }
    }
}