package fr.electronvert.facturation.model.tarif;


import java.time.LocalDate;



public class Tarif implements Comparable<Tarif>{

    /**
     * Date d'entrée en vigueur du tarif.
     */
    private LocalDate dateDebut;

    /**
     * Prix du kilowattheure en euros selon l'offre tarifaire.
     */

    private double prixKwhClassique;

    private double prixKwhHP;

    private double prixKwhHC;

    /**
     * Prix mensuel de l'abonnement en euros selon l'offre tarifaire.
     */

    private double prixAbonnementClassique;
    private double prixAbonnementHPHC;



    public Tarif(LocalDate dateDebut,
                 double prixKwhClassique,
                 double prixKwhHeuresPleines,
                 double prixKwhHeuresCreuses,
                 double prixAbonnementClassique,
                 double prixAbonnementHPHC) {

        //TODO : créer une exception perso qui prend quel prix est inccorect en argument, je sais pas si ça marcherait
        // sinon juste tout mettre ensemble et dire que tous doivent être supérieurs à 0

        if (prixKwhClassique <= 0) {
            throw new IllegalArgumentException("Le prix du kWh classique doit être supérieur à 0");
        }
        if (prixKwhHeuresPleines <= 0) {
            throw new IllegalArgumentException("Le prix du kWh heures pleines doit être supérieur à 0");
        }
        if (prixKwhHeuresCreuses <= 0) {
            throw new IllegalArgumentException("Le prix du kWh heures creuses doit être supérieur à 0");
        }
        if (prixAbonnementClassique <= 0) {
            throw new IllegalArgumentException("Le prix de l'abonnement classique doit être supérieur à 0");
        }
        if (prixAbonnementHPHC <= 0) {
            throw new IllegalArgumentException("Le prix de l'abonnement HP/HC doit être supérieur à 0");
        }

        this.dateDebut = dateDebut;
        this.prixKwhClassique = prixKwhClassique;
        this.prixKwhHP = prixKwhHeuresPleines;
        this.prixKwhHC = prixKwhHeuresCreuses;
        this.prixAbonnementClassique = prixAbonnementClassique;
        this.prixAbonnementHPHC = prixAbonnementHPHC;
    }










    public double getPrixKwhClassique() {
        return prixKwhClassique;
    }

    public double getPrixKwhHP() {
        return prixKwhHP;
    }

    public double getPrixKwhHC() {
        return prixKwhHC;
    }

    public double getPrixAbonnementClassique() {
        return prixAbonnementClassique;
    }

    public double getPrixAbonnementHPHC() {
        return prixAbonnementHPHC;
    }



    /**
     * Retourne la date d'entrée en vigueur du tarif.
     *
     * @return la date de début du tarif
     */
    public LocalDate getDateDebut() {
        return dateDebut;
    }


    @Override
    public int compareTo(Tarif autre) {
        return this.dateDebut.compareTo(autre.dateDebut);

    }
}
