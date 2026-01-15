package fr.electronvert.facturation.test;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.ModeFacturation;
import fr.electronvert.facturation.model.contrat.OffreClassique;
import fr.electronvert.facturation.model.contrat.OffreHPHC;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.service.*;

import java.time.LocalDate;

public class TestFacturesMensuelles {

    public static void main(String[] args) {

        System.out.println("=== Test des Factures Mensuelle de consommation réelle ===\n");

      // création d'un tarif pour les tests
        GestionnaireTarifs gestionnaireTarifs = new GestionnaireTarifs();
        gestionnaireTarifs.ajouterTarifInitial(new Tarif(
                LocalDate.of(2024, 1, 1),
                0.18,
                0.20,
                0.14,
                9.50,
                12.00
        ));

    // création de deux clients ainsi que leurs contrats avec un mode de facturation au réel, avec deux offres différentes
        GestionnaireClients gestionnaireClients = new GestionnaireClients();
        Client clientHPHC = gestionnaireClients.creerClient(
                "Bertrand", "Margaux", "margaux.baldin@gmail.com"
        );
        Client clientClassique = gestionnaireClients.creerClient(
                "Martin", "Marie", "marie.sergeant@gmail.com"
        );


        GestionnaireContrats gestionnaireContrats =
                new GestionnaireContrats(gestionnaireTarifs);

        Contrat contratHPHC = gestionnaireContrats.creerContrat(
                clientHPHC,
                "Strasbourg",
                new OffreHPHC(),
                ModeFacturation.REEL,
                LocalDate.of(2024, 6, 15)
        );

        Contrat contratClassique = gestionnaireContrats.creerContrat(
                clientClassique,
                "Lyon",
                new OffreClassique(),
                ModeFacturation.REEL,
                LocalDate.of(2025, 3, 1)
        );

// création d'un relevé d'ouverture + un relevé mensuel qui suit
        GestionnaireReleves gestionnaireReleves = new GestionnaireReleves();
        SimulateurIndex simulateurIndex = new SimulateurIndex();

        // Relevés d'ouverture
        gestionnaireReleves.genererReleveOuverture(contratHPHC, simulateurIndex);
        gestionnaireReleves.genererReleveOuverture(contratClassique, simulateurIndex);

        // Relevés mensuels
        gestionnaireReleves.genererReleveMensuel(
                contratHPHC,
                LocalDate.of(2024, 6, 30),
                simulateurIndex
        );

        gestionnaireReleves.genererReleveMensuel(
                contratClassique,
                LocalDate.of(2025, 3, 31),
                simulateurIndex
        );



        // Génération des factures mensuelles, avec pour date d'émission le 5 du mois
        GestionnaireFactures gestionnaireFactures =
                new GestionnaireFactures(gestionnaireTarifs);

        Facture factureHPHC = gestionnaireFactures.creerFactureMensuelle(
                contratHPHC,
                LocalDate.of(2024, 7, 5)
        );

        Facture factureClassique = gestionnaireFactures.creerFactureMensuelle(
                contratClassique,
                LocalDate.of(2025, 4, 5)
        );

        // FACTURE HPHC
        System.out.println("--- Facture mensuelle contrat HP/HC ---");
        System.out.println("Client : " + clientHPHC.getPrenom() + " " + clientHPHC.getNom());
        System.out.println("Référence : " + factureHPHC.getReference());
        System.out.println("Date émission : " + factureHPHC.getDateEmission());
        System.out.println("Montant HT : " + factureHPHC.getMontantHT());
        System.out.println("Montant TVA : " + factureHPHC.getMontantTVA());
        System.out.println("Montant TTC : " + factureHPHC.getMontantTTC());
        System.out.println("Statut : " + factureHPHC.getStatut());

        System.out.println("");

        // FACTURE CLASSIQUE
        System.out.println("--- Facture mensuelle Classique ---");
        System.out.println("Client : " + clientClassique.getPrenom() + " " + clientClassique.getNom());
        System.out.println("Référence : " + factureClassique.getReference());
        System.out.println("Date émission : " + factureClassique.getDateEmission());
        System.out.println("Montant HT : " + factureClassique.getMontantHT());
        System.out.println("Montant TVA : " + factureClassique.getMontantTVA());
        System.out.println("Montant TTC : " + factureClassique.getMontantTTC());
        System.out.println("Statut : " + factureClassique.getStatut());
    }
}
