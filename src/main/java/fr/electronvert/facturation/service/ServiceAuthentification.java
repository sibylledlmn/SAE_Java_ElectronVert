package fr.electronvert.facturation.service;

import fr.electronvert.facturation.model.utilisateur.Administrateur;
import fr.electronvert.facturation.model.utilisateur.Client;
import fr.electronvert.facturation.model.utilisateur.Utilisateur;

import java.util.List;

public class ServiceAuthentification {

    private final GestionnaireClients gestionnaireClients;
    private final List<Administrateur> administrateurs;

    public ServiceAuthentification(
            GestionnaireClients gestionnaireClients,
            List<Administrateur> administrateurs
    ) {
        this.gestionnaireClients = gestionnaireClients;
        this.administrateurs = administrateurs;
    }

    // TODO : exceptions? ou juste dans la vue mettre message echec connexion?

    public Utilisateur connecter(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        Client client = gestionnaireClients.rechercherParEmail(email);
        if (client != null) {
            return client;
        }

        for (Administrateur admin : administrateurs) {
            if (admin.getEmail().equalsIgnoreCase(email)) {
                return admin;
            }
        }

        return null;
    }
}
