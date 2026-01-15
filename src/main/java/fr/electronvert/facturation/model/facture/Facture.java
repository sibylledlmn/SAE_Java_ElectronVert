package fr.electronvert.facturation.model.facture;


import fr.electronvert.facturation.exception.FactureDejaPayeeException;
import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.releve.TypeReleve;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Facture métier figée.
 * <p>
 * Les montants sont calculés en amont par le GestionnaireFacture
 * à partir des offres tarifaires et des tarifs en vigueur.
 * La facture ne fait que stocker les résultats et gérer son cycle de vie.
 */
public class Facture implements Comparable<Facture> {


    private final String reference;
    private final TypeFacture type;
    private final Contrat contrat;

    private final LocalDate dateEmission;
    private final LocalDate dateEcheance;
    private LocalDate dateProchaineRelance;

    private double montantHT;
    private double montantTVA;
    private double montantTTC;
    private boolean montantsDefinis = false;

    private StatutFacture statut;

    private Paiement paiement;

    private final List<FraisRelance> fraisDeRelance = new ArrayList<>();
    private boolean contientFraisChangementOffre = false;


    // ---------- CONSTRUCTEUR ----------

    public Facture(
            Contrat contrat,
            LocalDate dateEmission,
            String reference,
            TypeFacture type
    ) {
        if (contrat == null) {
            throw new IllegalArgumentException("Une facture doit être liée à un contrat");
        }

        if (contrat.estFacturationTerminee()) {
            throw new IllegalStateException(
                    "La facturation du contrat est déjà terminée"
            );
        }


        if (!contrat.estActif()
                && type != TypeFacture.REGULARISATION
                && !(type == TypeFacture.MENSUELLE
                && contrat.getDernierReleve() != null
                && contrat.getDernierReleve().getTypeReleve() == TypeReleve.CLOTURE)) {

            throw new IllegalStateException(
                    "Impossible de créer une facture pour un contrat clôturé"
            );
        }


        if (dateEmission == null) {
            throw new IllegalArgumentException("La date d'émission ne peut pas être nulle");
        }
        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException("La référence de la facture est invalide");
        }
        if (type == null) {
            throw new IllegalArgumentException("Le type de facture ne peut pas être nul");
        }

        this.contrat = contrat;
        this.dateEmission = dateEmission;
        this.dateEcheance = dateEmission.plusDays(14);
        this.reference = reference;
        this.type = type;
        this.statut = StatutFacture.EMISE;

        contrat.ajouterFacture(this);

    }

    // ---------- MÉTIER ----------

    /**
     * Définit les montants de la facture.
     * Appel unique autorisé.
     */
    public void definirMontants(double montantHT, double montantTVA, double montantTTC) {
        if (montantsDefinis) {
            throw new IllegalStateException("Les montants sont déjà définis");
        }
        if (montantHT < 0 || montantTVA < 0 || montantTTC < 0) {
            throw new IllegalArgumentException("Les montants ne peuvent pas être négatifs");
        }
        if (Math.abs((montantHT + montantTVA) - montantTTC) > 0.01) {
            throw new IllegalArgumentException("Incohérence entre HT, TVA et TTC");
        }

        this.montantHT = montantHT;
        this.montantTVA = montantTVA;
        this.montantTTC = montantTTC;
        this.montantsDefinis = true;
    }

    public void marquerCommePayee(){
        if (statut == StatutFacture.PAYEE) {
            throw new IllegalStateException("La facture est déjà payée");
        }
        this.statut = StatutFacture.PAYEE;
    }

    public void passerEnImpayee(LocalDate datePassageImpayee) {
        if (statut == StatutFacture.PAYEE) {
            throw new IllegalStateException("La facture est déjà payée");
        }
        if (statut == StatutFacture.IMPAYEE) {
            throw new IllegalStateException("La facture est déjà impayée");
        }
        this.statut = StatutFacture.IMPAYEE;
        this.dateProchaineRelance = datePassageImpayee;
    }

    public void planifierProchaineRelance(LocalDate dateRelance) {
        this.dateProchaineRelance = dateRelance.plusWeeks(3);
    }

    public void ajouterFraisDeRelance(FraisRelance frais) {
        if (frais == null) {
            throw new IllegalArgumentException("Le frais de relance ne peut pas être nul");
        }
        this.fraisDeRelance.add(frais);
    }

    public void enregistrerPaiement(Paiement paiement) {
        if (paiement == null) {
            throw new IllegalArgumentException("Le paiement ne peut pas être nul");
        }
        if (statut == StatutFacture.PAYEE) {
            throw new FactureDejaPayeeException(
                    reference,
                    this.paiement != null ? this.paiement.getDatePaiement() : null
            );
        }
        this.paiement = paiement;
        this.statut = StatutFacture.PAYEE;
    }

    public void marquerPresenceFraisChangementOffre() {
        this.contientFraisChangementOffre = true;
    }

    public double getMontantTotalTTCAPayer() {
        double total = montantTTC;
        for (FraisRelance frais : fraisDeRelance) {
            total += frais.getMontantTTC();
        }
        return total;
    }

    /**
     * Résumé court (listes, UI).
     */
    public String toResume() {
        return String.format(
                "%s | %s | %.2f € | %s",
                reference,
                dateEmission,
                getMontantTotalTTCAPayer(),
                statut
        );
    }

    public String toDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== FACTURE ===\n")
                .append("Référence : ").append(reference).append("\n")
                .append("Type : ").append(type).append("\n")
                .append("Date émission : ").append(dateEmission).append("\n")
                .append("Date échéance : ").append(dateEcheance).append("\n")
                .append("Statut : ").append(statut).append("\n\n")
                .append("--- MONTANTS ---\n")
                .append(String.format("HT : %.2f €\n", montantHT))
                .append(String.format("TVA : %.2f €\n", montantTVA))
                .append(String.format("TTC : %.2f €\n", montantTTC));

        if (!fraisDeRelance.isEmpty()) {
            sb.append("\n\n--- FRAIS DE RELANCE ---\n");
            for (FraisRelance frais : fraisDeRelance) {
                sb.append(frais).append("\n");
            }
            sb.append(String.format(
                    "TOTAL À PAYER : %.2f €\n",
                    getMontantTotalTTCAPayer()
            ));
        }

        if (contientFraisChangementOffre) {
            sb.append("\n--- INFORMATION ---\n")
                    .append("Cette facture inclut des frais de changement d'offre.\n");
        }

        return sb.toString();
    }

    public String getReference() {
        return reference;
    }

    public TypeFacture getType() {
        return type;
    }

    public Contrat getContrat() {
        return contrat;
    }

    public LocalDate getDateEmission() {
        return dateEmission;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public LocalDate getDateProchaineRelance() {
        return dateProchaineRelance;
    }

    public StatutFacture getStatut() {
        return statut;}

    public List<FraisRelance> getFraisDeRelance() {
        return Collections.unmodifiableList(fraisDeRelance);
    }

    public double getMontantHT() {
        return montantHT;
    }

    public double getMontantTVA() {
        return montantTVA;
    }

    public double getMontantTTC() {
        return montantTTC;
    }

    // ---------- UTILITAIRES ----------

    @Override
    public int compareTo(Facture autre) {
        int cmp = this.dateEmission.compareTo(autre.dateEmission);
        if (cmp != 0) return cmp;
        return this.reference.compareTo(autre.reference);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Facture facture)) return false;
        return reference.equals(facture.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }
}




