package com.aubert_coutance.batch.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe AppProperties.
 * 
 * Ces tests vérifient :
 * - Le chargement correct des propriétés depuis le fichier database.properties
 * - L'accès aux différentes propriétés de configuration
 */
public class AppPropertiesTest {

    /**
     * Test : Vérifier que les propriétés sont chargées correctement.
     */
    @Test
    void testPropertiesLoaded() {
        // Act
        AppProperties props = new AppProperties();

        // Assert : vérifier que les propriétés ne sont pas nulles
        assertNotNull(props.getDbUrl());
        assertNotNull(props.getDbUser());
        assertNotNull(props.getDbPassword());
        assertNotNull(props.getSourceDirectory());
        assertNotNull(props.getArchiveDirectory());
    }

    /**
     * Test : Vérifier les valeurs des propriétés correspondent au fichier database.properties.
     */
    @Test
    void testPropertyValues() {
        // Act
        AppProperties props = new AppProperties();

        // Assert : vérifier les valeurs attendues
        assertTrue(props.getDbUrl().contains("postgresql"));
        assertTrue(props.getDbUrl().contains("localhost"));
        assertEquals("postgres", props.getDbUser());
    }

    /**
     * Test : Vérifier que les propriétés de répertoires ne sont pas vides.
     */
    @Test
    void testDirectoryProperties() {
        // Act
        AppProperties props = new AppProperties();

        // Assert
        assertFalse(props.getSourceDirectory().isEmpty());
        assertFalse(props.getArchiveDirectory().isEmpty());
        assertNotEquals(props.getSourceDirectory(), props.getArchiveDirectory());
    }
}
