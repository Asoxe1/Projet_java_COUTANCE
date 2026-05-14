package com.aubert_coutance.batch.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsable de la gestion des connexions à la base de données PostgreSQL.
 * Cette classe encapsule la création et la configuration des objets Connection
 * utilisés pour communiquer avec la base de données relationnelle.
 */
public class DatabaseConfig {

    private final AppProperties appProperties;

    /**
     * Constructeur : initialise la configuration avec les propriétés de l'application.
     * 
     * @param appProperties Configuration chargée depuis database.properties
     */
    public DatabaseConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    /**
     * Établit et retourne une connexion à la base de données PostgreSQL.
     * La connexion est établie en utilisant les identifiants fournis par AppProperties.
     * 
     * @return Une connexion active à la base de données PostgreSQL
     * @throws SQLException Si la connexion échoue (serveur indisponible, identifiants invalides, etc.)
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                appProperties.getDbUrl(),
                appProperties.getDbUser(),
                appProperties.getDbPassword()
        );
    }
}
