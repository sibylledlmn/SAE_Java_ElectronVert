package fr.electronvert.facturation.test;

import fr.electronvert.facturation.model.contrat.*;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.*;

import java.time.LocalDate;

public class TestChangementModeFacturation {

    public static void main(String[] args) {

        System.out.println("=== TEST CHANGEMENT DE MODE DE FACTURATION ===\n");

        // =====================================================
        // TARIF
        // =====================================================
        GestionnaireTarifs gestionnaireTarifs = new GestionnaireTarifs();
        gestionnaireTarifs.ajouterTarifInitial(new Tarif(
                LocalDate.of(2024, 1, 1),
                0.18, 0.20, 0.14,
                9.50, 12.00
        ));

        // =====================================================
        // CLIENTS
        // =====================================================
        GestionnaireClients gestionnaireClients = new GestionnaireClients();

        Client marie = gestionnaireClients.creerClient(
                "Martin", "Marie", "marie.martin@gmail.com"
        );

        Client margaux = gestionnaireClients.creerClient(
                "Bertrand", "Margaux", "margaux.bertrand@gmail.com"
        );

        Client lucas = gestionnaireClients.creerClient(
                "Durand", "Lucas", "lucas.durand@gmail.com"
        );

        // =====================================================
        // CONTRATS
        // =====================================================
        GestionnaireContrats gestionnaireContrats =
                new GestionnaireContrats(gestionnaireTarifs);

        // CAS 1 : REEL → ECHEANCIER
        Contrat contratMarie = gestionnaireContrats.creerContrat(
                marie,
                "Lyon",
                new OffreClassique(),
                ModeFacturation.REEL,
                LocalDate.of(2024, 3, 1)
        );

        // CAS 2 : ECHEANCIER → REEL
        Contrat contratMargaux = gestionnaireContrats.creerContrat(
                margaux,
                "Strasbourg",
                new OffreClassique(),
                ModeFacturation.ECHEANCIER,
                LocalDate.of(2024, 6, 1)
        );

        // CAS 3 : changement refusé
        Contrat contratLucas = gestionnaireContrats.creerContrat(
                lucas,
                "Bordeaux",
                new OffreClassique(),
                ModeFacturation.REEL,
                LocalDate.of(2024, 1, 1)
        );

        GestionnairePaiements gestionnairePaiements =
                new GestionnairePaiements();

        // =====================================================
        // CAS 1 — REEL → ECHEANCIER
        // =====================================================
        System.out.println("---- CAS 1 : REEL → ECHEANCIER ----");

        LocalDate demandeMarie = LocalDate.of(2025, 2, 10); // mois avant mars

        gestionnaireContrats.demanderChangementModeFacturation(
                contratMarie,
                ModeFacturation.ECHEANCIER,
                demandeMarie
        );

        System.out.println("Mode AVANT application : "
                + contratMarie.getModeFacturation());
        System.out.println("Échéancier AVANT : "
                + (contratMarie.getEcheancier() != null));

        // 👉 simulation du simulateur de date
        gestionnaireContrats.appliquerChangementsPlanifiesModeFacturation(contratMarie);

        System.out.println("Mode APRÈS application : "
                + contratMarie.getModeFacturation());
        System.out.println("Échéancier APRÈS : "
                + (contratMarie.getEcheancier() != null));

        System.out.println("Montant mensualité TTC : "
                + contratMarie.getEcheancier().getMontantMensualiteTTC() + " €");

        // prélèvement d'une mensualité
        gestionnairePaiements.preleverMensualite(
                contratMarie,
                LocalDate.of(2025, 3, 20)
        );

        System.out.println("Mensualités émises : "
                + contratMarie.getEcheancier().getMensualitesEmises());

        // =====================================================
        // CAS 2 — ECHEANCIER → REEL
        // =====================================================
        System.out.println("\n---- CAS 2 : ECHEANCIER → REEL ----");

        LocalDate demandeMargaux = LocalDate.of(2025, 5, 10); // mois avant juin

        gestionnaireContrats.demanderChangementModeFacturation(
                contratMargaux,
                ModeFacturation.REEL,
                demandeMargaux
        );

        System.out.println("Mode AVANT application : "
                + contratMargaux.getModeFacturation());
        System.out.println("Échéancier AVANT : "
                + (contratMargaux.getEcheancier() != null));

        // 👉 simulation du simulateur de date
        gestionnaireContrats.appliquerChangementsPlanifiesModeFacturation(contratMargaux);

        System.out.println("Mode APRÈS application : "
                + contratMargaux.getModeFacturation());
        System.out.println("Échéancier APRÈS : "
                + (contratMargaux.getEcheancier() != null));

        // tentative de prélèvement → doit échouer
        try {
            gestionnairePaiements.preleverMensualite(
                    contratMargaux,
                    LocalDate.of(2025, 6, 20)
            );
            System.out.println("❌ ERREUR : prélèvement autorisé en mode réel");
        } catch (IllegalStateException e) {
            System.out.println("✅ Prélèvement refusé en mode réel");
        }

        // =====================================================
        // CAS 3 — CHANGEMENT REFUSÉ (DATE NON VALIDE)
        // =====================================================
        System.out.println("\n---- CAS 3 : CHANGEMENT REFUSÉ ----");

        try {
            gestionnaireContrats.demanderChangementModeFacturation(
                    contratLucas,
                    ModeFacturation.ECHEANCIER,
                    LocalDate.of(2024, 6, 10) // pas le bon mois
            );
            System.out.println("❌ ERREUR : changement autorisé à tort");
        } catch (IllegalStateException e) {
            System.out.println("✅ Changement refusé comme prévu");
            System.out.println("Message : " + e.getMessage());
        }

        System.out.println("\n=== FIN DU TEST ===");
    }
}
