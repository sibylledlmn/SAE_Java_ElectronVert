package fr.electronvert.facturation.service;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.contrat.ModeFacturation;

import java.time.LocalDate;

public class SimulateurDate {

    private LocalDate dateCourante;

    private final GestionnaireContrats gestionnaireContrats;
    private final GestionnaireReleves gestionnaireReleves;
    private final GestionnaireFactures gestionnaireFactures;
    private final GestionnairePaiements gestionnairePaiements;
    private final GestionnaireRelances gestionnaireRelances;
    private final SimulateurIndex simulateurIndex;

    public SimulateurDate(
            LocalDate dateInitiale,
            GestionnaireContrats gestionnaireContrats,
            GestionnaireReleves gestionnaireReleves,
            GestionnaireFactures gestionnaireFactures,
            GestionnairePaiements gestionnairePaiements,
            GestionnaireRelances gestionnaireRelances,
            SimulateurIndex simulateurIndex
    ) {
        this.dateCourante = dateInitiale;
        this.gestionnaireContrats = gestionnaireContrats;
        this.gestionnaireReleves = gestionnaireReleves;
        this.gestionnaireFactures = gestionnaireFactures;
        this.gestionnairePaiements = gestionnairePaiements;
        this.gestionnaireRelances = gestionnaireRelances;
        this.simulateurIndex = simulateurIndex;
    }

    public LocalDate getDateCourante() {
        return dateCourante;
    }

    public void avancerDate() {
        dateCourante = dateCourante.plusDays(1);

        // Tous les jours, passage des factures en impayées, génération de relances

        gestionnaireFactures.verifierEcheances(dateCourante);
        gestionnaireRelances.traiterRelances(gestionnaireFactures.getFacturesImpayees(), dateCourante);

        // 1er du mois : application des changements d’offre
        if (dateCourante.getDayOfMonth() == 1) {
            appliquerChangementsOffre();
        }

        // 5 du mois : factures + régularisation
        if (dateCourante.getDayOfMonth() == 5) {
            genererFacturesMensuelles();
            genererRegularisations();
        }

        // 6 du mois : application des changements de mode de facturation
        if (dateCourante.getDayOfMonth() == 6) {
            appliquerChangementsMode();
        }

        // 20 du mois : prélèvement des mensualités
        if (dateCourante.getDayOfMonth() == 20) {
            preleverMensualites();
        }

        // Fin de mois : relevés de compteur
        if (dateCourante.getDayOfMonth() == dateCourante.lengthOfMonth()) {
            genererRelevesMensuels();
        }
    }

    public void avancerDe(int nbJours) {
        for (int i = 0; i < nbJours; i++) {
            avancerDate();
        }
    }



    private void appliquerChangementsOffre() {
        for (Contrat contrat : gestionnaireContrats.getContratsActifs()) {
            contrat.appliquerChangementOffre();
        }
    }

    private void appliquerChangementsMode() {
        for (Contrat contrat : gestionnaireContrats.getContratsActifs()) {
            gestionnaireContrats.appliquerChangementsPlanifiesModeFacturation(contrat);
        }
    }

    private void genererFacturesMensuelles() {
        for (Contrat contrat : gestionnaireContrats.getContratsActifs()) {
            if (contrat.getModeFacturation() == ModeFacturation.REEL) {
                gestionnaireFactures.creerFactureMensuelle(
                        contrat,
                        dateCourante
                );
            }
        }
    }

    private void genererRegularisations() {
        for (Contrat contrat : gestionnaireContrats.getContratsActifs()) {
            if (contrat.getModeFacturation() == ModeFacturation.ECHEANCIER
                    && contrat.getEcheancier() != null
                    && contrat.getEcheancier().doitDeclencherRegularisation()) {

                gestionnaireFactures.regulariserFinEcheancier(
                        contrat,
                        dateCourante
                );

                // nouvel échéancier basé sur consommation réelle
                gestionnaireContrats.creerEcheancier(contrat);
            }
        }
    }

    private void preleverMensualites() {
        for (Contrat contrat : gestionnaireContrats.getContratsActifs()) {
            if (contrat.getModeFacturation() == ModeFacturation.ECHEANCIER) {
                gestionnairePaiements.preleverMensualite(
                        contrat,
                        dateCourante
                );
            }
        }
    }

    private void genererRelevesMensuels() {
        for (Contrat contrat : gestionnaireContrats.getContratsActifs()) {
            gestionnaireReleves.genererReleveMensuel(
                    contrat,
                    dateCourante,
                    simulateurIndex
            );
        }
    }
}
