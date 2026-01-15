package fr.electronvert.facturation.model.contrat;

import fr.electronvert.facturation.model.facture.Facture;
import fr.electronvert.facturation.model.facture.StatutFacture;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.utilisateur.Client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Contrat {

    private final UUID id;
    private static int compteurContrats = 1;

    private final String reference;


    private Client client;
    private String adressePostale;

    private OffreTarifaire offreTarifaire;
    private OffreTarifaire offreTarifaireFuture;
    private ModeFacturation modeFacturation;
    private ModeFacturation modeFacturationFutur;
    private Echeancier echeancier;

    private StatutContrat statut;

    private LocalDate dateSouscription;
    private LocalDate dateFin;

    private String numeroCompteur; // optionnel, informatif

    private double fraisChangementOffreEnAttente;
    private double soldeCrediteur;

    private final List<Facture> factures = new ArrayList<>();
    private final List<Releve> releves = new ArrayList<>();

    private boolean facturationTerminee = false;



    public Contrat(Client client,
                   String adressePostale,
                   OffreTarifaire offreTarifaire,
                   ModeFacturation modeFacturation,
                   LocalDate dateSouscription) {

        this.id = UUID.randomUUID();
        this.reference = genererReference(dateSouscription);
        this.client = client;
        this.adressePostale = adressePostale;
        this.offreTarifaire = offreTarifaire;
        this.modeFacturation = modeFacturation;
        this.dateSouscription = dateSouscription;
        this.statut = StatutContrat.ACTIF;
        this.fraisChangementOffreEnAttente = 0.0;

    }


    private String genererReference(LocalDate date) {
        return String.format(
                "CTR-%d-%06d",
                date.getYear(),
                compteurContrats++
        );
    }


    public UUID getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public String getAdressePostale() {
        return adressePostale;
    }

    public OffreTarifaire getOffreTarifaire() {
        return offreTarifaire;
    }

    public ModeFacturation getModeFacturation() {
        return modeFacturation;
    }

    public StatutContrat getStatut() {
        return statut;
    }

    public LocalDate getDateSouscription() {
        return dateSouscription;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public double getFraisChangementOffreEnAttente() {
        return fraisChangementOffreEnAttente;
    }


    public List<Facture> getFactures() {
        return Collections.unmodifiableList(factures);
    }

    public List<Facture> getFacturesImpayees() {
        List<Facture> impayees = getFactures().stream()
                .filter(f -> f.getStatut() == StatutFacture.IMPAYEE)
                .collect(Collectors.toList());
        return  Collections.unmodifiableList(impayees);
    }

    public List<Releve> getReleves() {
        return Collections.unmodifiableList(releves);
    }

    // =====================
    // MÉTHODES MÉTIER SIMPLES
    // =====================

    public boolean estActif() {
        return statut == StatutContrat.ACTIF;
    }

    public void ajouterFacture(Facture facture) {
        factures.add(facture);
    }

    public void ajouterReleve(Releve releve) {
        releves.add(releve);
    }

    public void attacherNouvelEcheancier(Echeancier nouvelEcheancier) {

        if (this.echeancier != null) {
            this.echeancier.terminer();
        }

        this.echeancier = nouvelEcheancier;
    }

    public void supprimerEcheancier() {
        if (this.echeancier != null) {
            this.echeancier.terminer();
            this.echeancier = null;
        }

    }


    // =====================
    // FRAIS PONCTUELS
    // =====================


    public void ajouterFraisChangementOffre(double montant) {
        this.fraisChangementOffreEnAttente += montant;
    }

    public boolean changementOffreGratuit(LocalDate dateDemande) {
        LocalDate dateEffet = dateDemande.plusMonths(1);
        return dateEffet.getMonth() == dateSouscription.getMonth();
    }


    public void reinitialiserFraisChangementOffre() {
        this.fraisChangementOffreEnAttente = 0.0;
    }


    public void appliquerChangementOffre() {
        if (offreTarifaireFuture != null) {
            this.offreTarifaire = offreTarifaireFuture;
            this.offreTarifaireFuture = null;
        }
    }

    public boolean changementModeFacturationPossible(LocalDate dateDemande) {
        LocalDate dateEffet = dateDemande.plusMonths(1);
        return dateEffet.getMonth() == dateSouscription.getMonth();
    }



    public void planifierChangementModeFacturation(ModeFacturation nouveauMode) {
        this.modeFacturationFutur = nouveauMode;
    }

    public String getReference() {
        return reference;
    }

    public void planifierChangementOffreTarifaire(OffreTarifaire nouvelleOffre) {
        this.offreTarifaireFuture = nouvelleOffre;
    }


    public void appliquerChangementModeFacturation() {
        if (modeFacturationFutur != null) {
            this.modeFacturation = modeFacturationFutur;
            this.modeFacturationFutur = null;
        }
    }


    // TODO vérifier que pas de facture impayées pour que ce soit effectif
    public void cloturer(LocalDate dateFin) {
        this.statut = StatutContrat.CLOTURE;
        this.dateFin = dateFin;
    }


    public Echeancier getEcheancier() {
        return echeancier;
    }

    public boolean aDesFacturesImpayees() {
        for (Facture facture : factures) {
            if (facture.getStatut() == StatutFacture.IMPAYEE){
                return true;
            }
        }
        return false;
    }

    public Releve getDernierReleve() {
        if (releves.isEmpty()) {
            return null;
        }
        return Collections.max(releves);
    }


    public Releve getAvantDernierReleve() {
        if (releves == null || releves.size() < 2) {
            return null;
        }

        List<Releve> copie = new ArrayList<>(releves);
        Collections.sort(copie);

        return copie.get(copie.size() - 2);
    }

    public double getSoldeCrediteur() {
        return soldeCrediteur;
    }

    public void ajouterSoldeCrediteur(double soldeCrediteur) {
        if(soldeCrediteur > 0){
            throw new IllegalArgumentException("Le solde créditeur doit être un chiffre négatif");
        }
        this.soldeCrediteur = soldeCrediteur;
    }

    public void reinitialiserSoldeCrediteur(){
        this.soldeCrediteur = 0;
    }

    public boolean estFacturationTerminee() {
        return facturationTerminee;
    }

    public void marquerFacturationTerminee() {
        this.facturationTerminee = true;
    }

    public OffreTarifaire getOffreTarifaireFuture() {
        return offreTarifaireFuture;
    }

    public ModeFacturation getModeFacturationFutur() {
        return modeFacturationFutur;
    }



}
