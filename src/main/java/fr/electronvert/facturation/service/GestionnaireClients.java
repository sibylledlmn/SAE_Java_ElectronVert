package fr.electronvert.facturation.service;

import fr.electronvert.facturation.exception.ClientDejaExistantException;
import fr.electronvert.facturation.model.utilisateur.Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestionnaireClients {

    private final List<Client> clients = new ArrayList<>();


    public Client creerClient(String nom, String prenom, String email) {
        if (rechercherParEmail(email) != null) {
            throw new ClientDejaExistantException(
                    rechercherParEmail(email).getEmail(),
                    rechercherParEmail(email).getId()
            );
        }

        Client client = new Client(nom, prenom, email);
        clients.add(client);
        return client;
    }




    public Client rechercherParEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être null ou vide");
        }

        for (Client c : clients) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                return c;
            }
        }
        return null;
    }

    public List<Client> getTousLesClients() {
        return Collections.unmodifiableList(clients);
    }

    public int getNombreClients() {
        return clients.size();
    }
}
