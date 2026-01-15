package fr.electronvert.facturation.exception;

import java.time.LocalDate;

/**
 * Exception levée lors d'une tentative de paiement d'une facture déjà payée.
 * <p>
 * Cette exception permet de signaler un paiement en double tout en conservant
 * des informations utiles pour l'affichage ou le journalisation :
 * <ul>
 *   <li>la référence de la facture</li>
 *   <li>la date du paiement initial</li>
 * </ul>
 * </p>
 *
 * @author Sibylle Dillmann
 */
public class FactureDejaPayeeException extends RuntimeException {

    /**
     * Référence unique de la facture déjà payée.
     */
    private final String referenceFacture;

    /**
     * Date du paiement initial.
     * Elle peut être null si l'information n'est pas disponible.
     */
    private final LocalDate datePaiementInitial;

    /**
     * Constructeur de l'exception.
     *
     * @param referenceFacture la référence de la facture (non nulle)
     * @param datePaiementInitial la date du paiement initial (peut être null)
     */
    public FactureDejaPayeeException(String referenceFacture, LocalDate datePaiementInitial) {
        super(construireMessage(referenceFacture, datePaiementInitial));
        this.referenceFacture = referenceFacture;
        this.datePaiementInitial = datePaiementInitial;
    }

    private static String construireMessage(String referenceFacture, LocalDate datePaiementInitial) {
        if (datePaiementInitial != null) {
            return "La facture " + referenceFacture +
                    " est déjà payée (paiement effectué le " + datePaiementInitial + ")";
        }
        return "La facture " + referenceFacture + " est déjà payée";
    }

    public String getReferenceFacture() {
        return referenceFacture;
    }

    public LocalDate getDatePaiementInitial() {
        return datePaiementInitial;
    }
}
