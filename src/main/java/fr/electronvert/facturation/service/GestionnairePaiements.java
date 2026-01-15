package fr.electronvert.facturation.service;

import fr.electronvert.facturation.exception.FactureDejaPayeeException;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.Echeancier;
import fr.electronvert.facturation.model.contrat.ModeFacturation;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.Paiement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestionnairePaiements {

    List<Paiement> paiements =  new ArrayList<>();

    public Paiement payerFacture(
            Facture facture,
            LocalDate datePaiement
    ) throws FactureDejaPayeeException {

        if (facture == null || datePaiement == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        double montant = facture.getMontantTotalTTCAPayer();
        if(facture.getContrat().getSoldeCrediteur()<0){
            montant += facture.getContrat().getSoldeCrediteur();
        }

        Paiement paiement = new Paiement(datePaiement, montant);

        facture.enregistrerPaiement(paiement);

        paiements.add(paiement);

        return paiement;
    }

    public Paiement preleverMensualite(
            Contrat contrat,
            LocalDate datePrelevement
    ) {
        if (contrat == null || datePrelevement == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        if (contrat.getModeFacturation() != ModeFacturation.ECHEANCIER) {
            throw new IllegalStateException(
                    "Le contrat n'est pas en mode échéancier"
            );
        }

        Echeancier echeancier = contrat.getEcheancier();
        if (echeancier == null) {
            throw new IllegalStateException(
                    "Aucun échéancier associé au contrat"
            );
        }

        if (!echeancier.peutEmettreMensualite()) {
            throw new IllegalStateException(
                    "Aucune mensualité ne peut être émise pour cet échéancier"
            );
        }

        double montantMensualite = echeancier.getMontantMensualiteTTC();
        if(contrat.getSoldeCrediteur()<0){
            montantMensualite += contrat.getSoldeCrediteur();
            contrat.reinitialiserSoldeCrediteur();
        }

        Paiement paiement = new Paiement(
                datePrelevement,
                montantMensualite
        );

        echeancier.enregistrerMensualiteEmise();
        paiements.add(paiement);

        return paiement;
    }





    public List<Paiement> getPaiements() {
        return Collections.unmodifiableList(paiements);
    }

    public double calculerChiffreAffaires() {
        double total = 0.0;
        for (Paiement p : paiements) {
            total += p.getMontantPaye();
        }
        return total;
    }


}
