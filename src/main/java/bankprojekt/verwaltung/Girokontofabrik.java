package bankprojekt.verwaltung;

import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Kontofabrik;
import bankprojekt.verarbeitung.Kunde;

/**
 * Hilft Girokonten zu erstellen
 */
public class Girokontofabrik extends Kontofabrik {

    //Dispo ist hier in Girokonto ein essentielles Attribut
    private double dispo;

    /**
     * Konstruktor der Fabrik
     * @param dispo
     */
    public Girokontofabrik(double dispo){
        this.dispo = dispo;
    }

    /**
     * Methode zur Erzeugung eines neuen Girokontos
     * @param kunde beschreibt den Inhaber des Kontos
     * @param kontonummer ist die Kontonummer des Girokontos
     * @return neues Konto mit den vorherigen Eigenschaften
     */
    @Override
    public Konto erzeugeKonto(Kunde kunde, long kontonummer) {
        return new Girokonto(kunde, kontonummer, dispo);
    }

}
