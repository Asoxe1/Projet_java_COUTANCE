package com.aubert_coutance.batch.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modèle de données représentant un enregistrement de remboursement.
 * 
 * Cette classe encapsule tous les attributs d'une demande de remboursement :
 * - Informations personnelles du patient (nom, prénom, date de naissance)
 * - Coordonnées (téléphone, email)
 * - Détails médicaux (code soin, montant)
 * - Identifiants techniques (numéro de sécurité sociale, ID remboursement, timestamp)
 * 
 * L'ID remboursement sert de clé primaire et permet de déterminer
 * si un enregistrement est nouveau (INSERT) ou existant (UPDATE).
 */
public class Remboursement {

    private String numeroSecuriteSociale;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String numeroTelephone;
    private String eMail;
    private String idRemboursement;
    private String codeSoin;
    private BigDecimal montantRemboursement;
    private LocalDateTime timestampFichier;

    /**
     * Constructeur par défaut - initialise tous les champs à null.
     */
    public Remboursement() {
    }

    /**
     * Récupère le numéro de sécurité sociale du patient.
     * @return Numéro de sécurité sociale
     */
    public String getNumeroSecuriteSociale() {
        return numeroSecuriteSociale;
    }

    /**
     * Définit le numéro de sécurité sociale du patient.
     * @param numeroSecuriteSociale Numéro de sécurité sociale à assigner
     */
    public void setNumeroSecuriteSociale(String numeroSecuriteSociale) {
        this.numeroSecuriteSociale = numeroSecuriteSociale;
    }

    /**
     * Récupère le nom du patient.
     * @return Nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom du patient.
     * @param nom Nom à assigner
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Récupère le prénom du patient.
     * @return Prénom
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Définit le prénom du patient.
     * @param prenom Prénom à assigner
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * Récupère la date de naissance du patient.
     * @return Date de naissance
     */
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    /**
     * Définit la date de naissance du patient.
     * @param dateNaissance Date de naissance à assigner
     */
    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    /**
     * Récupère le numéro de téléphone du patient.
     * @return Numéro de téléphone
     */
    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    /**
     * Définit le numéro de téléphone du patient.
     * @param numeroTelephone Numéro de téléphone à assigner
     */
    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    /**
     * Récupère l'adresse email du patient.
     * @return Adresse email
     */
    public String geteMail() {
        return eMail;
    }

    /**
     * Définit l'adresse email du patient.
     * @param eMail Adresse email à assigner
     */
    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    /**
     * Récupère l'identifiant de remboursement (clé primaire).
     * @return ID remboursement
     */
    public String getIdRemboursement() {
        return idRemboursement;
    }

    /**
     * Définit l'identifiant de remboursement (clé primaire).
     * @param idRemboursement ID remboursement à assigner
     */
    public void setIdRemboursement(String idRemboursement) {
        this.idRemboursement = idRemboursement;
    }

    /**
     * Récupère le code du soin médical.
     * @return Code soin
     */
    public String getCodeSoin() {
        return codeSoin;
    }

    /**
     * Définit le code du soin médical.
     * @param codeSoin Code soin à assigner
     */
    public void setCodeSoin(String codeSoin) {
        this.codeSoin = codeSoin;
    }

    /**
     * Récupère le montant du remboursement.
     * @return Montant en euros (BigDecimal avec 2 décimales)
     */
    public BigDecimal getMontantRemboursement() {
        return montantRemboursement;
    }

    /**
     * Définit le montant du remboursement.
     * @param montantRemboursement Montant en euros à assigner
     */
    public void setMontantRemboursement(BigDecimal montantRemboursement) {
        this.montantRemboursement = montantRemboursement;
    }

    /**
     * Récupère le timestamp du fichier source (extrait du nom du fichier).
     * @return Timestamp du fichier (format YYYYMMDDHHmmss)
     */
    public LocalDateTime getTimestampFichier() {
        return timestampFichier;
    }

    /**
     * Définit le timestamp du fichier source.
     * @param timestampFichier Timestamp à assigner
     */
    public void setTimestampFichier(LocalDateTime timestampFichier) {
        this.timestampFichier = timestampFichier;
    }

    /**
     * Compare deux objets Remboursement en fonction de leur ID.
     * Deux remboursements ayant le même ID sont considérés comme égaux.
     * 
     * @param o Objet à comparer
     * @return true si l'ID remboursement est identique, false sinon
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Remboursement that = (Remboursement) o;
        return Objects.equals(idRemboursement, that.idRemboursement);
    }

    /**
     * Calcule le code de hachage basé sur l'ID remboursement.
     * 
     * @return Code de hachage
     */
    @Override
    public int hashCode() {
        return Objects.hash(idRemboursement);
    }

    /**
     * Retourne une représentation textuelle de l'objet Remboursement.
     * 
     * @return Description textuelle lisible
     */
    @Override
    public String toString() {
        return "Remboursement{" +
                "numeroSecuriteSociale='" + numeroSecuriteSociale + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", numeroTelephone='" + numeroTelephone + '\'' +
                ", eMail='" + eMail + '\'' +
                ", idRemboursement='" + idRemboursement + '\'' +
                ", codeSoin='" + codeSoin + '\'' +
                ", montantRemboursement=" + montantRemboursement +
                ", timestampFichier=" + timestampFichier +
                '}';
    }
}
