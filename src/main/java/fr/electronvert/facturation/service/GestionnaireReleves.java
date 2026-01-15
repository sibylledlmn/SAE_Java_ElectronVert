package fr.electronvert.facturation.service;

import fr.electronvert.facturation.model.contrat.Contrat;
import fr.electronvert.facturation.model.releve.Releve;
import fr.electronvert.facturation.model.releve.TypeConso;
import fr.electronvert.facturation.model.releve.TypeReleve;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class GestionnaireReleves {


    public Releve genererReleveOuverture(
            Contrat contrat,
            SimulateurIndex simulateurIndex

    ) {
        Map<TypeConso, Double> indexInitiaux =
                simulateurIndex.genererIndexInitial(contrat);

        Releve releve = new Releve(
                contrat,
                TypeReleve.OUVERTURE,
                contrat.getDateSouscription(),
                indexInitiaux
        );

        contrat.ajouterReleve(releve);
        return releve;
    }


    ;

    public Releve genererReleveMensuel(Contrat contrat, LocalDate date, SimulateurIndex simulateurIndex
    ) {
        if (!contrat.estActif()) {
            throw new IllegalStateException(
                    "Impossible de générer un relevé mensuel: le contrat est clôturé"
            );
        }

        if(contrat.getDernierReleve() == null) {
            throw new IllegalStateException(
                    "Impossible de générer un relevé mensuel sans relevé d'ouverture"
            );
        }

        Map<TypeConso, Double> index =
                simulateurIndex.calculerIndex(contrat, date);
        Releve releve = new Releve(
                contrat,
                TypeReleve.MENSUEL,
                date,
                index
        );

        contrat.ajouterReleve(releve);
        return releve;
    }

    ;

    public Releve genererReleveCloture(
            Contrat contrat,
            LocalDate dateCloture,
            SimulateurIndex simulateurIndex
    ) {
        if (contrat.estActif()) {
            throw new IllegalStateException(
                    "Impossible de générer un relevé de clôture : le contrat n'est pas clôturé"
            );
        }


        Releve dernier = contrat.getDernierReleve();
        if (dernier != null &&
                dernier.getTypeReleve() == TypeReleve.CLOTURE) {

            throw new IllegalStateException(
                    "Un relevé de clôture existe déjà pour ce contrat"
            );
        }

        Map<TypeConso, Double> index =
                simulateurIndex.calculerIndex(contrat, dateCloture);

        Releve releve = new Releve(
                contrat,
                TypeReleve.CLOTURE,
                dateCloture,
                index
        );

        contrat.ajouterReleve(releve);
        return releve;
    }



    ;

    public void genererRelevesMensuelsContratsActifs(GestionnaireContrats gestionnaireContrats, LocalDate date, SimulateurIndex simulateurIndex) {
       List<Contrat> contratsActifs = gestionnaireContrats.getContratsActifs();
       for(Contrat c :  contratsActifs) {
           genererReleveMensuel(c, date, simulateurIndex);
       }
    }


    ;

}
