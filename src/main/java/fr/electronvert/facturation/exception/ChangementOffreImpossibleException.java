package fr.electronvert.facturation.exception;

/**
 * Exception levée lors d'une tentative de changement d'offre tarifaire impossible.
 * <p>
 * Les cas possibles incluent :
 * - Changement vers l'offre déjà active
 * - Offre tarifaire incompatible???
 * </p>
 *
 * @author Sibylle Dillmann
 */
public class ChangementOffreImpossibleException extends RuntimeException {

    private final String referenceContrat;
    private final String offreActuelle;
    private final String offreDemandee;
    private final String raison;

    /**
     * Constructeur de l'exception.
     *
     * @param referenceContrat la référence du contrat
     * @param offreActuelle le nom de l'offre actuellement active
     * @param offreDemandee le nom de l'offre demandée
     * @param raison la raison du refus
     */
    public ChangementOffreImpossibleException(
            String referenceContrat,
            String offreActuelle,
            String offreDemandee,
            String raison
    ) {
        super(construireMessage(referenceContrat, offreActuelle, offreDemandee, raison));
        this.referenceContrat = referenceContrat;
        this.offreActuelle = offreActuelle;
        this.offreDemandee = offreDemandee;
        this.raison = raison;
    }

    /**
     * Constructeur simplifié pour changement vers la même offre.
     *
     * @param referenceContrat la référence du contrat
     * @param nomOffre le nom de l'offre (actuelle = demandée)
     */
    public ChangementOffreImpossibleException(String referenceContrat, String nomOffre) {
        this(referenceContrat, nomOffre, nomOffre, "Cette offre est déjà active");
    }

    private static String construireMessage(
            String referenceContrat,
            String offreActuelle,
            String offreDemandee,
            String raison
    ) {
        if (offreActuelle.equals(offreDemandee)) {
            return "Impossible de changer l'offre du contrat " + referenceContrat +
                    " : l'offre " + offreActuelle + " est déjà active";
        }

        return "Impossible de changer du contrat " + referenceContrat +
                " de l'offre " + offreActuelle + " vers " + offreDemandee +
                " : " + raison;
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
     * Retourne le nom de l'offre actuellement active.
     *
     * @return le nom de l'offre actuelle
     */
    public String getOffreActuelle() {
        return offreActuelle;
    }

    /**
     * Retourne le nom de l'offre demandée.
     *
     * @return le nom de l'offre demandée
     */
    public String getOffreDemandee() {
        return offreDemandee;
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
     * Vérifie si la demande concerne la même offre.
     *
     * @return true si offre actuelle = offre demandée
     */
    public boolean estMemeOffre() {
        return offreActuelle.equals(offreDemandee);
    }
}