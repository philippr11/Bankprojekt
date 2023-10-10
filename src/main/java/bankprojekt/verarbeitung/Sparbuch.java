package bankprojekt.verarbeitung;

import java.time.LocalDate;

/**
 * ein Sparbuch, d.h. ein Konto, das nur recht eingeschränkt genutzt
 * werden kann. Insbesondere darf man monatlich nur höchstens 2000€
 * abheben, wobei der Kontostand nie unter 0,50€ fallen darf. 
 * @author Doro
 *
 */
public class Sparbuch extends Konto {
	/**
	 * Zinssatz, mit dem das Sparbuch verzinst wird. 0,03 entspricht 3%
	 */
	private double zinssatz;
	
	/**
	 * Monatlich erlaubter Gesamtbetrag für Abhebungen
	 */
	public static final double ABHEBESUMME = 2000;
	
	/**
	 * Betrag, der im aktuellen Monat bereits abgehoben wurde
	 */
	private double bereitsAbgehoben = 0;

	/**
	 * Monat und Jahr der letzten Abhebung
	 */
	private LocalDate zeitpunkt = LocalDate.now();
	
	/**
	* ein Standard-Sparbuch
	*/
	public Sparbuch() {
		zinssatz = 0.03;
	}

	/**
	* ein Standard-Sparbuch, das inhaber gehört und die angegebene Kontonummer hat
	* @param inhaber der Kontoinhaber
	* @param kontonummer die Wunsch-Kontonummer
	* @throws IllegalArgumentException wenn inhaber null ist
	*/
	public Sparbuch(Kunde inhaber, long kontonummer) {
		super(inhaber, kontonummer);
		zinssatz = 0.03;
	}
	
	@Override
	public String toString()
	{
    	String ausgabe = "-- SPARBUCH --" + System.lineSeparator() +
    	super.toString()
    	+ "Zinssatz: " + this.zinssatz * 100 +"%" + System.lineSeparator();
    	return ausgabe;
	}

	/**
	 * prüft Sparbuch auf Bedingungen, die erfüllt sein müssen für Abhebevorgang
	 * @param betrag wird mitgegeben um höhenspezifische Bedingungen überprüfen zu können
	 * @return true falls alle Bedingungen erfüllt
	 */
	@Override
	protected boolean kontenspezifischeAbhebebedingungen(double betrag) {
		double waehrungsbereinigteAbhebesumme = getAktuelleWaehrung().euroInWaehrungUmrechnen(ABHEBESUMME);
		if (getKontostand() - betrag >= 0.50 &&
				bereitsAbgehoben + betrag <= waehrungsbereinigteAbhebesumme)
		{
			setKontostand(getKontostand() - betrag);
			return true;
		}
		else
			return false;
	}


	/**
	 * erledigt was Sparbuchspezifisch nach dem abheben noch zu erledigen ist
	 * @param betrag wird übergeben um damit weiterzuarbeiten
	 */
	@Override
	protected void nachAbhebenToDo(double betrag) {
		bereitsAbgehoben += betrag;
		this.zeitpunkt = LocalDate.now();
	}

	/**
	 * erledigt Vorbedinungen für Abhebevorgang
	 */
	@Override
	protected void vorAbhebenToDo() {
		LocalDate heute = LocalDate.now();
		if(heute.getMonth() != zeitpunkt.getMonth() || heute.getYear() != zeitpunkt.getYear())
		{
			this.bereitsAbgehoben = 0;
		}
	}




	/**
	 * Ergänzt Währungswechsel für bereitsAbgehoben in Sparbuch Klasse
	 * @param neu neue Währung
	 */
	@Override
	public void waehrungswechsel(Waehrung neu){

		this.bereitsAbgehoben = getAktuelleWaehrung().festgelegteWaehrungInAngegebeneWaehrung(this.bereitsAbgehoben, neu);
		super.waehrungswechsel(neu);
	}

}
