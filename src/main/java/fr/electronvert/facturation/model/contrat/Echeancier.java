package fr.electronvert.facturation.model.contrat;

import fr.electronvert.facturation.model.facture.TauxTVA;

import java.time.LocalDate;

public class Echeancier {

    private LocalDate dateDebut;
    private LocalDate dateFin;

    public static final int NOMBRE_MENSUALITES = 11;
    private int nbMensualitesEmises;

    private double montantMensualite;

    private boolean termine;

    public Echeancier(LocalDate dateDebut, double estimationAnnuelleHT) {
        if (estimationAnnuelleHT <= 0) {
            throw new IllegalArgumentException("Estimation annuelle invalide");
        }

        this.dateDebut = dateDebut;
        this.dateFin = dateDebut.plusMonths(NOMBRE_MENSUALITES + 1);

        this.montantMensualite = estimationAnnuelleHT / NOMBRE_MENSUALITES;
        this.nbMensualitesEmises = 0;
        this.termine = false;
    }


    public boolean peutEmettreMensualite() {
        return !termine && nbMensualitesEmises < NOMBRE_MENSUALITES;
    }

    public void enregistrerMensualiteEmise() {
        if (!peutEmettreMensualite()) {
            throw new IllegalStateException("Aucune mensualité ne peut être émise");
        }
        nbMensualitesEmises++;
    }

    public boolean doitDeclencherRegularisation() {
        return !termine && nbMensualitesEmises == NOMBRE_MENSUALITES;
    }

    public void terminer() {
        this.termine = true;
    }


    public double getMontantMensualite() {
        return montantMensualite;
    }

    public int getMensualitesEmises() {
        return nbMensualitesEmises;
    }

    public int getMensualitesRestantes() {
        return NOMBRE_MENSUALITES - nbMensualitesEmises;
    }

    public boolean estTermine() {
        return termine;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }


    public double getMontantMensualiteTTC() {
        return TauxTVA.NORMAL.calculerTTC(montantMensualite);
    }

}



