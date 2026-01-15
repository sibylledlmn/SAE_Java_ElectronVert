package fr.electronvert.facturation.model.contrat;

import java.time.LocalDate;

public class DemandeModificationContrat {

    private final Contrat contrat;
    private final TypeDemandeModification type;
    private final LocalDate dateDemande;

    private StatutDemande statut;

    private ModeFacturation nouveauModeFacturation;
    private OffreTarifaire nouvelleOffreTarifaire;

    public DemandeModificationContrat( Contrat contrat,
                                       LocalDate dateDemande,
                                       TypeDemandeModification type,
                                       OffreTarifaire nouvelleOffre,
                                       ModeFacturation nouveauMode) {
        if (contrat == null || dateDemande == null || type == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        this.contrat = contrat;
        this.dateDemande = dateDemande;
        this.type = type;
        this.nouvelleOffreTarifaire = nouvelleOffre;
        this.nouveauModeFacturation = nouveauMode;
        this.statut = StatutDemande.EN_ATTENTE;
    }
    public Contrat getContrat() { return contrat; }
    public LocalDate getDateDemande() { return dateDemande; }
    public TypeDemandeModification getType() { return type; }
    public OffreTarifaire getNouvelleOffre() { return nouvelleOffreTarifaire; }
    public ModeFacturation getNouveauMode() { return nouveauModeFacturation; }

    public StatutDemande getStatut() {
        return statut;
    }

    public void marquerAppliquee() {
        this.statut = StatutDemande.APPLIQUEE;
    }



}
