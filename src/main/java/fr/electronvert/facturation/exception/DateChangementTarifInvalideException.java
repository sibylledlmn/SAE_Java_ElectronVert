package fr.electronvert.facturation.exception;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;





public class DateChangementTarifInvalideException extends RuntimeException {


    private static final String DATES_AUTORISEES = "1er février ou le 1er août";

    private final LocalDate dateTentee;

    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy");


    /**
     * Constructeur de l'exception.
     * <p>
     * Crée une exception avec un message descriptif incluant la date tentée
     * et les dates autorisées pour les changements de tarif.
     * </p>
     *
     * @param dateTentee la date à laquelle le changement de tarif a été tenté (non null)
     */
    public DateChangementTarifInvalideException(LocalDate dateTentee) {
        super("Un nouveau tarif ne peut pas entrer en vigueur le: " + dateTentee.format(FORMATTER) + ". " +
                "Un nouveau tarif peut entrer en vigueur le  " + DATES_AUTORISEES + ".");
        this.dateTentee = dateTentee;
    }


    // à supprimer si jamais utilisé, à voir
    /**
     * Retourne la date à laquelle le changement de tarif a été tenté.
     * <p>
     * Cette information peut être utilisée par l'interface utilisateur pour :
     * <ul>
     *   <li>Afficher un calendrier avec les dates autorisées</li>
     *   <li>Proposer automatiquement la prochaine date valide</li>
     *   <li>Logger l'erreur avec le contexte complet</li>
     * </ul>
     *
     *
     * @return la date tentée
     */
    public LocalDate getDateTentee() {
        return dateTentee;
    }


    // pareil je pense pas que ce soit utile
    /**
     * Calcule et retourne la prochaine date autorisée après la date tentée.
     * <p>
     * Cette méthode utilitaire facilite l'affichage d'un message constructif
     * à l'utilisateur en lui proposant directement la prochaine date valide.
     * </p>
     * <p>
     * <strong>Logique :</strong>
     * <ul>
     *   <li>Si date tentée avant le 1er février : retourne 1er février de la même année</li>
     *   <li>Si date tentée entre février et juillet : retourne 1er août de la même année</li>
     *   <li>Si date tentée après le 1er août : retourne 1er février de l'année suivante</li>
     * </ul>
     *
     *
     * @return la prochaine date autorisée pour un changement de tarif
     */
    public LocalDate getProchaineeDateAutorisee() {
        int annee = dateTentee.getYear();

        // Si avant le 1er février de cette année
        if (dateTentee.isBefore(LocalDate.of(annee, 2, 1))) {
            return LocalDate.of(annee, 2, 1);
        }
        // Si entre le 1er février et le 1er août
        else if (dateTentee.isBefore(LocalDate.of(annee, 8, 1))) {
            return LocalDate.of(annee, 8, 1);
        }
        // Si après le 1er août : prochaine date est février année suivante
        else {
            return LocalDate.of(annee + 1, 2, 1);
        }
    }
}

