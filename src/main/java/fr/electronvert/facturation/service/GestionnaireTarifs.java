package fr.electronvert.facturation.service;

import fr.electronvert.facturation.exception.DateChangementTarifInvalideException;
import fr.electronvert.facturation.model.tarif.Tarif;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestionnaireTarifs {

    private final List<Tarif> historiqueDesTarifs = new ArrayList<>();

    // GESTION DES TARIFS

    public Tarif creerNouveauTarif(
            LocalDate dateDebut,
            double prixKwhClassique,
            double prixKwhHeuresPleines,
            double prixKwhHeuresCreuses,
            double prixAbonnementClassique,
            double prixAbonnementHPHC
    ) throws DateChangementTarifInvalideException {

        if (!estDateValideChangementTarif(dateDebut)) {
            throw new DateChangementTarifInvalideException(dateDebut);
        }

        if (!historiqueDesTarifs.isEmpty()) {
            Tarif dernierTarif = historiqueDesTarifs.get(historiqueDesTarifs.size() - 1);

            if (!dateDebut.isAfter(dernierTarif.getDateDebut())) {
                throw new IllegalArgumentException(
                        "La date de début du nouveau tarif doit être postérieure au tarif actuel"
                );
            }
        }

        Tarif nouveauTarif = new Tarif(
                dateDebut,
                prixKwhClassique,
                prixKwhHeuresPleines,
                prixKwhHeuresCreuses,
                prixAbonnementClassique,
                prixAbonnementHPHC
        );

        historiqueDesTarifs.add(nouveauTarif);
        Collections.sort(historiqueDesTarifs);

        return nouveauTarif;
    }

    public void ajouterTarifInitial(Tarif tarif) {
        if (tarif == null) {
            throw new IllegalArgumentException("Le tarif ne peut pas être nul");
        }

        if (!historiqueDesTarifs.isEmpty()) {
            throw new IllegalStateException(
                    "Le tarif initial ne peut être ajouté que si aucun tarif n'existe"
            );
        }

        historiqueDesTarifs.add(tarif);
    }

    /**
     * Retourne le tarif actif à une date donnée.
     */
    public Tarif getTarifActif(LocalDate date) {
        return historiqueDesTarifs.stream()
                .filter(t -> !t.getDateDebut().isAfter(date))
                .max(Tarif::compareTo)
                .orElseThrow(() ->
                        new IllegalStateException("Aucun tarif actif pour la date : " + date)
                );
    }

    /**
     * Vérifie si une date est valide pour un changement de tarif.
     */
    private boolean estDateValideChangementTarif(LocalDate date) {
        return (date.getMonthValue() == 2 && date.getDayOfMonth() == 1)
                || (date.getMonthValue() == 8 && date.getDayOfMonth() == 1);
    }

    /**
     * Historique non modifiable.
     */
    public List<Tarif> getHistoriqueDesTarifs() {
        return Collections.unmodifiableList(historiqueDesTarifs);
    }
}
