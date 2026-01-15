package fr.electronvert.facturation.test;

import fr.electronvert.facturation.model.contrat.*;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.*;

import java.time.LocalDate;

public class TestClotureContrat {

    public static void main(String[] args) {

        System.out.println("--- Test clôture contrat en facturation RÉELLE ---\n");

        // TARIF
        GestionnaireTarifs gestionnaireTarifs = new GestionnaireTarifs();
        gestionnaireTarifs.ajouterTarifInitial(new Tarif(
                LocalDate.of(2024, 1, 1),
                0.18, 0.20, 0.14,
                9.50, 12.00
        ));

        // CLIENT
        GestionnaireClients gestionnaireClients = new GestionnaireClients();
        Client client = gestionnaireClients.creerClient(
                "Bertrand", "Margaux", "margaux.baldin@gmail.com"
        );

        // CONTRAT EN MODE RÉEL
        GestionnaireContrats gestionnaireContrats =
                new GestionnaireContrats(gestionnaireTarifs);

        Contrat contrat = gestionnaireContrats.creerContrat(
                client,
                "Strasbourg",
                new OffreHPHC(),
                ModeFacturation.REEL,
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

        // FACTURE MENSUELLE
        GestionnaireFactures gestionnaireFactures =
                new GestionnaireFactures(gestionnaireTarifs);

        Facture factureMensuelle = gestionnaireFactures.creerFactureMensuelle(
                contrat,
                LocalDate.of(2024, 7, 5)
        );

        factureMensuelle.marquerCommePayee();

        // CLÔTURE
        gestionnaireContrats.cloturerContrat(
                contrat,
                LocalDate.of(2024, 7, 10)
        );

        // RELEVÉ DE CLÔTURE
        Releve releveCloture = gestionnaireReleves.genererReleveCloture(
                contrat,
                LocalDate.of(2024, 7, 10),
                simulateurIndex
        );

        System.out.println("Relevé de clôture : " + releveCloture.getTypeReleve());

        // DERNIÈRE FACTURE (PAS DE RÉGULARISATION)
        Facture derniereFacture = gestionnaireFactures.creerFactureMensuelle(
                contrat,
                LocalDate.of(2024, 8, 5)
        );

        System.out.println("\nDernière facture (mode réel) :");
        System.out.println("Type : " + derniereFacture.getType());
        System.out.println("Montant TTC : " + derniereFacture.getMontantTTC());
        System.out.println("Statut : " + derniereFacture.getStatut());
        System.out.println("Facturation du contrat terminée : " + contrat.estFacturationTerminee());
    }
}

