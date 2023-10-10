package bankprojekt.verarbeitung;

/**
 * Überklasse von den anderen jeweiligen spezifischen Kontofabriken
 */
public abstract class Kontofabrik {

    /**
     * Erstellt ein Konto
     * @param kunde jeweiliger Inhaber des Kontos
     * @param kontonummer Kontonummer des Kontos
     * @return Konto Objekt des erstellten Kontos
     */
    public abstract Konto erzeugeKonto(Kunde kunde, long kontonummer);

}
