package fr.electronvert.facturation.exception;

/**
 * Exception levée lors d'une tentative de facturation
 * alors qu'il n'y a pas assez de relevés disponibles pour pouvoir générer une facture.
 * <p>
 * Pour générer une facture il faut au minimum deux relevés se suivant chronologiquement.
 * </p>
 *
 * @author Sibylle Dillmann
 */
public class RelevesInsuffisantsException extends RuntimeException {

    private final String referenceContrat;
    private final int nbRelevesDisponibles;
    private final int nbRelevesRequis;

    /**
     * Constructeur de l'exception.
     *
     * @param referenceContrat la référence du contrat
     * @param nbRelevesDisponibles le nombre de relevés actuellement disponibles
     * @param nbRelevesRequis le nombre de relevés requis pour l'opération
     */
    public RelevesInsuffisantsException(
            String referenceContrat,
            int nbRelevesDisponibles,
            int nbRelevesRequis
    ) {
        super(construireMessage(referenceContrat, nbRelevesDisponibles, nbRelevesRequis));
        this.referenceContrat = referenceContrat;
        this.nbRelevesDisponibles = nbRelevesDisponibles;
        this.nbRelevesRequis = nbRelevesRequis;
    }

    /**
     * Constructeur simplifié avec 2 relevés requis par défaut.
     *
     * @param referenceContrat la référence du contrat
     * @param nbRelevesDisponibles le nombre de relevés disponibles
     */
    public RelevesInsuffisantsException(String referenceContrat, int nbRelevesDisponibles) {
        this(referenceContrat, nbRelevesDisponibles, 2);
    }

    private static String construireMessage(
            String referenceContrat,
            int nbDisponibles,
            int nbRequis
    ) {
        return "Impossible de facturer le contrat " + referenceContrat + " : " +
                nbDisponibles + " relevé(s) disponible(s), " +
                nbRequis + " requis";
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
     * Retourne le nombre de relevés actuellement disponibles.
     *
     * @return le nombre de relevés disponibles
     */
    public int getNbRelevesDisponibles() {
        return nbRelevesDisponibles;
    }

    /**
     * Retourne le nombre de relevés requis.
     *
     * @return le nombre de relevés nécessaires
     */
    public int getNbRelevesRequis() {
        return nbRelevesRequis;
    }

    /**
     * Retourne le nombre de relevés manquants.
     *
     * @return la différence entre requis et disponibles
     */
    public int getNbRelevesManquants() {
        return Math.max(0, nbRelevesRequis - nbRelevesDisponibles);
    }
}