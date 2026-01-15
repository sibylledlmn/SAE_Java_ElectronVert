package fr.electronvert.facturation.model.contrat;

import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.tarif.Tarif;

import java.util.Map;

public abstract class OffreTarifaire {

    protected String nom;
    private TypeConso typeConso;
    protected static final double FRAIS_CHANGEMENT_OFFRE_HT = 75.0;




    public abstract double calculerCoutElectricite(
            Map<TypeConso, Double> consommations,
            Tarif tarif
    );

    public abstract double calculerCoutAbonnementAnnuel(Tarif tarif);

    public abstract Map<TypeConso, Double> calculerConsommation(
            Releve precedent,
            Releve courant
    );



    public TypeConso getTypeConso(){
        return typeConso;
    };

    public String getNom() {
        return nom;
    }


    protected void verifierReleves(Releve precedent, Releve actuel) {

        if (precedent == null || actuel == null) {
            throw new IllegalArgumentException("Relevés manquants");
        }

        if (actuel.getDateDeReleve().isBefore(precedent.getDateDeReleve())) {
            throw new IllegalArgumentException("Relevés non chronologiques");
        }
    }

    public abstract double calculerCoutAbonnementMensuel(Tarif tarif);

    public static double getFraisChangementOffreHT() {
        return FRAIS_CHANGEMENT_OFFRE_HT;
    }

}
