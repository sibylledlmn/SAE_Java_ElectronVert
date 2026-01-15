package fr.electronvert.facturation.service;

import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.FraisRelance;
import fr.electronvert.facturation.model.facture.StatutFacture;
import fr.electronvert.facturation.model.facture.TauxTVA;

import java.time.LocalDate;
import java.util.List;

/**
 * Service métier responsable de la génération des frais de relance
 * pour les factures impayées.
 */
public class GestionnaireRelances {


    /**
     * Parcourt les factures impayées et génère des frais de relance
     * si la date de relance est atteinte.
     */
    //TODO : revoir les exceptions

    public void traiterRelances(
            List<Facture> facturesImpayees,
            LocalDate dateDuJour
    ) {
        if (facturesImpayees == null || dateDuJour == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        for (Facture facture : facturesImpayees) {
            if (facture.getDateProchaineRelance() != null
                    && !dateDuJour.isBefore(facture.getDateProchaineRelance())) {

                creerFraisRelance(facture, dateDuJour);
            }
        }
    }

    /**
     * Crée un frais de relance pour une facture impayée.
     */
    public FraisRelance creerFraisRelance(
            Facture facture,
            LocalDate dateRelance
    ) {
        if (facture == null) {
            throw new IllegalArgumentException("La facture ne peut pas être nulle");
        }
        if (facture.getStatut() != StatutFacture.IMPAYEE) {
            throw new IllegalStateException("La facture n'est pas impayée");
        }

        int numeroRelance = facture.getFraisDeRelance().size() + 1;

        double montantHT = FraisRelance.MONTANT_HT;
        double montantTVA = TauxTVA.NORMAL.calculerMontantTVA(montantHT);
        double montantTTC = montantHT + montantTVA;

        FraisRelance frais = new FraisRelance(
                numeroRelance,
                dateRelance
        );

        facture.ajouterFraisDeRelance(frais);
        facture.planifierProchaineRelance(dateRelance);

        return frais;
    }
}

