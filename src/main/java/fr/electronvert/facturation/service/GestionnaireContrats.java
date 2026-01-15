package fr.electronvert.facturation.service;

import fr.electronvert.facturation.exception.ContratAvecFacturesImpayeesException;
import fr.electronvert.facturation.model.contrat.*;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GestionnaireContrats {

    private final List<Contrat> contrats = new ArrayList<>();
    private final GestionnaireTarifs gestionnaireTarifs;


    public GestionnaireContrats(GestionnaireTarifs gestionnaireTarifs) {
        this.gestionnaireTarifs = gestionnaireTarifs;
    }


    public List<Contrat> getContrats() {
        return List.copyOf(contrats);
    }

    public Contrat creerContrat(Client client, String adressePostale,
                                OffreTarifaire offre,
                                ModeFacturation modeFacturation,
                                LocalDate dateSouscription) {

        Contrat contrat = new Contrat(client, adressePostale, offre, modeFacturation, dateSouscription);

        contrats.add(contrat);

        if (modeFacturation == ModeFacturation.ECHEANCIER) {
            creerEcheancier(contrat);
        }

        return contrat;
    }


    public List<Contrat> getContratsActifs(){
        List<Contrat> contratsActifs = new ArrayList<>();
        for (Contrat contrat : contrats) {
            if(contrat.estActif()){
                contratsActifs.add(contrat);
            }
        }
        return contratsActifs;
    }



    // =====================
    // ÉCHÉANCIER
    // =====================

    // TODO : reovir le local date now avec mon simulateur de date

    public void creerEcheancier(Contrat contrat) {

        double estimationAnnuelleHT = estimerCoutAnnuel(contrat, contrat.getDateSouscription());

        Echeancier echeancier = new Echeancier(
                LocalDate.now(),
                estimationAnnuelleHT
        );

        contrat.attacherNouvelEcheancier(echeancier);
    }




    // =====================
    // CHANGEMENT DE MODE
    // =====================

    public void demanderChangementModeFacturation(
            Contrat contrat,
            ModeFacturation nouveauMode,
            LocalDate dateDemande
    ) {
        if (contrat == null || nouveauMode == null || dateDemande == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        if (!contrat.changementModeFacturationPossible(dateDemande)) {
            throw new IllegalStateException(
                    "Demande de changement autorisée uniquement le mois précédent l'anniversaire"
            );
        }

        contrat.planifierChangementModeFacturation(nouveauMode);
    }


    public void demanderChangementOffreTarifaire(
            Contrat contrat,
            OffreTarifaire nouvelleOffre,
            LocalDate dateDemande
    ) {
        if (contrat == null || nouvelleOffre == null || dateDemande == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        if (!contrat.changementOffreGratuit(dateDemande)) {
            contrat.ajouterFraisChangementOffre(
                    OffreTarifaire.getFraisChangementOffreHT()
            );
        }

        contrat.planifierChangementOffreTarifaire(nouvelleOffre);
    }

    public void appliquerChangementsPlanifiesModeFacturation(Contrat contrat) {

        ModeFacturation ancienMode = contrat.getModeFacturation();

        contrat.appliquerChangementModeFacturation();
        contrat.appliquerChangementOffre();

        ModeFacturation nouveauMode = contrat.getModeFacturation();

        if (ancienMode != ModeFacturation.ECHEANCIER
                && nouveauMode == ModeFacturation.ECHEANCIER) {

            creerEcheancier(contrat);
        }

        if (ancienMode == ModeFacturation.ECHEANCIER
                && nouveauMode != ModeFacturation.ECHEANCIER) {

            contrat.supprimerEcheancier();
        }
    }



    // =====================
    // ESTIMATION ANNUELLE
    // =====================

    private double estimerCoutAnnuel(Contrat contrat,         LocalDate dateEstimation
    ) {


        // 1️⃣ Estimation de la consommation annuelle
        Map<TypeConso, Double> consommationAnnuelle;

        if (contrat.getReleves().isEmpty()) {
            consommationAnnuelle = estimerConsommationParDefaut(contrat);
        } else {
            consommationAnnuelle = calculerConsommationAnneePrecedente(contrat);

            System.out.println("Consommation annuelle réelle calculée : "
                    + consommationAnnuelle);
        }

        // 2️⃣ Tarif en vigueur
        Tarif tarif = gestionnaireTarifs.getTarifActif(dateEstimation);

        // 3️⃣ Calcul du coût de l'électricité via l'offre
        double coutElectriciteHT =
                contrat.getOffreTarifaire()
                        .calculerCoutElectricite(consommationAnnuelle, tarif);

        // 4️⃣ Abonnement annuel
        double abonnementAnnuelHT =
                contrat.getOffreTarifaire().calculerCoutAbonnementAnnuel(tarif) ;

        return coutElectriciteHT + abonnementAnnuelHT;
    }

    private Map<TypeConso, Double> estimerConsommationParDefaut(Contrat contrat) {

        double consoMoyenneAnnuelle = 4500;

        if (contrat.getOffreTarifaire() instanceof OffreClassique) {
            return Map.of(TypeConso.TOTAL, consoMoyenneAnnuelle);
        }

        if (contrat.getOffreTarifaire() instanceof OffreHPHC) {
            return Map.of(
                    TypeConso.HP, consoMoyenneAnnuelle * 0.6,
                    TypeConso.HC, consoMoyenneAnnuelle * 0.4
            );
        }

        throw new IllegalStateException("Offre tarifaire inconnue");
    }


    private Map<TypeConso, Double> calculerConsommationAnneePrecedente(
            Contrat contrat
    ) {
        List<Releve> releves = new ArrayList<>(contrat.getReleves());
        Collections.sort(releves);

        if (releves.size() < 2) {
            throw new IllegalStateException(
                    "Relevés insuffisants pour calculer une consommation annuelle"
            );
        }

        Releve debut = releves.get(0);               // plus ancien
        Releve fin = releves.get(releves.size() - 1); // plus récent

        return fin.calculerConsommation(debut);
    }




    public void cloturerContrat(Contrat contrat, LocalDate dateCloture) {

        if (!contrat.estActif()) {
            throw new IllegalStateException("Le contrat est déjà clôturé");
        }

        if (contrat.aDesFacturesImpayees()) {
            throw new ContratAvecFacturesImpayeesException(
                    contrat.getReference(),
                    contrat.getFacturesImpayees()
            );
        }

        contrat.cloturer(dateCloture);
    }



}
