package fr.electronvert.facturation.model.facture;

import java.time.LocalDate;

public class Paiement {
    private static int compteur = 0;

    private final String id;
    private final LocalDate datePaiement;
    private final double montantPaye;

    public Paiement(LocalDate datePaiement, double montantPaye) {
        if (datePaiement == null) {
            throw new IllegalArgumentException("La date de paiement ne peut pas être nulle");
        }
        if (montantPaye <= 0) {
            throw new IllegalArgumentException("Le montant payé doit être positif");
        }

        this.id = genererReference();
        this.datePaiement = datePaiement;
        this.montantPaye = montantPaye;
    }

    private static String genererReference() {
        compteur++;
        return String.format("PAIE-%04d", compteur);
    }

    public static int getCompteur() {
        return compteur;
    }

    public String getId() {
        return id;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public double getMontantPaye() {
        return montantPaye;
    }
}
