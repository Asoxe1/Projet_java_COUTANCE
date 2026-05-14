package com.aubert_coutance.batch.dao;

import com.aubert_coutance.batch.model.Remboursement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe DAO (Data Access Object) responsable de la gestion de la persistance des remboursements.
 * 
 * Cette classe :
 * - Exécute les opérations de base de données (INSERT/UPDATE)
 * - Gère les transactions
 * - Utilise les prepared statements pour éviter les injections SQL
 * - Implémente le pattern UPSERT (INSERT OR UPDATE)
 */
public class RemboursementDao {

    private static final Logger LOGGER = Logger.getLogger(RemboursementDao.class.getName());
    private final Connection connection;

    /**
     * Constructeur : initialise le DAO avec une connexion à la base de données.
     * 
     * @param connection Connexion à la base de données PostgreSQL
     */
    public RemboursementDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insère ou met à jour une liste de remboursements dans la base de données.
     * 
     * Cette méthode utilise la clause PostgreSQL "ON CONFLICT ... DO UPDATE" pour :
     * - Insérer les nouveaux enregistrements (ID remboursement inexistant)
     * - Mettre à jour les enregistrements existants (ID remboursement existant)
     * 
     * L'ID remboursement est la clé primaire et détermine le comportement.
     * 
     * Les données sont insérées par lot (batch) pour optimiser les performances.
     * 
     * @param remboursements La liste des remboursements à persister
     * @throws SQLException En cas d'erreur de communication avec la base de données
     */
    public void upsert(List<Remboursement> remboursements) throws SQLException {
        // Requête SQL avec clause ON CONFLICT pour gérer les doublons
        String sql = "INSERT INTO remboursement (" +
                "numero_securite_sociale, nom, prenom, date_naissance, " +
                "numero_telephone, e_mail, id_remboursement, code_soin, " +
                "montant_remboursement, timestamp_fichier" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id_remboursement) DO UPDATE SET " +
                "numero_securite_sociale = EXCLUDED.numero_securite_sociale, " +
                "nom = EXCLUDED.nom, " +
                "prenom = EXCLUDED.prenom, " +
                "date_naissance = EXCLUDED.date_naissance, " +
                "numero_telephone = EXCLUDED.numero_telephone, " +
                "e_mail = EXCLUDED.e_mail, " +
                "code_soin = EXCLUDED.code_soin, " +
                "montant_remboursement = EXCLUDED.montant_remboursement, " +
                "timestamp_fichier = EXCLUDED.timestamp_fichier";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            int totalCount = 0;
            
            // Remplir les paramètres pour chaque remboursement
            for (Remboursement r : remboursements) {
                pstmt.setString(1, r.getNumeroSecuriteSociale());
                pstmt.setString(2, r.getNom());
                pstmt.setString(3, r.getPrenom());
                pstmt.setDate(4, java.sql.Date.valueOf(r.getDateNaissance()));
                pstmt.setString(5, r.getNumeroTelephone());
                pstmt.setString(6, r.geteMail());
                pstmt.setString(7, r.getIdRemboursement());
                pstmt.setString(8, r.getCodeSoin());
                pstmt.setBigDecimal(9, r.getMontantRemboursement());
                pstmt.setTimestamp(10, Timestamp.valueOf(r.getTimestampFichier()));
                
                // Ajouter à la suite de commandes pour exécution en lot
                pstmt.addBatch();
                totalCount++;
            }
            
            // Exécuter toutes les commandes en une seule requête
            int[] results = pstmt.executeBatch();
            LOGGER.info("Upsert exécuté : " + results.length + " ligne(s) affectée(s).");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'upsert de " + remboursements.size() + " remboursement(s)", e);
            throw e;
        }
    }
}