package fr.electronvert.facturation.test;

import fr.electronvert.facturation.model.contrat.*;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.*;

import java.time.LocalDate;

public class TestRenouvellementEcheancier {

    public static void main(String[] args) {

        System.out.println("=== TEST RENOUVELLEMENT D'ÉCHÉANCIER APRÈS 1 AN ===\n");

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
        // CLIENT + CONTRAT
        // =====================================================
        GestionnaireClients gestionnaireClients = new GestionnaireClients();
        Client marie = gestionnaireClients.creerClient(
                "Martin", "Marie", "marie.martin@gmail.com"
        );

        GestionnaireContrats gestionnaireContrats =
                new GestionnaireContrats(gestionnaireTarifs);

        Contrat contrat = gestionnaireContrats.creerContrat(
                marie,
                "Lyon",
                new OffreClassique(),
                ModeFacturation.ECHEANCIER,
                LocalDate.of(2024, 1, 1)
        );

        System.out.println("Contrat créé en échéancier");
        System.out.println("Mensualité initiale TTC : "
                + contrat.getEcheancier().getMontantMensualiteTTC() + " €");

        // =====================================================
        // RELEVÉS SUR 1 AN
        // =====================================================
        GestionnaireReleves gestionnaireReleves = new GestionnaireReleves();
        SimulateurIndex simulateurIndex = new SimulateurIndex();

        // relevé d'ouverture
        gestionnaireReleves.genererReleveOuverture(contrat, simulateurIndex);

        // relevés mensuels pendant 1 an
        LocalDate dateReleve = LocalDate.of(2024, 1, 31);
        for (int i = 0; i < 12; i++) {
            gestionnaireReleves.genererReleveMensuel(
                    contrat,
                    dateReleve.plusMonths(i),
                    simulateurIndex
            );
        }

        System.out.println("Nombre de relevés générés : "
                + contrat.getReleves().size());

        // =====================================================
        // 11 MENSUALITÉS PRÉLEVÉES
        // =====================================================
        GestionnairePaiements gestionnairePaiements =
                new GestionnairePaiements();

        for (int i = 0; i < 11; i++) {
            gestionnairePaiements.preleverMensualite(
                    contrat,
                    LocalDate.of(2024, 2 + i, 20)
            );
        }

        System.out.println("Mensualités émises : "
                + contrat.getEcheancier().getMensualitesEmises());

        // =====================================================
        // FACTURE DE RÉGULARISATION
        // =====================================================
        GestionnaireFactures gestionnaireFactures =
                new GestionnaireFactures(gestionnaireTarifs);

        Facture factureRegu =
                gestionnaireFactures.regulariserFinEcheancier(
                        contrat,
                        LocalDate.of(2025, 1, 5)
                );

        System.out.println("\n--- FACTURE DE RÉGULARISATION ---");
        System.out.println("Montant TTC : " + factureRegu.getMontantTTC() + " €");
        System.out.println("Statut : " + factureRegu.getStatut());

        // =====================================================
        // NOUVEL ÉCHÉANCIER
        // =====================================================
        gestionnaireContrats.creerEcheancier(contrat);

        System.out.println("\n--- NOUVEL ÉCHÉANCIER ---");
        System.out.println("Nouvelle mensualité TTC : "
                + contrat.getEcheancier().getMontantMensualiteTTC() + " €");

        System.out.println("Mensualités émises : "
                + contrat.getEcheancier().getMensualitesEmises());

        System.out.println("\n=== FIN DU TEST ===");
    }
}
