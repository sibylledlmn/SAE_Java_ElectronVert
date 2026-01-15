package fr.electronvert.facturation.test;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.ModeFacturation;
import fr.electronvert.facturation.model.contrat.OffreClassique;
import fr.electronvert.facturation.model.contrat.OffreHPHC;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.service.*;

import java.time.LocalDate;
import java.util.Map;

public class TestReleves {
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

        GestionnaireReleves gestionnaireReleves = new GestionnaireReleves();
        SimulateurIndex simulateurIndex = new SimulateurIndex();

        System.out.println("=== Test Des Relevés ===");
        System.out.println("");

        // CREATIONS DES RELEVES D'OUVERTURE

        Releve releveOuverture1 = gestionnaireReleves.genererReleveOuverture(contrat1, simulateurIndex);
        Releve releveOuverture2 = gestionnaireReleves.genererReleveOuverture(contrat2, simulateurIndex);

        System.out.println("Relevé ajouté : " + releveOuverture1.getTypeReleve());
        System.out.println("Date : " + releveOuverture1.getDateDeReleve());
        System.out.println("Index : " + releveOuverture1.getIndex());
        System.out.println("Nombre de relevés : " + contrat1.getReleves().size());

        System.out.println("");

        System.out.println("Relevé ajouté : " + releveOuverture2.getTypeReleve());
        System.out.println("Date : " + releveOuverture2.getDateDeReleve());
        System.out.println("Index : " + releveOuverture2.getIndex());
        System.out.println("Nombre de relevés : " + contrat2.getReleves().size());

        System.out.println("");

        // AJOUT D'UN RELEVE MENSUEL

        Releve releveMensuel1 = gestionnaireReleves.genererReleveMensuel(contrat1, LocalDate.of(2025, 3, 31), simulateurIndex);
        Releve releveMensuel2 = gestionnaireReleves.genererReleveMensuel(contrat2, LocalDate.of(2024, 6, 30), simulateurIndex);

        System.out.println("Relevé ajouté : " + releveMensuel1.getTypeReleve());
        System.out.println("Date : " + releveMensuel1.getDateDeReleve());
        System.out.println("Index : " + releveMensuel1.getIndex());
        System.out.println("Nombre de relevés : " + contrat1.getReleves().size());

        System.out.println("");

        System.out.println("Relevé ajouté : " + releveMensuel2.getTypeReleve());
        System.out.println("Date : " + releveMensuel2.getDateDeReleve());
        System.out.println("Index : " + releveMensuel2.getIndex());
        System.out.println("Nombre de relevés : " + contrat2.getReleves().size());

        System.out.println("");

        // CALCUL DE LA CONSOMMATION

        Map<TypeConso, Double> consommation1 = releveMensuel1.calculerConsommation(releveOuverture1);
        Map<TypeConso, Double> consommation2 = releveMensuel2.calculerConsommation(releveOuverture2);

        System.out.println("Consommation calculée (Offre Classique) :");
        System.out.println("TOTAL : " + consommation1.get(TypeConso.TOTAL));
        System.out.println("Consommation calculée (Offre HP/HC):");
        System.out.println("HP : " + consommation2.get(TypeConso.HP));
        System.out.println("HC : " + consommation2.get(TypeConso.HC));








    }
}
