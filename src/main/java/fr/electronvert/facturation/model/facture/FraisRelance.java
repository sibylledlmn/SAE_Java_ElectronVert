package fr.electronvert.facturation.model.facture;

import java.time.LocalDate;

public class FraisRelance {

    public static final double MONTANT_HT = 15.0;

    private final LocalDate dateRelance;
    private final double montantHT;
    private final double montantTVA;
    private final double montantTTC;
    private final int numeroRelance;
    private String libelle;

    public FraisRelance(int numeroRelance, LocalDate dateRelance) {

        if (numeroRelance <= 0) {
            throw new IllegalArgumentException("Le numéro de relance doit être positif");
        }
        if (dateRelance == null) {
            throw new IllegalArgumentException("La date de relance ne peut pas être nulle");
        }


        this.numeroRelance = numeroRelance;
        this.dateRelance = dateRelance;
        this.montantHT = MONTANT_HT;
        this.montantTVA = TauxTVA.NORMAL.calculerMontantTVA(montantHT);
        this.montantTTC = montantHT + montantTVA;
        this.libelle = "Frais de relance n°" + numeroRelance;

    }

    public double getMontantTTC() {
        return montantTTC;
    }

    public LocalDate getDateRelance() {
        return dateRelance;
    }

    public double getMontantHT() {
        return montantHT;
    }

    public double getMontantTVA() {
        return montantTVA;
    }

    public int getNumeroRelance() {
        return numeroRelance;
    }

    public String getLibelle() {
        return libelle;
    }





    @Override
    public String toString() {
        return libelle + " - " + String.format("%.2f €", montantTTC);
    }

}
