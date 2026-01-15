package fr.electronvert.facturation.exception;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Exception levée lors d'une tentative d'opération sur un contrat inactif/cloturé.
 * <p>
 * Cette exception est levée lorsqu'on tente d'effectuer une opération
 * (facturation, génération de relevé, etc.) sur un contrat qui est clôturé.
 * </p>
 *
 * @author Sibylle Dillmann
 */
public class ContratInactifException extends RuntimeException {

    private final String referenceContrat;
    private final LocalDate dateCloture;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy");

    /**
     * Constructeur de l'exception.
     *
     * @param referenceContrat la référence du contrat inactif
     * @param dateCloture la date de clôture du contrat (peut être null si non clôturé)
     */
    public ContratInactifException(String referenceContrat, LocalDate dateCloture) {
        super(construireMessage(referenceContrat, dateCloture));
        this.referenceContrat = referenceContrat;
        this.dateCloture = dateCloture;
    }

    /**
     * Constructeur simplifié sans date de clôture.
     *
     * @param referenceContrat la référence du contrat inactif
     */
    public ContratInactifException(String referenceContrat) {
        this(referenceContrat, null);
    }

    private static String construireMessage(String referenceContrat, LocalDate dateCloture) {
        if (dateCloture != null) {
            return "Le contrat " + referenceContrat + " est inactif depuis le " +
                    dateCloture.format(FORMATTER);
        }
        return "Le contrat " + referenceContrat + " est inactif";
    }

    /**
     * Retourne la référence du contrat inactif.
     *
     * @return la référence du contrat (ex: CTR-2025-000123)
     */
    public String getReferenceContrat() {
        return referenceContrat;
    }

    /**
     * Retourne la date de clôture du contrat.
     *
     * @return la date de clôture, ou null si non disponible
     */
    public LocalDate getDateCloture() {
        return dateCloture;
    }
}