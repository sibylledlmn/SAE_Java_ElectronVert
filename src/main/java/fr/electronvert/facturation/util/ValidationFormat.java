package fr.electronvert.facturation.util;

public final class ValidationFormat {

    private ValidationFormat() {}

    public static void verifierNonVide(String valeur, String champ) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException(champ + " ne peut pas être vide");
        }
    }

    public static void verifierEmail(String email) {
        verifierNonVide(email, "Email");

        String regex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.(fr|com)$";
        if (!email.matches(regex)) {
            throw new IllegalArgumentException("Email invalide");
        }
    }
}

