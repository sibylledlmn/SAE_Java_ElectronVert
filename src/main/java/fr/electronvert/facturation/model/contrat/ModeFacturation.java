package fr.electronvert.facturation.model.contrat;

public enum ModeFacturation {

        /**
         * Facturation mensuelle de la consommation réelle.
         * Le client reçoit une facture le 5 basée sur sa consommation réelle du mois précédent.
         *  La facture est calculée à partir du relevé de consommation du mois précédent.
         */

        REEL,

        /**
         * Facturation par échéancier sur 11 mois avec facture de régularisation le 12ème mois.
         * Le client paie 11 mensualités fixes calculées sur une estimation de sa consommation
         * annuelle. Le 12ème mois, une facture de régularisation est émise pour ajuster le
         * montant total en fonction de la consommation réelle de l'année.
         */
        ECHEANCIER

}
