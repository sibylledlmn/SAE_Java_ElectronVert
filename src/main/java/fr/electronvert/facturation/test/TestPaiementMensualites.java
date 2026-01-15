package fr.electronvert.facturation.test;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.ModeFacturation;
import fr.electronvert.facturation.model.contrat.OffreClassique;
import fr.electronvert.facturation.model.facture.Paiement;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.GestionnaireClients;
import fr.electronvert.facturation.service.GestionnaireContrats;
import fr.electronvert.facturation.service.GestionnairePaiements;
import fr.electronvert.facturation.service.GestionnaireTarifs;

import java.time.LocalDate;

public class TestPaiementMensualites {

    public static void main(String[] args) {

        System.out.println("--- Test du paiement/prélèvement des mensualités ===\n");

        // initialisation d'un tarif initial

        GestionnaireTarifs gestionnaireTarifs = new GestionnaireTarifs();
        gestionnaireTarifs.ajouterTarifInitial(new Tarif(
                LocalDate.of(2024, 1, 1),
                0.18,
                0.20,
                0.14,
                9.50,
                12.00
        ));


        // création d'un client à tester
        GestionnaireClients gestionnaireClients = new GestionnaireClients();
        Client client = gestionnaireClients.creerClient(
                "Martin",
                "Marie",
                "marie.sergeant@gmail.com"
        );


        // création du contrat du client  avec mode de facturation échéancier
        GestionnaireContrats gestionnaireContrats =
                new GestionnaireContrats(gestionnaireTarifs);

        Contrat contrat = gestionnaireContrats.creerContrat(
                client,
                "Lyon",
                new OffreClassique(),
                ModeFacturation.ECHEANCIER,
                LocalDate.of(2025, 3, 1)
        );


        // état de l'échéancier à la création du client et du contrat
        System.out.println("Mensualité TTC : "
                + contrat.getEcheancier().getMontantMensualiteTTC());
        System.out.println("Mensualités émises : "
                + contrat.getEcheancier().getMensualitesEmises());
        System.out.println("Mensualités restantes : "
                + contrat.getEcheancier().getMensualitesRestantes());
        System.out.println("");



        // TEST DES PAIEMENTS/PRELEVEMENTS (les 20 du mois)
        GestionnairePaiements gestionnairePaiements =
                new GestionnairePaiements();

        // 1er prélèvement
        Paiement paiement1 = gestionnairePaiements.preleverMensualite(
                contrat,
                LocalDate.of(2025, 3, 20)
        );

        System.out.println("Paiement du " + paiement1.getDatePaiement());
        System.out.println("Montant payé TTC : " + paiement1.getMontantPaye());
        System.out.println("Mensualités émises : "
                + contrat.getEcheancier().getMensualitesEmises());
        System.out.println("Mensualités restantes : "
                + contrat.getEcheancier().getMensualitesRestantes());
        System.out.println("");

        // 2e prélèvement
        Paiement paiement2 = gestionnairePaiements.preleverMensualite(
                contrat,
                LocalDate.of(2025, 4, 20)
        );

        System.out.println("Paiement du " + paiement2.getDatePaiement());
        System.out.println("Montant payé TTC : " + paiement2.getMontantPaye());
        System.out.println("Mensualités émises : "
                + contrat.getEcheancier().getMensualitesEmises());
        System.out.println("Mensualités restantes : "
                + contrat.getEcheancier().getMensualitesRestantes());
    }
}
