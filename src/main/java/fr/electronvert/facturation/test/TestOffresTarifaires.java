package fr.electronvert.facturation.test;

import fr.electronvert.facturation.model.contrat.OffreClassique;
import fr.electronvert.facturation.model.contrat.OffreHPHC;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.tarif.Tarif;

import java.time.LocalDate;
import java.util.Map;

public class TestOffresTarifaires {
    public static void main(String[] args) {


        // GESTIONNAIRES

      // tarif de référence pour les tests :

        Tarif tarif = new Tarif(
                LocalDate.of(2024, 1, 1),
                0.18, // prix kWh classique
                0.20, // prix kWh HP
                0.14, // prix kWh HC
                9.50, // abonnement classique
                12.00 // abonnement HP/HC
        );


        // consommation connue et fixe pour les tests :

        Map<TypeConso, Double> consoClassique =
                Map.of(TypeConso.TOTAL, 120.0);

        Map<TypeConso, Double> consoHPHC =
                Map.of(
                        TypeConso.HP, 80.0,
                        TypeConso.HC, 40.0
                );

        System.out.println("=== Test Des Offres Tarifaires ===");
        System.out.println("");

        // OFFRE CLASSIQUE

        OffreClassique offreClassique = new OffreClassique();


        double coutElecClassique =
                offreClassique.calculerCoutElectricite(consoClassique, tarif);

        double abonnementClassique =
                offreClassique.calculerCoutAbonnementMensuel(tarif);

        System.out.println("--- Offre Classique ---");
        System.out.println("Consommation TOTAL : 120 kWh");
        System.out.println("Prix kWh : " + tarif.getPrixKwhClassique());
        System.out.println("Coût électricité HT : " + coutElecClassique);
        System.out.println("Abonnement mensuel HT : " + abonnementClassique);
        System.out.println("Total HT : " + (coutElecClassique + abonnementClassique));

        System.out.println("");

        // OFFRE HP/HC

        OffreHPHC offreHPHC = new OffreHPHC();

        double coutElecHPHC =
                offreHPHC.calculerCoutElectricite(consoHPHC, tarif);

        double abonnementHPHC =
                offreHPHC.calculerCoutAbonnementMensuel(tarif);

        System.out.println("--- Offre HP / HC ---");
        System.out.println("Consommation HP : 80 kWh");
        System.out.println("Consommation HC : 40 kWh");
        System.out.println("Prix HP : " + tarif.getPrixKwhHP());
        System.out.println("Prix HC : " + tarif.getPrixKwhHC());
        System.out.println("Coût électricité HT : " + coutElecHPHC);
        System.out.println("Abonnement mensuel HT : " + abonnementHPHC);
        System.out.println("Total HT : " + (coutElecHPHC + abonnementHPHC));

    }
}
