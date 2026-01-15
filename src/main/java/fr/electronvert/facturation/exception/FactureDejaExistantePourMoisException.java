package fr.electronvert.facturation.exception;

import fr.electronvert.facturation.model.facture.TypeFacture;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Exception levée lors d'une tentative de création d'une facture mensuelle
 * alors qu'une facture existe déjà pour ce contrat et ce mois.
 * <p>
 * Cette exception empêche la double facturation d'un même mois.
 * </p>
 *
 * @author Sibylle Dillmann
 */
public class FactureDejaExistantePourMoisException extends RuntimeException {

    private final String referenceContrat;
    private final TypeFacture typeFacture;
    private final LocalDate mois;
    private final String factureExistanteReference;

    private static final DateTimeFormatter FORMAT_MOIS = DateTimeFormatter.ofPattern("MMMM yyyy");

    /**
     * Constructeur de l'exception.
     *
     * @param referenceContrat la référence du contrat
     * @param typeFacture le type de facture (MENSUELLE, REGULARISATION)
     * @param mois le mois concerné (1er du mois)
     * @param factureExistanteReference la référence de la facture existante
     */
    public FactureDejaExistantePourMoisException(
            String referenceContrat,
            TypeFacture typeFacture,
            LocalDate mois,
            String factureExistanteReference
    ) {
        super(construireMessage(referenceContrat, typeFacture, mois, factureExistanteReference));
        this.referenceContrat = referenceContrat;
        this.typeFacture = typeFacture;
        this.mois = mois;
        this.factureExistanteReference = factureExistanteReference;
    }

    /**
     * Constructeur simplifié sans référence de facture existante.
     */
    public FactureDejaExistantePourMoisException(
            String referenceContrat,
            TypeFacture typeFacture,
            LocalDate mois
    ) {
        this(referenceContrat, typeFacture, mois, null);
    }

    private static String construireMessage(
            String referenceContrat,
            TypeFacture typeFacture,
            LocalDate mois,
            String reference
    ) {
        String msg = "Une facture " + typeFacture.name().toLowerCase() +
                " existe déjà pour le contrat " + referenceContrat +
                " pour le mois de " + mois.format(FORMAT_MOIS);

        if (reference != null) {
            msg += " (Référence: " + reference + ")";
        }

        return msg;
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
     * Retourne le type de facture.
     *
     * @return le type (MENSUELLE ou REGULARISATION)
     */
    public TypeFacture getTypeFacture() {
        return typeFacture;
    }

    /**
     * Retourne le mois concerné.
     *
     * @return le premier jour du mois concerné
     */
    public LocalDate getMois() {
        return mois;
    }

    /**
     * Retourne la référence de la facture existante.
     *
     * @return la référence, ou null si non disponible
     */
    public String getFactureExistanteReference() {
        return factureExistanteReference;
    }
}