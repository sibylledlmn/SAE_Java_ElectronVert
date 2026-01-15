package fr.electronvert.facturation.test;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.ModeFacturation;
import fr.electronvert.facturation.model.contrat.OffreClassique;
import fr.electronvert.facturation.model.contrat.OffreHPHC;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.GestionnaireClients;
import fr.electronvert.facturation.service.GestionnaireContrats;
import fr.electronvert.facturation.service.GestionnaireTarifs;

import java.time.LocalDate;

public class TestCreationContrats {

    public static void main(String[] args) {
        GestionnaireTarifs gestionnaireTarifs = new GestionnaireTarifs();
        gestionnaireTarifs.ajouterTarifInitial(new Tarif(
                LocalDate.of(2024, 1, 1),
                0.18,
                0.20,
                0.14,
                9.50,
                12.00
        ));


        GestionnaireClients gestionnaireClients = new GestionnaireClients();
        Client client1 = gestionnaireClients.creerClient("Martin", "Marie", "marie.sergeant@gmail.com");
        Client client2 = gestionnaireClients.creerClient("Bertrand", "Margaux", "margaux.baldin@gmail.com");

        GestionnaireContrats gestionnaireContrats = new GestionnaireContrats(gestionnaireTarifs);
        Contrat contrat1 = gestionnaireContrats.creerContrat(client1, "Lyon",new OffreClassique(), ModeFacturation.ECHEANCIER,
                LocalDate.of(2025, 3, 1));
        Contrat contrat2 = gestionnaireContrats.creerContrat(client2, "Strasbourg",new OffreHPHC(), ModeFacturation.REEL,
                LocalDate.of(2024, 6, 15));


        System.out.println("=== Test de créations des clients et leur contrat ===");


        System.out.println("Contrat actif : " + contrat1.estActif());
        System.out.println("Échéancier présent : " + (contrat1.getEcheancier() != null));
        System.out.println("Contrat actif : " + contrat2.estActif());
        System.out.println("Échéancier présent : " + (contrat2.getEcheancier() != null));

        System.out.println("");


        System.out.println("----- Contrat 1 -----");
        System.out.println("Client : " + contrat1.getClient().getPrenom()
                + " " + contrat1.getClient().getNom());
        System.out.println("Adresse : " + contrat1.getAdressePostale());
        System.out.println("Date souscription : " + contrat1.getDateSouscription());
        System.out.println("Offre : " + contrat1.getOffreTarifaire().getClass().getSimpleName());
        System.out.println("Mode de facturation : " + contrat1.getModeFacturation());

        System.out.println("");

        if (contrat1.getEcheancier() != null) {
            System.out.println("Montant mensualité HT : "
                    + contrat1.getEcheancier().getMontantMensualite());
            System.out.println("Mensualités émises : "
                    + contrat1.getEcheancier().getMensualitesEmises());
            System.out.println("Échéancier terminé : "
                    + contrat1.getEcheancier().estTermine());
        }

        System.out.println("");

        System.out.println("----- Contrat 2 -----");
        System.out.println("Client : " + contrat2.getClient().getPrenom()
                + " " + contrat2.getClient().getNom());
        System.out.println("Adresse : " + contrat2.getAdressePostale());
        System.out.println("Date souscription : " + contrat2.getDateSouscription());
        System.out.println("Offre : " + contrat2.getOffreTarifaire().getClass().getSimpleName());
        System.out.println("Mode de facturation : " + contrat2.getModeFacturation());

        System.out.println("");

        System.out.println("Nombre total de contrats : "
                + gestionnaireContrats.getContrats().size());
        System.out.println("Nombre de clients : "
                + gestionnaireClients.getNombreClients());


    }

}
