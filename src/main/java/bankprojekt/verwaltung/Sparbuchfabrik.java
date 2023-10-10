package bankprojekt.verwaltung;

import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Kontofabrik;
import bankprojekt.verarbeitung.Kunde;
import bankprojekt.verarbeitung.Sparbuch;

/**
 * Hilft Sparbücher zu erstellen
 */
public class Sparbuchfabrik extends Kontofabrik {


    /**
     * erstellt ein neues Sparbuch für den entsprechenden Kunden
     * @param kunde beschreibt Inhaber des Sparbuchs
     * @param kontonummer beschreibt Kontonummer des Sparbuchs
     * @return neues Sparbuch Objekt
     */
    @Override
    public Konto erzeugeKonto(Kunde kunde, long kontonummer) {
        return new Sparbuch(kunde, kontonummer);
    }

}
