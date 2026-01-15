package fr.electronvert.facturation.model.contrat;

import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.tarif.Tarif;

import java.util.Map;

public class OffreClassique extends  OffreTarifaire {







    @Override
    public double calculerCoutElectricite(Map<TypeConso, Double> consommations, Tarif tarif) {
        if (consommations == null || tarif == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }
        if (!consommations.containsKey(TypeConso.TOTAL)) {
            throw new IllegalStateException(
                    "Consommation TOTAL requise pour une offre classique"
            );
        }
        return consommations.get(TypeConso.TOTAL)
                * tarif.getPrixKwhClassique();    }

    @Override
    public double calculerCoutAbonnementAnnuel(Tarif tarif) {
        if (tarif == null) {
            throw new IllegalArgumentException("Tarif invalide");
        }
        return tarif.getPrixAbonnementClassique()*12;
    }

    @Override
    public Map<TypeConso, Double> calculerConsommation(
            Releve precedent,
            Releve courant
    ) {
        Map<TypeConso, Double> brute =
                courant.calculerConsommation(precedent);

        return Map.of(
                TypeConso.TOTAL,
                brute.get(TypeConso.TOTAL)
        );
    }

    @Override
    public double calculerCoutAbonnementMensuel(Tarif tarif) {
        if (tarif == null) {
            throw new IllegalArgumentException("Tarif invalide");
        }
        return tarif.getPrixAbonnementClassique();
    }



}
