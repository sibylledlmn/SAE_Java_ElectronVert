package fr.electronvert.facturation.test;

import fr.electronvert.facturation.model.contrat.*;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.*;

import java.time.LocalDate;

public class TestRegularisationAnticipee {

    public static void main(String[] args) {

        System.out.println("--- Test de régularisation lors d'une cloture de contrat avant la fin de l'échéancier ---\n");

        // initialisation d'un tarif initial
        GestionnaireTarifs gestionnaireTarifs = new GestionnaireTarifs();
        gestionnaireTarifs.ajouterTarifInitial(new Tarif(
                LocalDate.of(2024, 1, 1),
                0.18, 0.20, 0.14,
                9.50, 12.00
        ));

        // création d'un client et son contrat en mode échéancier
        GestionnaireClients gestionnaireClients = new GestionnaireClients();
        Client client = gestionnaireClients.creerClient(
                "Bertrand", "Margaux", "margaux.baldin@gmail.com"
        );

        // CONTRAT EN ÉCHÉANCIER
        GestionnaireContrats gestionnaireContrats =
                new GestionnaireContrats(gestionnaireTarifs);

        Contrat contrat = gestionnaireContrats.creerContrat(
                client,
                "Strasbourg",
                new OffreHPHC(),
                ModeFacturation.ECHEANCIER,
                LocalDate.of(2024, 1, 1)
        );

        // RELEVÉS
        GestionnaireReleves gestionnaireReleves = new GestionnaireReleves();
        SimulateurIndex simulateurIndex = new SimulateurIndex();

        gestionnaireReleves.genererReleveOuverture(contrat, simulateurIndex);
        gestionnaireReleves.genererReleveMensuel(
                contrat,
                LocalDate.of(2024, 6, 30),
                simulateurIndex
        );

        // QUELQUES MENSUALITÉS
        GestionnairePaiements gestionnairePaiements =
                new GestionnairePaiements();

        for (int mois = 0; mois < 3; mois++) {
            gestionnairePaiements.preleverMensualite(
                    contrat,
                    LocalDate.of(2024, mois + 1, 20)
            );
        }

        // CLÔTURE
        contrat.cloturer(LocalDate.of(2024, 7, 1));

        // RÉGULARISATION → FACTURE
        GestionnaireFactures gestionnaireFactures =
                new GestionnaireFactures(gestionnaireTarifs);

        Facture factureRegularisation =
                gestionnaireFactures.regulariserClotureAnticipee(
                        contrat,
                        LocalDate.of(2024, 7, 5)
                );

        // AFFICHAGE
        System.out.println("Facture de régularisation de clôture");
        System.out.println("Type : " + factureRegularisation.getType());
        System.out.println("Montant TTC : " + factureRegularisation.getMontantTTC());
        System.out.println("Statut : " + factureRegularisation.getStatut());
        System.out.println("Contrat actif : " + contrat.estActif());
    }
}
