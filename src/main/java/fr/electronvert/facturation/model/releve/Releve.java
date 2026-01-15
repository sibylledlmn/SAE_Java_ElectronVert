package fr.electronvert.facturation.model.releve;

import fr.electronvert.facturation.model.contrat.Contrat;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class Releve implements Comparable<Releve>{

    private final UUID id;
    private TypeReleve typeReleve;
    private LocalDate dateDeReleve;
    private Contrat contrat;
    private final Map<TypeConso, Double> index;


    public Releve(Contrat contrat, TypeReleve typeReleve, LocalDate dateDeReleve,  Map<TypeConso, Double> index) {

        if (contrat == null) {
            throw new IllegalArgumentException("Un relevé doit être rattaché à un contrat");
        }
        if (dateDeReleve == null) {
            throw new IllegalArgumentException("La date du relevé est obligatoire");
        }
        if (index == null || index.isEmpty()) {
            throw new IllegalArgumentException("Les index ne peuvent pas être vides");
        }
        for (Double valeur : index.values()) {
            if (valeur < 0) {
                throw new IllegalArgumentException("Les index ne peuvent pas être négatifs");
            }
        }
        if (typeReleve == null) {
            throw new IllegalArgumentException("Le type de relevé est obligatoire");
        }

        this.contrat = contrat;
        this.id = UUID.randomUUID();
        this.typeReleve = typeReleve;
        this.dateDeReleve = dateDeReleve;
        this.index = Map.copyOf(index);


    }


    public Map<TypeConso, Double> calculerConsommation(Releve precedent) {

        if (precedent == null) {
            throw new IllegalArgumentException("Le relevé précédent est requis");
        }

        if (!this.contrat.equals(precedent.contrat)) {
            throw new IllegalArgumentException("Les relevés doivent appartenir au même contrat");
        }

        if (precedent.dateDeReleve.isAfter(this.dateDeReleve)) {
            throw new IllegalArgumentException("Ordre chronologique invalide entre relevés");
        }

        Map<TypeConso, Double> consommation = new EnumMap<>(TypeConso.class);

        for (TypeConso typeConso : index.keySet()) {
            double valeurActuelle = index.get(typeConso);
            double valeurPrecedente = precedent.index.getOrDefault(typeConso, 0.0);

            double delta = valeurActuelle - valeurPrecedente;

            if (delta < 0) {
                throw new IllegalStateException("Consommation négative détectée");
            }

            consommation.put(typeConso, delta);
        }

        return consommation;
    }


    @Override
    public int compareTo(Releve autre) {
        if (autre == null) {
            throw new NullPointerException("Le relevé à comparer ne peut pas être nul");
        }
        return this.dateDeReleve.compareTo(autre.dateDeReleve);

    }

    public UUID getId() {
        return id;
    }

    public TypeReleve getTypeReleve() {
        return typeReleve;
    }

    public LocalDate getDateDeReleve() {
        return dateDeReleve;
    }

    public Contrat getContrat() {
        return contrat;
    }

    public Map<TypeConso, Double> getIndex() {
        return index;
    }
}
