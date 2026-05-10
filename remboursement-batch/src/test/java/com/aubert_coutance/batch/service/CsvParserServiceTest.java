package com.aubert_coutance.batch.service;

import com.aubert_coutance.batch.model.Remboursement;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe CsvParserService.
 * 
 * Ces tests vérifient :
 * - Le parsing correct des fichiers CSV valides
 * - L'extraction du timestamp du nom de fichier
 * - La gestion des erreurs (formats invalides, données manquantes)
 * - L'ignoration de l'en-tête
 */
public class CsvParserServiceTest {

    private CsvParserService parserService;

    @BeforeEach
    void setUp() {
        parserService = new CsvParserService();
    }

    /**
     * Test : Parser un fichier CSV valide avec un enregistrement correct.
     */
    @Test
    void testParseValidFile(@TempDir Path tempDir) throws IOException, CsvValidationException {
        // Arrange : créer un fichier CSV valide
        String csvContent = "Numero_Securite_Sociale,Nom,Prenom,Date_Naissance,Numero_Telephone,E_Mail,ID_Remboursement,Code_Soin,Montant_Remboursement\n" +
                "12345678901234,Dupont,Jean,1990-05-15,0612345678,jean@example.com,REM001,SOIN001,150.50\n";
        
        Path csvFile = tempDir.resolve("users_20240115143000.csv");
        Files.write(csvFile, csvContent.getBytes(StandardCharsets.UTF_8));

        // Act : parser le fichier
        List<Remboursement> result = parserService.parseFile(csvFile);

        // Assert : vérifier que le parsing a réussi
        assertEquals(1, result.size());
        Remboursement r = result.get(0);
        assertEquals("12345678901234", r.getNumeroSecuriteSociale());
        assertEquals("Dupont", r.getNom());
        assertEquals("Jean", r.getPrenom());
        assertEquals(LocalDate.of(1990, 5, 15), r.getDateNaissance());
        assertEquals("0612345678", r.getNumeroTelephone());
        assertEquals("jean@example.com", r.geteMail());
        assertEquals("REM001", r.getIdRemboursement());
        assertEquals("SOIN001", r.getCodeSoin());
        assertEquals(new BigDecimal("150.50"), r.getMontantRemboursement());
        assertEquals(LocalDateTime.of(2024, 1, 15, 14, 30, 0), r.getTimestampFichier());
    }

    /**
     * Test : Parser un fichier CSV avec plusieurs enregistrements valides.
     */
    @Test
    void testParseMultipleRecords(@TempDir Path tempDir) throws IOException, CsvValidationException {
        // Arrange
        String csvContent = "Numero_Securite_Sociale,Nom,Prenom,Date_Naissance,Numero_Telephone,E_Mail,ID_Remboursement,Code_Soin,Montant_Remboursement\n" +
                "11111111111111,Alice,Martin,1985-03-20,0611111111,alice@example.com,REM001,SOIN001,100.00\n" +
                "22222222222222,Bob,Bernard,1992-07-10,0622222222,bob@example.com,REM002,SOIN002,200.50\n" +
                "33333333333333,Charlie,Charrier,1988-11-05,0633333333,charlie@example.com,REM003,SOIN003,75.25\n";
        
        Path csvFile = tempDir.resolve("users_20240115143000.csv");
        Files.write(csvFile, csvContent.getBytes(StandardCharsets.UTF_8));

        // Act
        List<Remboursement> result = parserService.parseFile(csvFile);

        // Assert
        assertEquals(3, result.size());
        assertEquals("Alice", result.get(0).getNom());
        assertEquals("Bob", result.get(1).getNom());
        assertEquals("Charlie", result.get(2).getNom());
    }

    /**
     * Test : Parser un fichier CSV avec une ligne invalide (données manquantes).
     */
    @Test
    void testParseFileWithInvalidLine(@TempDir Path tempDir) throws IOException, CsvValidationException {
        // Arrange : la deuxième ligne est incomplète
        String csvContent = "Numero_Securite_Sociale,Nom,Prenom,Date_Naissance,Numero_Telephone,E_Mail,ID_Remboursement,Code_Soin,Montant_Remboursement\n" +
                "12345678901234,Dupont,Jean,1990-05-15,0612345678,jean@example.com,REM001,SOIN001,150.50\n" +
                "22222222222222,Durand,Marie\n";
        
        Path csvFile = tempDir.resolve("users_20240115143000.csv");
        Files.write(csvFile, csvContent.getBytes(StandardCharsets.UTF_8));

        // Act
        List<Remboursement> result = parserService.parseFile(csvFile);

        // Assert : seul l'enregistrement valide est retourné
        assertEquals(1, result.size());
    }

    /**
     * Test : Parser un fichier CSV avec une date mal formatée.
     */
    @Test
    void testParseFileWithInvalidDate(@TempDir Path tempDir) throws IOException, CsvValidationException {
        // Arrange
        String csvContent = "Numero_Securite_Sociale,Nom,Prenom,Date_Naissance,Numero_Telephone,E_Mail,ID_Remboursement,Code_Soin,Montant_Remboursement\n" +
                "12345678901234,Dupont,Jean,15-05-1990,0612345678,jean@example.com,REM001,SOIN001,150.50\n";
        
        Path csvFile = tempDir.resolve("users_20240115143000.csv");
        Files.write(csvFile, csvContent.getBytes(StandardCharsets.UTF_8));

        // Act
        List<Remboursement> result = parserService.parseFile(csvFile);

        // Assert : aucun enregistrement valide
        assertEquals(0, result.size());
    }

    /**
     * Test : Parser un fichier CSV avec un montant invalide.
     */
    @Test
    void testParseFileWithInvalidAmount(@TempDir Path tempDir) throws IOException, CsvValidationException {
        // Arrange
        String csvContent = "Numero_Securite_Sociale,Nom,Prenom,Date_Naissance,Numero_Telephone,E_Mail,ID_Remboursement,Code_Soin,Montant_Remboursement\n" +
                "12345678901234,Dupont,Jean,1990-05-15,0612345678,jean@example.com,REM001,SOIN001,abc\n";
        
        Path csvFile = tempDir.resolve("users_20240115143000.csv");
        Files.write(csvFile, csvContent.getBytes(StandardCharsets.UTF_8));

        // Act
        List<Remboursement> result = parserService.parseFile(csvFile);

        // Assert : aucun enregistrement valide
        assertEquals(0, result.size());
    }

    /**
     * Test : Extraire correctement le timestamp du nom de fichier valide.
     */
    @Test
    void testTimestampExtraction(@TempDir Path tempDir) throws IOException, CsvValidationException {
        // Arrange
        String csvContent = "Numero_Securite_Sociale,Nom,Prenom,Date_Naissance,Numero_Telephone,E_Mail,ID_Remboursement,Code_Soin,Montant_Remboursement\n" +
                "12345678901234,Dupont,Jean,1990-05-15,0612345678,jean@example.com,REM001,SOIN001,150.50\n";
        
        Path csvFile = tempDir.resolve("users_20240625183045.csv");
        Files.write(csvFile, csvContent.getBytes(StandardCharsets.UTF_8));

        // Act
        List<Remboursement> result = parserService.parseFile(csvFile);

        // Assert
        assertEquals(1, result.size());
        assertEquals(LocalDateTime.of(2024, 6, 25, 18, 30, 45), result.get(0).getTimestampFichier());
    }

    /**
     * Test : Levée d'exception avec un nom de fichier invalide.
     */
    @Test
    void testInvalidFilename(@TempDir Path tempDir) throws IOException {
        // Arrange : nom de fichier ne respectant pas le format
        String csvContent = "Numero_Securite_Sociale,Nom,Prenom,Date_Naissance,Numero_Telephone,E_Mail,ID_Remboursement,Code_Soin,Montant_Remboursement\n" +
                "12345678901234,Dupont,Jean,1990-05-15,0612345678,jean@example.com,REM001,SOIN001,150.50\n";
        
        Path csvFile = tempDir.resolve("invalid_filename.csv");
        Files.write(csvFile, csvContent.getBytes(StandardCharsets.UTF_8));

        // Act & Assert : doit lever une IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> parserService.parseFile(csvFile));
    }

    /**
     * Test : Parser un fichier CSV vide (seulement l'en-tête).
     */
    @Test
    void testParseEmptyFile(@TempDir Path tempDir) throws IOException, CsvValidationException {
        // Arrange
        String csvContent = "Numero_Securite_Sociale,Nom,Prenom,Date_Naissance,Numero_Telephone,E_Mail,ID_Remboursement,Code_Soin,Montant_Remboursement\n";
        
        Path csvFile = tempDir.resolve("users_20240115143000.csv");
        Files.write(csvFile, csvContent.getBytes(StandardCharsets.UTF_8));

        // Act
        List<Remboursement> result = parserService.parseFile(csvFile);

        // Assert
        assertEquals(0, result.size());
    }

    /**
     * Test : Parser un fichier CSV avec espaces inutiles.
     */
    @Test
    void testParseFileWithWhitespace(@TempDir Path tempDir) throws IOException, CsvValidationException {
        // Arrange
        String csvContent = "Numero_Securite_Sociale,Nom,Prenom,Date_Naissance,Numero_Telephone,E_Mail,ID_Remboursement,Code_Soin,Montant_Remboursement\n" +
                "  12345678901234  ,  Dupont  ,  Jean  ,  1990-05-15  ,  0612345678  ,  jean@example.com  ,  REM001  ,  SOIN001  ,  150.50  \n";
        
        Path csvFile = tempDir.resolve("users_20240115143000.csv");
        Files.write(csvFile, csvContent.getBytes(StandardCharsets.UTF_8));

        // Act
        List<Remboursement> result = parserService.parseFile(csvFile);

        // Assert : les espaces doivent être trimés
        assertEquals(1, result.size());
        assertEquals("12345678901234", result.get(0).getNumeroSecuriteSociale());
        assertEquals("Dupont", result.get(0).getNom());
    }
}
