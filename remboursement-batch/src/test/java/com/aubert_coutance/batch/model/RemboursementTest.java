package com.aubert_coutance.batch.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Remboursement.
 * 
 * Ces tests vérifient :
 * - La création et l'initialisation des objets
 * - Les getters et setters
 * - Les méthodes equals() et hashCode()
 * - La représentation textuelle
 */
public class RemboursementTest {

    private Remboursement remboursement;

    @BeforeEach
    void setUp() {
        remboursement = new Remboursement();
    }

    /**
     * Test : Vérifier que le constructeur crée un objet avec des valeurs nulles.
     */
    @Test
    void testDefaultConstructor() {
        assertNull(remboursement.getNumeroSecuriteSociale());
        assertNull(remboursement.getNom());
        assertNull(remboursement.getPrenom());
        assertNull(remboursement.getDateNaissance());
        assertNull(remboursement.getNumeroTelephone());
        assertNull(remboursement.geteMail());
        assertNull(remboursement.getIdRemboursement());
        assertNull(remboursement.getCodeSoin());
        assertNull(remboursement.getMontantRemboursement());
        assertNull(remboursement.getTimestampFichier());
    }

    /**
     * Test : Vérifier les setters et getters pour tous les champs.
     */
    @Test
    void testSettersAndGetters() {
        // Arrange & Act
        remboursement.setNumeroSecuriteSociale("12345678901234");
        remboursement.setNom("Dupont");
        remboursement.setPrenom("Jean");
        remboursement.setDateNaissance(LocalDate.of(1990, 5, 15));
        remboursement.setNumeroTelephone("0612345678");
        remboursement.seteMail("jean@example.com");
        remboursement.setIdRemboursement("REM001");
        remboursement.setCodeSoin("SOIN001");
        remboursement.setMontantRemboursement(new BigDecimal("150.50"));
        remboursement.setTimestampFichier(LocalDateTime.of(2024, 1, 15, 14, 30, 0));

        // Assert
        assertEquals("12345678901234", remboursement.getNumeroSecuriteSociale());
        assertEquals("Dupont", remboursement.getNom());
        assertEquals("Jean", remboursement.getPrenom());
        assertEquals(LocalDate.of(1990, 5, 15), remboursement.getDateNaissance());
        assertEquals("0612345678", remboursement.getNumeroTelephone());
        assertEquals("jean@example.com", remboursement.geteMail());
        assertEquals("REM001", remboursement.getIdRemboursement());
        assertEquals("SOIN001", remboursement.getCodeSoin());
        assertEquals(new BigDecimal("150.50"), remboursement.getMontantRemboursement());
        assertEquals(LocalDateTime.of(2024, 1, 15, 14, 30, 0), remboursement.getTimestampFichier());
    }

    /**
     * Test : Vérifier l'égalité basée sur l'ID remboursement.
     */
    @Test
    void testEqualsBasedOnId() {
        // Arrange
        Remboursement r1 = new Remboursement();
        r1.setIdRemboursement("REM001");
        r1.setNom("Dupont");

        Remboursement r2 = new Remboursement();
        r2.setIdRemboursement("REM001");
        r2.setNom("Martin"); // Nom différent, ne devrait pas affecter l'égalité

        Remboursement r3 = new Remboursement();
        r3.setIdRemboursement("REM002");
        r3.setNom("Dupont"); // Même nom que r1, mais ID différent

        // Act & Assert
        assertEquals(r1, r2); // Même ID -> égaux
        assertNotEquals(r1, r3); // ID différent -> pas égaux
    }

    /**
     * Test : Vérifier que deux objets distincts avec le même ID sont considérés comme égaux.
     */
    @Test
    void testEqualsSameId() {
        // Arrange
        remboursement.setIdRemboursement("REM001");
        Remboursement other = new Remboursement();
        other.setIdRemboursement("REM001");

        // Act & Assert
        assertTrue(remboursement.equals(other));
        assertTrue(other.equals(remboursement)); // Symétrie
    }

    /**
     * Test : Vérifier que un objet n'est pas égal à null.
     */
    @Test
    void testNotEqualToNull() {
        remboursement.setIdRemboursement("REM001");
        assertNotEquals(remboursement, null);
    }

    /**
     * Test : Vérifier que un objet est égal à lui-même.
     */
    @Test
    void testEqualToItself() {
        remboursement.setIdRemboursement("REM001");
        assertEquals(remboursement, remboursement);
    }

    /**
     * Test : Vérifier le hashCode basé sur l'ID.
     */
    @Test
    void testHashCodeBasedOnId() {
        // Arrange
        Remboursement r1 = new Remboursement();
        r1.setIdRemboursement("REM001");

        Remboursement r2 = new Remboursement();
        r2.setIdRemboursement("REM001");

        Remboursement r3 = new Remboursement();
        r3.setIdRemboursement("REM002");

        // Act & Assert
        assertEquals(r1.hashCode(), r2.hashCode()); // Même ID -> même hashCode
        assertNotEquals(r1.hashCode(), r3.hashCode()); // ID différent -> hashCode différent (généralement)
    }

    /**
     * Test : Vérifier la méthode toString().
     */
    @Test
    void testToString() {
        // Arrange
        remboursement.setNumeroSecuriteSociale("12345678901234");
        remboursement.setNom("Dupont");
        remboursement.setPrenom("Jean");
        remboursement.setIdRemboursement("REM001");

        // Act
        String result = remboursement.toString();

        // Assert
        assertTrue(result.contains("12345678901234"));
        assertTrue(result.contains("Dupont"));
        assertTrue(result.contains("Jean"));
        assertTrue(result.contains("REM001"));
        assertTrue(result.contains("Remboursement{"));
    }

    /**
     * Test : Vérifier que deux objets avec des ID nulls sont égaux.
     */
    @Test
    void testEqualsWithNullIds() {
        // Arrange
        Remboursement r1 = new Remboursement(); // ID = null
        Remboursement r2 = new Remboursement(); // ID = null

        // Act & Assert
        assertEquals(r1, r2); // Tous les deux ont ID = null
    }
}
