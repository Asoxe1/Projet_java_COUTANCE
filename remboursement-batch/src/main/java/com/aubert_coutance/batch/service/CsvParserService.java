package com.aubert_coutance.batch.service;

import com.aubert_coutance.batch.model.Remboursement;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service responsable de l'analyse (parsing) des fichiers CSV de remboursements.
 * 
 * Cette classe :
 * - Parse les fichiers CSV au format spécifié
 * - Valide le format des données
 * - Gère les erreurs de parsing (dates mal formatées, nombres invalides, etc.)
 * - Extrait le timestamp du nom de fichier
 * - Crée des objets Remboursement à partir des données CSV
 */
public class CsvParserService {

    private static final Logger LOGGER = Logger.getLogger(CsvParserService.class.getName());
    
    // Motif regex pour valider le nom de fichier et extraire le timestamp
    // Format attendu : users_YYYYMMDDHHmmss.csv
    private static final Pattern FILENAME_PATTERN = Pattern.compile("users_(\\d{14})\\.csv");
    
    // Format de conversion du timestamp (YYYYMMDDHHmmss)
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    // Format de conversion des dates de naissance (yyyy-MM-dd)
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Parse un fichier CSV et retourne une liste d'objets Remboursement.
     * Le timestamp est automatiquement extrait du nom de fichier et assigné à chaque enregistrement.
     * 
     * Les lignes invalides (mauvais format, données manquantes) sont ignorées avec un log d'avertissement.
     * L'en-tête CSV est automatiquement détecté et ignoré.
     * 
     * @param filePath Le chemin vers le fichier CSV à analyser
     * @return La liste des remboursements valides extraits du fichier
     * @throws IOException Si le fichier ne peut pas être lu
     * @throws CsvValidationException Si une erreur de parsing CSV se produit
     * @throws IllegalArgumentException Si le nom de fichier ne respecte pas le format attendu
     */
    public List<Remboursement> parseFile(Path filePath) throws IOException, CsvValidationException {
        List<Remboursement> remboursements = new ArrayList<>();
        LocalDateTime fileTimestamp = extractTimestampFromFilename(filePath.getFileName().toString());
        int lineNumber = 0;

        try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
            String[] line;

            while ((line = reader.readNext()) != null) {
                lineNumber++;
                
                // Vérifier que la ligne contient le nombre de colonnes attendu
                if (line.length < 9) {
                    LOGGER.warning("Ligne " + lineNumber + " incomplète (seulement " + line.length + " colonnes), ignorée.");
                    continue;
                }

                // Ignorer la ligne d'en-tête
                if ("Numero_Securite_Sociale".equalsIgnoreCase(line[0].trim())) {
                    LOGGER.fine("En-tête CSV détecté et ignoré (ligne " + lineNumber + ")");
                    continue;
                }

                try {
                    // Créer un nouvel objet Remboursement et remplir ses propriétés
                    Remboursement r = new Remboursement();
                    r.setNumeroSecuriteSociale(line[0].trim());
                    r.setNom(line[1].trim());
                    r.setPrenom(line[2].trim());
                    
                    // Parser la date de naissance au format yyyy-MM-dd
                    r.setDateNaissance(LocalDate.parse(line[3].trim(), DATE_FORMATTER));
                    
                    r.setNumeroTelephone(line[4].trim());
                    r.seteMail(line[5].trim());
                    r.setIdRemboursement(line[6].trim());
                    r.setCodeSoin(line[7].trim());
                    
                    // Parser le montant comme BigDecimal pour éviter les pertes de précision
                    r.setMontantRemboursement(new BigDecimal(line[8].trim()));
                    
                    // Assigner le timestamp extrait du nom de fichier
                    r.setTimestampFichier(fileTimestamp);

                    remboursements.add(r);
                } catch (DateTimeParseException e) {
                    LOGGER.log(Level.WARNING, "Erreur de format de date sur la ligne " + lineNumber + ", ligne ignorée.", e);
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Erreur de format de montant sur la ligne " + lineNumber + ", ligne ignorée.", e);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erreur lors du parsing de la ligne " + lineNumber + ", ligne ignorée.", e);
                }
            }
        }
        
        LOGGER.info("Parsing terminé : " + remboursements.size() + " enregistrement(s) valide(s) sur " + lineNumber + " ligne(s) lue(s).");
        return remboursements;
    }

    /**
     * Extrait le timestamp du nom de fichier.
     * Le format attendu est : users_YYYYMMDDHHmmss.csv
     * 
     * @param filename Nom du fichier CSV
     * @return Timestamp extrait au format LocalDateTime
     * @throws IllegalArgumentException Si le nom de fichier ne respecte pas le format
     */
    private LocalDateTime extractTimestampFromFilename(String filename) {
        Matcher matcher = FILENAME_PATTERN.matcher(filename);
        if (matcher.matches()) {
            String timestampStr = matcher.group(1);
            return LocalDateTime.parse(timestampStr, TIMESTAMP_FORMATTER);
        }
        throw new IllegalArgumentException("Le format du nom de fichier est invalide : " + filename + ". Format attendu : users_YYYYMMDDHHmmss.csv");
    }
}