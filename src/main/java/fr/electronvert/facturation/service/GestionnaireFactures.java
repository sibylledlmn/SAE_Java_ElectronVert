package fr.electronvert.facturation.service;

import fr.electronvert.facturation.exception.ContratInactifException;
import fr.electronvert.facturation.exception.FactureDejaExistantePourMoisException;
import fr.electronvert.facturation.exception.RelevesInsuffisantsException;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.Echeancier;
import fr.electronvert.facturation.model.contrat.ModeFacturation;
import fr.electronvert.facturation.model.contrat.OffreTarifaire;
import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.StatutFacture;
import fr.electronvert.facturation.model.facture.TauxTVA;
import fr.electronvert.facturation.model.facture.TypeFacture;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.releve.TypeReleve;
import fr.electronvert.facturation.model.tarif.Tarif;
import fr.electronvert.facturation.model.utilisateur.Client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GestionnaireFactures {

    private final GestionnaireTarifs gestionnaireTarifs;
    private final List<Facture> factures = new ArrayList<>();

    public GestionnaireFactures(GestionnaireTarifs gestionnaireTarifs) {
        if (gestionnaireTarifs == null) {
            throw new IllegalArgumentException("Le gestionnaire de tarifs est requis");
        }
        this.gestionnaireTarifs = gestionnaireTarifs;
    }


    // TODO : revoir les exceptions
    public Facture creerFactureMensuelle(
            Contrat contrat,
            LocalDate dateEmission
    ) {

        if (contrat == null  || dateEmission == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }
        if (!contrat.estActif() && contrat.getDernierReleve().getTypeReleve() != TypeReleve.CLOTURE) {
            throw new ContratInactifException(
                    contrat.getReference(),
                    contrat.getDateFin()
            );        }

        OffreTarifaire offreTarifaire = contrat.getOffreTarifaire();
        if (offreTarifaire == null) {
            throw new IllegalStateException("Aucune offre tarifaire associée au contrat");
        }


        LocalDate moisFacture = dateEmission.withDayOfMonth(1);
        Facture factureExistante = rechercherFacturePourMois(
                contrat,
                TypeFacture.MENSUELLE,
                moisFacture
        );

        if (factureExistante != null) {
            throw new FactureDejaExistantePourMoisException(
                    contrat.getReference(),
                    TypeFacture.MENSUELLE,
                    moisFacture,
                    factureExistante.getReference()
            );
        }

        // -------- Relevés --------
        Releve dernier = contrat.getDernierReleve();
        Releve precedent = contrat.getAvantDernierReleve();

        int nbReleves = (dernier != null ? 1 : 0) + (precedent != null ? 1 : 0);
        if (dernier == null || precedent == null) {
            throw new RelevesInsuffisantsException(
                    contrat.getReference(),
                    nbReleves
            );
        }

        // -------- Tarif actif --------
        Tarif tarifActif = gestionnaireTarifs.getTarifActif(dateEmission);

        // -------- Consommation --------
        Map<TypeConso, Double> consommations =
                offreTarifaire.calculerConsommation(precedent, dernier);


        // -------- Coûts HT --------
        double coutElectriciteHT =
                offreTarifaire.calculerCoutElectricite(consommations, tarifActif);

        double coutAbonnementMensuelHT =
                offreTarifaire.calculerCoutAbonnementMensuel(tarifActif);

        // ---------- TVA ----------

        double montantHT = coutElectriciteHT + coutAbonnementMensuelHT;
        double montantTVA = TauxTVA.NORMAL.calculerMontantTVA(montantHT);

        // ---------- Frais de changement d’offre ----------
        boolean contientFraisChangementOffre = false;

        if (contrat.getFraisChangementOffreEnAttente() > 0) {
            double fraisHT = contrat.getFraisChangementOffreEnAttente();
            double fraisTVA = TauxTVA.NORMAL.calculerMontantTVA(fraisHT);

            montantHT += fraisHT;
            montantTVA += fraisTVA;

            contrat.reinitialiserFraisChangementOffre();
            contientFraisChangementOffre = true;
        }

        double montantTTC = montantHT + montantTVA;



        // ---------- Référence ----------
        String reference = genererReferenceFacture(
                contrat,
                TypeFacture.MENSUELLE,
                moisFacture
        );

        // ---------- Création facture ----------
        Facture facture = new Facture(
                contrat,
                dateEmission,
                reference,
                TypeFacture.MENSUELLE
        );

        facture.definirMontants(montantHT, montantTVA, montantTTC);

        if (contientFraisChangementOffre) {
            facture.marquerPresenceFraisChangementOffre();
        }

        factures.add(facture);
        contrat.ajouterFacture(facture);
        if (!contrat.estActif()
                && contrat.getDernierReleve().getTypeReleve() == TypeReleve.CLOTURE) {
            contrat.marquerFacturationTerminee();
        }
        return facture;
    }

    public void verifierEcheances(LocalDate date) {
        for (Facture facture : factures) {
            if (facture.getStatut() == StatutFacture.EMISE
                    && date.isAfter(facture.getDateEcheance())) {
                facture.passerEnImpayee(date);
            }
        }
    }

    // FACTURES DE REGULARISATION

// TODO : vérifier que fonctionne pour cloture anticipée

    public Facture creerFactureRegularisation(
            Contrat contrat,
            LocalDate dateEmission
    ) {
        double regularisation;

        if (!contrat.estActif()) {
            regularisation = calculerRegularisationCloture(contrat);
        } else {
            regularisation = calculerRegularisationFinEcheancier(contrat);
        }

        LocalDate moisFacture = dateEmission.withDayOfMonth(1);

        String reference = genererReferenceFacture(
                contrat,
                TypeFacture.REGULARISATION,
                moisFacture
        );

        Facture facture = new Facture(
                contrat,
                dateEmission,
                reference,
                TypeFacture.REGULARISATION
        );

        // 🔹 Cas favorable au client
        if (regularisation < 0) {
            if (!contrat.estActif()) {
                // facture négative remboursée
                double montantHT = regularisation;
                double montantTVA = 0;
                double montantTTC = regularisation;

                facture.definirMontants(montantHT, montantTVA, montantTTC);
                facture.marquerCommePayee();
            } else {
                // solde créditeur
                contrat.ajouterSoldeCrediteur(regularisation);
                facture.definirMontants(0, 0, 0);
                facture.marquerCommePayee();
            }
        }
        // 🔹 Cas défavorable au client
        else {
            double montantHT = regularisation;
            double montantTVA = TauxTVA.NORMAL.calculerMontantTVA(montantHT);
            double montantTTC = montantHT + montantTVA;

            facture.definirMontants(montantHT, montantTVA, montantTTC);
        }

        factures.add(facture);
        contrat.ajouterFacture(facture);

        // fin de l’échéancier si présent
        if (contrat.getEcheancier() != null) {
            contrat.getEcheancier().terminer();
        }

        contrat.marquerFacturationTerminee();
        return facture;
    }


    public Facture regulariserFinEcheancier(Contrat contrat, LocalDate date) {
        double montant = calculerRegularisationFinEcheancier(contrat);
        return creerFactureRegularisation(contrat, date);
    }

    public Facture regulariserClotureAnticipee(Contrat contrat, LocalDate date) {
        Releve dernier = contrat.getDernierReleve();

        if (contrat.getModeFacturation() == ModeFacturation.REEL) {
            throw new IllegalStateException(
                    "Aucune régularisation n’est nécessaire pour un contrat en facturation réelle"
            );
        }

        if (dernier == null || dernier.getTypeReleve() != TypeReleve.CLOTURE) {
            throw new IllegalStateException(
                    "Un relevé de clôture est requis pour la régularisation d’un contrat clôturé"
            );
        }


        double montant = calculerRegularisationCloture(contrat);
        return creerFactureRegularisation(contrat, date);
    }


    public double calculerRegularisationFinEcheancier(Contrat contrat){
        Echeancier echeancier = contrat.getEcheancier();
        if(echeancier == null || !echeancier.doitDeclencherRegularisation()){
            throw new IllegalStateException("Régularisation annuelle non déclenchable");
        }
        List<Releve> releves = contrat.getReleves();

        if(releves.size() < 2){
            throw new IllegalStateException("Relevés insuffisants pour la régularisation");
        }
        double montantReel = calculerCoutReel(contrat, releves);
        double montantDejaPaye = calculerMontantMensualites(echeancier);

//        echeancier.terminer();

        return montantReel - montantDejaPaye;
    }

    public double calculerRegularisationCloture(Contrat contrat) {
        if (contrat.estActif()) {
            throw new IllegalStateException(
                    "Le contrat doit être clôturé pour régularisation"
            );
        }

        Echeancier echeancier = contrat.getEcheancier();
        if (echeancier == null) {
            return 0.0;
        }

        double montantReel = calculerCoutReel(contrat, contrat.getReleves());
        double montantDejaPaye = calculerMontantMensualites(echeancier);

        return montantReel - montantDejaPaye;
    }


    private double calculerCoutReel(
            Contrat contrat,
            List<Releve> releves
    ) {
        List<Releve> relevesTries = new ArrayList<>(releves);
        Collections.sort(relevesTries);

        double total = 0.0;

        for (int i = 1; i < relevesTries.size(); i++) {

            Releve dernier = relevesTries.get(i);
            Releve precedent = relevesTries.get(i - 1);

            Map<TypeConso, Double> consommation =
                    contrat.getOffreTarifaire()
                            .calculerConsommation(precedent, dernier);

            Tarif tarif = gestionnaireTarifs
                    .getTarifActif(dernier.getDateDeReleve());

            total += contrat.getOffreTarifaire()
                    .calculerCoutElectricite(consommation, tarif);

            total += contrat.getOffreTarifaire()
                    .calculerCoutAbonnementMensuel(tarif);
        }

        return total;
    }

    private double calculerMontantMensualites(Echeancier echeancier) {

        if (echeancier == null) {
            throw new IllegalArgumentException("Échéancier inexistant");
        }

        return echeancier.getMensualitesEmises()
                * echeancier.getMontantMensualite();
    }




        public List<Facture> getFactures() {
        return Collections.unmodifiableList(factures);
    }

    public List<Facture> getFacturesImpayees() {
        List<Facture> resultat = new ArrayList<>();
        for (Facture f : factures) {
            if (f.getStatut() == StatutFacture.IMPAYEE) {
                resultat.add(f);
            }
        }
        return resultat;
    }

    public Facture rechercherParReference(String reference) {
        for (Facture f : factures) {
            if (f.getReference().equals(reference)) {
                return f;
            }
        }
        return null;
    }

    public List<Facture> getFacturesParClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas être nul");
        }

        List<Facture> resultat = new ArrayList<>();
        for (Facture f : factures) {
            if(f.getContrat().getClient().equals(client)) {
                resultat.add(f);
            }
        }
        return resultat;
    }


//    private boolean factureExistePourMois(
//            Contrat contrat,
//            TypeFacture type,
//            LocalDate mois
//    ) {
//        for (Facture f : factures) {
//            if (f.getContrat().equals(contrat)
//                    && f.getType() == type
//                    && f.getDateEmission().withDayOfMonth(1).equals(mois)) {
//                return true;
//            }
//        }
//        return false;
//    }

    private Facture rechercherFacturePourMois(
            Contrat contrat,
            TypeFacture type,
            LocalDate mois
    ) {
        return factures.stream()
                .filter(f -> f.getContrat().equals(contrat)
                        && f.getType() == type
                        && f.getDateEmission().withDayOfMonth(1).equals(mois))
                .findFirst()
                .orElse(null);
    }


// TODO : trop lourd la référence avec l'id du contrat, à revoir

    public String genererReferenceFacture(
            Contrat contrat,
            TypeFacture type,
            LocalDate dateEmission
    ) {
        String base = "FACT-%s-%s-%d-%02d"
                .formatted(
                        contrat.getId(),
                        type.name(),
                        dateEmission.getYear(),
                        dateEmission.getMonthValue()
                );

        long compteur = factures.stream()
                .filter(f -> f.getReference().startsWith(base))
                .count();

        return base + "-" + String.format("%03d", compteur + 1);
    }

}
