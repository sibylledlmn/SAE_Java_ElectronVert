package fr.electronvert.facturation.model.facture;


/**
 * Énumération des taux de TVA applicables à la facturation d'électricité.
 * Deux taux de TVA peuvent être appliqués :
 * <ul>
 * <li>Taux réduit de 5,5%
 * <li>Taux normal de 20%
 * </ul>
 * Depuis le 1er aout 2025 le taux normal est apppliqué pour l'abonnement et la consommation électrique.
 * Nous gardons quand même le taux réduit dans l'énumération en cas de changements ultérieurs.
 * @author Sibylle Dillmann
 */

public enum TauxTVA {

    /**
     * Taux de TVA normal de 20%
     */
    NORMAL(20.0),
    /**
     * Taux de TVA réduit de 5,5%
     */
    REDUIT(5.5);

    /**
     * Valeur du taux de TVA en pourcentage.
     */
    private final double taux;

    /**
     * Constructeur privé de l'énumération.
     *
     * @param valeur le taux de TVA en pourcentage
     */
    TauxTVA(double valeur) {
        this.taux = valeur;
    }


    /**
     * Retourne le taux de TVA en pourcentage.
     *
     * @return le taux de TVA (5.5 ou 20.0)
     */
    public double getTaux() {
        return this.taux;
    }

    /**
     * Calcule le montant de TVA à partir d'un montant HT.
     * <p>
     * Formule utilisée : montantTVA = montantHT × (taux/100)
     * </p>
     *
     * @param montantHT le montant hors taxes en euros
     * @return le montant de la TVA en euros
     * @throws IllegalArgumentException si le montant HT est négatif
     */
    public double calculerMontantTVA(double montantHT) {
        if (montantHT < 0) {
            throw new IllegalArgumentException("Le montant HT ne peut pas être négatif");
        }
        return montantHT * (this.taux / 100);
    }

    /**
     * Calcule le montant TTC à partir d'un montant HT en appliquant le taux de TVA.
     * <p>
     * Formule utilisée : montantTTC = montantHT × (1 + taux/100)
     * </p>
     * @param montantHT le montant hors taxes en euros
     * @return le montant toutes taxes comprises en euros
     * @throws IllegalArgumentException si le montant HT est négatif
     */
    public double calculerTTC(double montantHT) {
        if (montantHT < 0) {
            throw new IllegalArgumentException("Le montant HT ne peut pas être négatif");
        }
        return montantHT * (1 + this.taux / 100);
    }

}
