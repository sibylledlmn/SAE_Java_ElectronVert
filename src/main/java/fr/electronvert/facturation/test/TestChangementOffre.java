package fr.electronvert.facturation.test;

import fr.electronvert.facturation.model.contrat.*;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.*;

import java.time.LocalDate;
import java.util.Map;

public class TestChangementOffre {

    public static void main(String[] args) {

        System.out.println("--- Tests de changements d'offre tarifaire ---\n");

      // initialisation d'un premier tarif
        GestionnaireTarifs gestionnaireTarifs = new GestionnaireTarifs();
        gestionnaireTarifs.ajouterTarifInitial(new Tarif(
                LocalDate.of(2024, 1, 1),
                0.18, 0.20, 0.14,
                9.50, 12.00
        ));

      // création de deux clients et contrats pour tester le changement d'offre gratuit et payant
        GestionnaireClients gestionnaireClients = new GestionnaireClients();

        Client marie = gestionnaireClients.creerClient(
                "Martin", "Marie", "marie.martin@gmail.com"
        );

        Client margaux = gestionnaireClients.creerClient(
                "Bertrand", "Margaux", "margaux.bertrand@gmail.com"
        );


        GestionnaireContrats gestionnaireContrats =
                new GestionnaireContrats(gestionnaireTarifs);

        //  changement PAYANT
        Contrat contrat1 = gestionnaireContrats.creerContrat(
                marie,
                "Lyon",
                new OffreClassique(),
                ModeFacturation.REEL,
                LocalDate.of(2024, 3, 1)
        );

        // changement GRATUIT (mois anniversaire)
        Contrat contrat2 = gestionnaireContrats.creerContrat(
                margaux,
                "Strasbourg",
                new OffreClassique(),
                ModeFacturation.REEL,
                LocalDate.of(2024, 6, 1)
        );


        GestionnaireReleves gestionnaireReleves = new GestionnaireReleves();
        GestionnaireFactures gestionnaireFactures =
                new GestionnaireFactures(gestionnaireTarifs);
        SimulateurIndex simulateurIndex = new SimulateurIndex();


        // CHANGEMENT PAYANT
        System.out.println("---- CAS CHANGEMENT PAYANT ----\n");

        gestionnaireReleves.genererReleveOuverture(contrat1, simulateurIndex);

        Releve releveMarieJuin = gestionnaireReleves.genererReleveMensuel(
                contrat1,
                LocalDate.of(2024, 6, 30),
                simulateurIndex
        );

        Map<TypeConso, Double> consoMarieAvant =
                releveMarieJuin.calculerConsommation(
                        contrat1.getAvantDernierReleve()
                );

        System.out.println("Consommation AVANT changement (Offre Classique) :");
        consoMarieAvant.forEach((k, v) ->
                System.out.println(k + " : " + v)
        );

        Facture factureMarieAvant =
                gestionnaireFactures.creerFactureMensuelle(
                        contrat1,
                        LocalDate.of(2024, 7, 5)
                );

        System.out.println("Facture AVANT changement : "
                + factureMarieAvant.getMontantTTC() + " €");

        //  Demande de changement HORS mois anniversaire
        gestionnaireContrats.demanderChangementOffreTarifaire(
                contrat1,
                new OffreHPHC(),
                LocalDate.of(2024, 6, 10)
        );

        System.out.println("Frais changement en attente : "
                + contrat1.getFraisChangementOffreEnAttente());

        //  Application du changement
        contrat1.appliquerChangementOffre();

        Releve releveMarieJuillet = gestionnaireReleves.genererReleveMensuel(
                contrat1,
                LocalDate.of(2024, 7, 31),
                simulateurIndex
        );

        Map<TypeConso, Double> consoMarieApres =
                releveMarieJuillet.calculerConsommation(
                        contrat1.getAvantDernierReleve()
                );

        System.out.println("\nConsommation APRÈS changement (HP/HC) :");
        consoMarieApres.forEach((typeConso, kWh) ->
                System.out.println(typeConso + " : " + kWh)
        );

        Facture factureMarieApres =
                gestionnaireFactures.creerFactureMensuelle(
                        contrat1,
                        LocalDate.of(2024, 8, 5)
                );

        System.out.println("Facture APRÈS changement : "
                + factureMarieApres.getMontantTTC() + " €");



        //  CAS CHANGEMENT GRATUIT
        System.out.println("\n---- CAS CHANGEMENT GRATUIT ----\n");

        gestionnaireReleves.genererReleveOuverture(contrat2, simulateurIndex);

        Releve releveMargauxMai = gestionnaireReleves.genererReleveMensuel(
                contrat2,
                LocalDate.of(2025, 5, 31),
                simulateurIndex
        );

        Facture factureMargauxAvant =
                gestionnaireFactures.creerFactureMensuelle(
                        contrat2,
                        LocalDate.of(2025, 6, 5)
                );

        System.out.println("Facture AVANT changement : "
                + factureMargauxAvant.getMontantTTC() + " €");

        //  Demande le mois précédent l’anniversaire (GRATUIT)
        gestionnaireContrats.demanderChangementOffreTarifaire(
                contrat2,
                new OffreHPHC(),
                LocalDate.of(2025, 5, 10)
        );

        System.out.println("Frais changement en attente : "
                + contrat2.getFraisChangementOffreEnAttente());

        //  Application du changement le mois suivant
        contrat2.appliquerChangementOffre();

        Releve releveMargauxJuin = gestionnaireReleves.genererReleveMensuel(
                contrat2,
                LocalDate.of(2025, 6, 30),
                simulateurIndex
        );

        Facture factureMargauxApres =
                gestionnaireFactures.creerFactureMensuelle(
                        contrat2,
                        LocalDate.of(2025, 7, 5)
                );

        System.out.println("Facture APRÈS changement : "
                + factureMargauxApres.getMontantTTC() + " €");

    }
}

