package fr.electronvert.facturation.exception;

/**
 * Exception levée lors d'une tentative de création d'un client
 * avec un email déjà utilisé.
 * <p>
 * Cette exception permet d'éviter les doublons de clients
 * et d'informer clairement l'utilisateur que cet email est déjà enregistré.
 * </p>
 *
 * @author Sibylle Dillmann
 */
public class ClientDejaExistantException extends RuntimeException {

    private final String email;
    private final String clientExistantId;

    /**
     * Constructeur de l'exception.
     *
     * @param email l'email en conflit
     * @param clientExistantId l'identifiant du client existant avec cet email
     */
    public ClientDejaExistantException(String email, String clientExistantId) {
        super("Un client avec l'email " + email + " existe déjà (ID: " + clientExistantId + ")");
        this.email = email;
        this.clientExistantId = clientExistantId;
    }

    /**
     * Constructeur simplifié sans ID du client existant.
     *
     * @param email l'email en conflit
     */
    public ClientDejaExistantException(String email) {
        this(email, "inconnu");
    }

    /**
     * Retourne l'email en conflit.
     *
     * @return l'email déjà utilisé
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retourne l'identifiant du client existant.
     *
     * @return l'ID du client qui utilise déjà cet email
     */
    public String getClientExistantId() {
        return clientExistantId;
    }
}