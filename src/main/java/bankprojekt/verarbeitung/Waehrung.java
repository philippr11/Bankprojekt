package bankprojekt.verarbeitung;

/**
 * Alle Währungen, die in unserem Bankprojekt zur Verfügung stehen
 * @author philipprober
 */
public enum Waehrung {
    EUR(1),
    BGN(1.9558),
    DKK(61.62),
    MKD(7.4604);

    private final double kurs;

    Waehrung(double umrechnungskurs){
        this.kurs = umrechnungskurs;
    }

    /**
     * Diese Methode erwartet einen Euro Betrag und wandelt ihn in den jeweiligen Währungskurs um
     * @param betrag ist die Höhe in Euro
     * @return den jeweiligen Währungsbetrag
     */
    public double euroInWaehrungUmrechnen(double betrag){
        double result = betrag * this.kurs;
        return result;
    }

    /**
     * Die Methode wandelt analog zur obigen andere Währungen in Euro um
     * @param betrag ist die Höhe der Fremdwärhung
     * @return ist die Eurosumme
     */
    public double waehrungInEuroUmrechnen(double betrag){
        double result = betrag / this.kurs;
        return result;
    }

    /**
     * Hilfsfunktion, falls meine Währung mal nicht von FW in Euro oder von Euro in FW ist. HilfsRechnung über Euro
     * @param betrag in fremdwährung
     * @param neu Zielwährung
     * @return Betrag in geforderter Zielwährung
     */
    public double festgelegteWaehrungInAngegebeneWaehrung(double betrag, Waehrung neu){
        return neu.euroInWaehrungUmrechnen(this.waehrungInEuroUmrechnen(betrag));
    }
}
