package fr.electronvert.facturation.model.utilisateur;


public class Administrateur extends Utilisateur {

    private static int compteur = 1;
    private static final String DOMAINE_EMAIL = "@electronvert.fr";

    public Administrateur(String nom, String prenom, String email) {
        super(genererId(), nom, prenom, verifierEmailAdmin(email));
    }

    private static String genererId() {
        return "ADM-" + compteur++;
    }

    private static String verifierEmailAdmin(String email) {
        if (!email.toLowerCase().endsWith(DOMAINE_EMAIL)) {
            throw new IllegalArgumentException(
                    "L'adresse email d'un administrateur doit se terminer par "
                            + DOMAINE_EMAIL
            );
        }

        return email;
    }

    @Override
    public RoleUtilisateur getRole() {
        return RoleUtilisateur.ADMINISTRATEUR;
    }
}



