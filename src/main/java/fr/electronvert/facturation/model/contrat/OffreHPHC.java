package fr.electronvert.facturation.model.contrat;

import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.tarif.Tarif;

import java.util.Map;

public class OffreHPHC extends OffreTarifaire {


    @Override
    public double calculerCoutElectricite(
            Map<TypeConso, Double> consommations,
            Tarif tarif
    ) {
        if (consommations == null || tarif == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        if (!consommations.containsKey(TypeConso.HP)
                || !consommations.containsKey(TypeConso.HC)) {
            throw new IllegalStateException(
                    "Consommations HP/HC manquantes pour une offre HP/HC"
            );
        }

        double hp = consommations.get(TypeConso.HP);
        double hc = consommations.get(TypeConso.HC);

        return hp * tarif.getPrixKwhHP()
                + hc * tarif.getPrixKwhHC();
    }

    @Override
    public double calculerCoutAbonnementAnnuel(Tarif tarif) {
        return tarif.getPrixAbonnementHPHC() * 12;
    }


    @Override
    public double calculerCoutAbonnementMensuel(Tarif tarif) {
        return tarif.getPrixAbonnementHPHC();
    }


    @Override
    public Map<TypeConso, Double> calculerConsommation(
            Releve precedent,
            Releve actuel
    ) {
        Map<TypeConso, Double> brute =
                actuel.calculerConsommation(precedent);

        return Map.of(
                TypeConso.HP, brute.get(TypeConso.HP),
                TypeConso.HC, brute.get(TypeConso.HC)
        );
    }
}



