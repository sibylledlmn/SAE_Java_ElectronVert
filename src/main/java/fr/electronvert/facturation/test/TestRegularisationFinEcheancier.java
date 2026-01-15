package fr.electronvert.facturation.test;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.ModeFacturation;
import fr.electronvert.facturation.model.contrat.OffreClassique;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.Paiement;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.*;

import java.time.LocalDate;

public class TestRegularisationFinEcheancier {

    public static void main(String[] args) {

        System.out.println("--- Test des factures de régularisation et du solde créditeur à la fin d'un échéancier ---\n");

        // initialisation d'un tarif initial

        GestionnaireTarifs gestionnaireTarifs = new GestionnaireTarifs();
        gestionnaireTarifs.ajouterTarifInitial(new Tarif(
                LocalDate.of(2024, 1, 1),
                0.18, 0.20, 0.14,
                9.50, 12.00
        ));

        // création de deux clientes et leurs contrats en mode échéancier
        GestionnaireClients gestionnaireClients = new GestionnaireClients();
        Client client = gestionnaireClients.creerClient(
                "Martin", "Marie", "marie.sergeant@gmail.com"
        );

        Client margaux = gestionnaireClients.creerClient(
                "Bertrand", "Margaux", "margaux.baldin@gmail.com"
        );

        GestionnaireContrats gestionnaireContrats =
                new GestionnaireContrats(gestionnaireTarifs);

        Contrat contrat = gestionnaireContrats.creerContrat(
                client,
                "Lyon",
                new OffreClassique(),
                ModeFacturation.ECHEANCIER,
                LocalDate.of(2024, 1, 1)
        );

        Contrat contrat2 = gestionnaireContrats.creerContrat(
                margaux,
                "Strasbourg",
                new OffreClassique(),
                ModeFacturation.ECHEANCIER,
                LocalDate.of(2024, 1, 15)
        );

        // Génération d'un an de relevés d'index du compteur

        GestionnaireReleves gestionnaireReleves = new GestionnaireReleves();
        SimulateurIndex simulateurIndex = new SimulateurIndex();

        gestionnaireReleves.genererReleveOuverture(contrat, simulateurIndex);

        for (int mois = 1; mois <= 11; mois++) {
            gestionnaireReleves.genererReleveMensuel(
                    contrat,
                    LocalDate.of(2024, mois + 1, 1),
                    simulateurIndex
            );
        }

        gestionnaireReleves.genererReleveOuverture(contrat2, simulateurIndex);
        for (int mois = 1; mois <= 11; mois++) {
            gestionnaireReleves.genererReleveMensuel(
                    contrat2,
                    LocalDate.of(2024, mois + 1, 15),
                    simulateurIndex
            );
        }

        // Prélèvement des 11 mensualités

        GestionnairePaiements gestionnairePaiements =
                new GestionnairePaiements();

        for (int mois = 0; mois < 11; mois++) {
            Paiement paiement = gestionnairePaiements.preleverMensualite(
                    contrat,
                    LocalDate.of(2024, mois + 1, 20)
            );
            System.out.println("Mensualité prélevée " + (mois+1) + " : " + paiement.getMontantPaye());
            gestionnairePaiements.preleverMensualite(
                    contrat2,
                    LocalDate.of(2024, mois + 1, 20)
            );
        }



        // Génération de la facture de régularisation du 12ème mois
        GestionnaireFactures gestionnaireFactures =
                new GestionnaireFactures(gestionnaireTarifs);

        Facture factureRegularisation =
                gestionnaireFactures.regulariserFinEcheancier(
                        contrat,
                        LocalDate.of(2025, 1, 5)
                );

        Facture factureRegularisation2 = gestionnaireFactures.regulariserFinEcheancier(
                contrat2,
                LocalDate.of(2025, 1, 5)
        );

        System.out.println("Facture de régularisation créée");
        System.out.println("Type : " + factureRegularisation.getType());
        System.out.println("Montant HT : " + factureRegularisation.getMontantHT());
        System.out.println("TVA : " + factureRegularisation.getMontantTVA());
        System.out.println("Montant TTC : " + factureRegularisation.getMontantTTC());
        System.out.println("Statut : " + factureRegularisation.getStatut());
        System.out.println("Échéancier terminé : "
                + contrat.getEcheancier().estTermine());
        System.out.println("Solde créditeur : " +contrat.getSoldeCrediteur());
        System.out.println("");
        System.out.println("-------");
        System.out.println("");
        System.out.println("Facture de régularisation 2 créée");
        System.out.println("Type : " + factureRegularisation2.getType());
        System.out.println("Montant HT : " + factureRegularisation2.getMontantHT());
        System.out.println("TVA : " + factureRegularisation2.getMontantTVA());
        System.out.println("Montant TTC : " + factureRegularisation2.getMontantTTC());
        System.out.println("Statut : " + factureRegularisation2.getStatut());
        System.out.println("Échéancier terminé : "
                + contrat2.getEcheancier().estTermine());
        System.out.println("Solde créditeur : " +contrat2.getSoldeCrediteur());
    }

}
