package fr.electronvert.facturation.test;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.ModeFacturation;
import fr.electronvert.facturation.model.contrat.OffreClassique;
import fr.electronvert.facturation.model.contrat.OffreHPHC;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeReleve;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.*;

import java.time.LocalDate;

public class DonneesTest {

// GESTIONNAIRES

    public static GestionnaireTarifs creerGestionnaireTarifs() {
        GestionnaireTarifs gt = new GestionnaireTarifs();

        // 🔹 Tarif historique initial
        gt.ajouterTarifInitial(new Tarif(
                LocalDate.of(2024, 1, 1),
                0.18,
                0.20,
                0.14,
                9.50,
                12.00
        ));

        // 🔹 Tarif réglementé suivant
        gt.creerNouveauTarif(
                LocalDate.of(2025, 2, 1),
                0.20,
                0.22,
                0.15,
                10.0,
                13.0
        );

        return gt;
    }

    public static GestionnaireClients creerGestionnaireClients() {
        return new GestionnaireClients();
    }

    public static GestionnaireContrats creerGestionnaireContrats(
            GestionnaireTarifs gt
    ) {
        return new GestionnaireContrats(gt);
    }

    public static GestionnaireFactures creerGestionnaireFactures(
            GestionnaireTarifs gt
    ) {
        return new GestionnaireFactures(gt);
    }

    public static GestionnairePaiements creerGestionnairePaiement() {
        return new GestionnairePaiements();
    }

    public static SimulateurIndex creerSimulateurIndex() {
        return new SimulateurIndex();
    }

// CLIENTS ET CONTRATS

    public static Contrat creerContratEcheancierClassique(
            GestionnaireClients gestionnaireClients,
            GestionnaireContrats gestionnaireContrats
    ) {
        Client client = gestionnaireClients.creerClient(
                "Durand",
                "Alice",
                "alice.durand@mail.fr"
        );

        return gestionnaireContrats.creerContrat(
                client,
                "12 rue des Lilas",
                new OffreClassique(),
                ModeFacturation.ECHEANCIER,
                LocalDate.of(2025, 3, 1)
        );
    }

    public static Contrat creerContratMensuelHPHC(
            GestionnaireClients gestionnaireClients,
            GestionnaireContrats gestionnaireContrats
    ) {
        Client client = gestionnaireClients.creerClient(
                "Martin",
                "Lucas",
                "lucas.martin@mail.fr"
        );

        return gestionnaireContrats.creerContrat(
                client,
                "8 avenue du Soleil",
                new OffreHPHC(),
                ModeFacturation.REEL,
                LocalDate.of(2025, 3, 1)
        );
    }

    /* =========================
       RELEVÉS (via SimulateurIndex)
       ========================= */

    public static void ajouterPremierReleve(
            Contrat contrat,
            LocalDate date,
            SimulateurIndex simulateurIndex
    ) {
        Releve releve = new Releve(
                contrat,
                TypeReleve.OUVERTURE,
                date,
                simulateurIndex.genererIndexInitial(contrat)
        );
        contrat.ajouterReleve(releve);
    }

    public static void ajouterReleveSuivant(
            Contrat contrat,
            LocalDate date,
            SimulateurIndex simulateurIndex
    ) {
        Releve releve = new Releve(
                contrat,
                TypeReleve.MENSUEL,
                date,
                simulateurIndex.calculerIndex(contrat, date)
        );
        contrat.ajouterReleve(releve);
    }

    public static void ajouterReleveCloture(
            Contrat contrat,
            LocalDate date,
            SimulateurIndex simulateurIndex
    ) {
        Releve releve = new Releve(
                contrat,
                TypeReleve.CLOTURE,
                date,
                simulateurIndex.calculerIndex(contrat, date)
        );
        contrat.ajouterReleve(releve);
    }

    public static void ajouterDeuxRelevesMensuels(
            Contrat contrat,
            LocalDate debut,
            SimulateurIndex simulateurIndex
    ) {
        ajouterPremierReleve(contrat, debut, simulateurIndex);
        ajouterReleveSuivant(contrat, debut.plusMonths(1), simulateurIndex);
    }

    public static void ajouterRelevesSurUnAn(
            Contrat contrat,
            LocalDate debut,
            SimulateurIndex simulateurIndex
    ) {
        ajouterPremierReleve(contrat, debut, simulateurIndex);

        for (int i = 1; i <= 12; i++) {
            ajouterReleveSuivant(
                    contrat,
                    debut.plusMonths(i),
                    simulateurIndex
            );
        }
    }
}
