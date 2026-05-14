package com.aubert_coutance.batch.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Classe responsable de charger et fournir les propriétés de configuration de l'application.
 * Cette classe lit le fichier database.properties qui contient les paramètres de connexion
 * à la base de données ainsi que les répertoires sources et d'archive du batch.
 * 
 * Les propriétés chargées incluent :
 * - db.url : URL de connexion PostgreSQL
 * - db.user : Utilisateur PostgreSQL
 * - db.password : Mot de passe PostgreSQL
 * - batch.dossier.source : Répertoire source des fichiers CSV
 * - batch.dossier.archive : Répertoire d'archivage des fichiers traités
 */
public class AppProperties {

    private static final String PROPERTIES_FILE = "database.properties";
    private final Properties properties;

    /**
     * Constructeur : charge automatiquement les propriétés depuis le fichier de configuration.
     * 
     * @throws IllegalStateException Si le fichier de propriétés n'est pas trouvé ou ne peut pas être chargé
     */
    public AppProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Impossible de trouver le fichier : " + PROPERTIES_FILE);
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new IllegalStateException("Erreur lors du chargement du fichier de propriétés", ex);
        }
    }

    /**
     * Récupère l'URL de connexion à la base de données PostgreSQL.
     * 
     * @return URL JDBC (ex: jdbc:postgresql://localhost:5432/postgres)
     */
    public String getDbUrl() {
        return properties.getProperty("db.url");
    }

    /**
     * Récupère le nom d'utilisateur PostgreSQL.
     * 
     * @return Nom d'utilisateur pour la connexion
     */
    public String getDbUser() {
        return properties.getProperty("db.user");
    }

    /**
     * Récupère le mot de passe PostgreSQL.
     * 
     * @return Mot de passe pour la connexion
     */
    public String getDbPassword() {
        return properties.getProperty("db.password");
    }

    /**
     * Récupère le répertoire source où se trouvent les fichiers CSV à traiter.
     * 
     * @return Chemin du répertoire source
     */
    public String getSourceDirectory() {
        return properties.getProperty("batch.dossier.source");
    }

    /**
     * Récupère le répertoire où les fichiers traités sont archivés.
     * 
     * @return Chemin du répertoire d'archive
     */
    public String getArchiveDirectory() {
        return properties.getProperty("batch.dossier.archive");
    }
}
