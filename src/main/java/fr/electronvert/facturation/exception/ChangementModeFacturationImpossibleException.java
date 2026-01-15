package fr.electronvert.facturation.exception;

import fr.electronvert.facturation.model.contrat.ModeFacturation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Exception levée lors d'une tentative de changement de mode de facturation impossible.
 * <p>
 * Les cas possibles incluent :
 * - Changement vers le même mode de facturation
 * - Changement hors période autorisée (doit être demandé le mois précédant la date anniversaire)
 * </p>
 *
 * @author Sibylle Dillmann
 */
public class ChangementModeFacturationImpossibleException extends RuntimeException {

    private final String referenceContrat;
    private final ModeFacturation modeActuel;
    private final ModeFacturation modeDemande;
    private final String raison;
    private final LocalDate prochaineeDateAutorisee;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy");

    /**
     * Constructeur complet de l'exception.
     *
     * @param referenceContrat la référence du contrat
     * @param modeActuel le mode de facturation actuellement actif
     * @param modeDemande le mode de facturation demandé
     * @param raison la raison du refus
     * @param prochaineeDateAutorisee la prochaine date où le changement sera possible (peut être null)
     */
    public ChangementModeFacturationImpossibleException(
            String referenceContrat,
            ModeFacturation modeActuel,
            ModeFacturation modeDemande,
            String raison,
            LocalDate prochaineeDateAutorisee
    ) {
        super(construireMessage(referenceContrat, modeActuel, modeDemande, raison, prochaineeDateAutorisee));
        this.referenceContrat = referenceContrat;
        this.modeActuel = modeActuel;
        this.modeDemande = modeDemande;
        this.raison = raison;
        this.prochaineeDateAutorisee = prochaineeDateAutorisee;
    }

    /**
     * Constructeur simplifié pour changement vers le même mode.
     *
     * @param referenceContrat la référence du contrat
     * @param mode le mode (actuel = demandé)
     */
    public ChangementModeFacturationImpossibleException(String referenceContrat, ModeFacturation mode) {
        this(referenceContrat, mode, mode, "Ce mode de facturation est déjà actif", null);
    }

    /**
     * Constructeur pour changement hors période autorisée.
     *
     * @param referenceContrat la référence du contrat
     * @param modeActuel le mode actuel
     * @param modeDemande le mode demandé
     * @param prochaineeDateAutorisee la prochaine date autorisée
     */
    public ChangementModeFacturationImpossibleException(
            String referenceContrat,
            ModeFacturation modeActuel,
            ModeFacturation modeDemande,
            LocalDate prochaineeDateAutorisee
    ) {
        this(
                referenceContrat,
                modeActuel,
                modeDemande,
                "Le changement doit être demandé le mois précédant la date anniversaire du contrat",
                prochaineeDateAutorisee
        );
    }

    private static String construireMessage(
            String referenceContrat,
            ModeFacturation modeActuel,
            ModeFacturation modeDemande,
            String raison,
            LocalDate prochaineDate
    ) {
        StringBuilder msg = new StringBuilder();

        if (modeActuel == modeDemande) {
            msg.append("Impossible de changer le mode de facturation du contrat ")
                    .append(referenceContrat)
                    .append(" : le mode ")
                    .append(modeActuel)
                    .append(" est déjà actif");
        } else {
            msg.append("Impossible de changer le mode de facturation du contrat ")
                    .append(referenceContrat)
                    .append(" de ")
                    .append(modeActuel)
                    .append(" vers ")
                    .append(modeDemande)
                    .append(" : ")
                    .append(raison);
        }

        if (prochaineDate != null) {
            msg.append(". Prochaine date autorisée : ")
                    .append(prochaineDate.format(FORMATTER));
        }

        return msg.toString();
    }

    /**
     * Retourne la référence du contrat.
     *
     * @return la référence du contrat (ex: CTR-2025-000123)
     */
    public String getReferenceContrat() {
        return referenceContrat;
    }

    /**
     * Retourne le mode de facturation actuellement actif.
     *
     * @return le mode actuel (REEL ou ECHEANCIER)
     */
    public ModeFacturation getModeActuel() {
        return modeActuel;
    }

    /**
     * Retourne le mode de facturation demandé.
     *
     * @return le mode demandé (REEL ou ECHEANCIER)
     */
    public ModeFacturation getModeDemande() {
        return modeDemande;
    }

    /**
     * Retourne la raison du refus.
     *
     * @return la raison textuelle
     */
    public String getRaison() {
        return raison;
    }

    /**
     * Retourne la prochaine date autorisée pour un changement.
     *
     * @return la prochaine date autorisée, ou null si non applicable
     */
    public LocalDate getProchaineeDateAutorisee() {
        return prochaineeDateAutorisee;
    }

    /**
     * Vérifie si la demande concerne le même mode.
     *
     * @return true si mode actuel = mode demandé
     */
    public boolean estMemeMode() {
        return modeActuel == modeDemande;
    }
}