package fr.electronvert.facturation.model.utilisateur;

public class Client extends Utilisateur {

    private static int compteur = 1;

    public Client(String nom, String prenom, String email) {
        super(genererId(), nom, prenom, email);
    }

    private static String genererId() {
        return "CLI-" + compteur++;
    }

    @Override
    public RoleUtilisateur getRole() {
        return RoleUtilisateur.CLIENT;
    }
}

