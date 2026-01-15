package fr.electronvert.facturation.exception;

import fr.electronvert.facturation.model.facture.Facture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception levée lors d'une tentative de clôture d'un contrat
 * qui possède encore des factures impayées.
 * <p>
 * Cette exception empêche la clôture d'un contrat tant que toutes
 * les factures n'ont pas été réglées par le client.
 * </p>
 *
 * @author Sibylle Dillmann
 */
public class ContratAvecFacturesImpayeesException extends RuntimeException {

    private final String referenceContrat;
    private final List<Facture> facturesImpayees;

    /**
     * Constructeur de l'exception.
     *
     * @param referenceContrat la référence du contrat
     * @param facturesImpayees la liste des factures impayées
     */
    public ContratAvecFacturesImpayeesException(
            String referenceContrat,
            List<Facture> facturesImpayees
    ) {
        super(construireMessage(referenceContrat, facturesImpayees));
        this.referenceContrat = referenceContrat;
        this.facturesImpayees = new ArrayList<>(facturesImpayees);
    }

    private static String construireMessage(String referenceContrat, List<Facture> factures) {
        double montantTotal = factures.stream()
                .mapToDouble(Facture::getMontantTotalTTCAPayer)
                .sum();

        return String.format(
                "Impossible de clôturer le contrat %s : %d facture(s) impayée(s) pour un total de %.2f €",
                referenceContrat,
                factures.size(),
                montantTotal
        );
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
     * Retourne la liste des factures impayées.
     *
     * @return une liste non modifiable des factures impayées
     */
    public List<Facture> getFacturesImpayees() {
        return Collections.unmodifiableList(facturesImpayees);
    }

    /**
     * Retourne le nombre de factures impayées.
     *
     * @return le nombre de factures
     */
    public int getNombreFacturesImpayees() {
        return facturesImpayees.size();
    }

    /**
     * Retourne le montant total des impayés.
     *
     * @return la somme des montants de toutes les factures impayées
     */
    public double getMontantTotalImpaye() {
        return facturesImpayees.stream()
                .mapToDouble(Facture::getMontantTotalTTCAPayer)
                .sum();
    }
}