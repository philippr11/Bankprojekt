package bankprojekt.verarbeitung;

import com.google.common.primitives.Doubles;
import javafx.beans.property.*;
import org.decimal4j.util.DoubleRounder;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * stellt ein allgemeines Bank-Konto dar
 */
public abstract class Konto implements Comparable<Konto>, Serializable {
	/**
	 * Methode sollte hier eigentlich nicht stehen, wegen
	 * keine Ausgaben in Verarbeitungsklassen!!!
	 */
	public void ausgeben() {
		System.out.println(this.toString());
	}

	/**
	 * der Kontoinhaber
	 */
	private Kunde inhaber;

	/**
	 * die Kontonummer
	 */
	private final long nummer;

	/**
	 * der aktuelle Kontostand
	 */
	private final ReadOnlyDoubleWrapper kontostandProperty;

	/**
	 * die aktuelle Währung, standartmäßig Euro
	 */
	private Waehrung waehrung;

	/**
	 * Hashmap zum Speichern von einem Aktiendepot, Speichert Aktie selbst und Anzahl derer
	 */
	private Map<Aktie, Integer> depot = new HashMap<>();

	/**
	 * Verwaltet die Threads und sorgt bei Käufen/Verkäufen für Synchronität
	 */
	private ExecutorService executorService = Executors.newCachedThreadPool();

	/**
	 * neuer service Parameter um Threads zu implementieren
	 */
	private ExecutorService service;

	/**
	 * Objekt welches übergeben werden kann um Änderungen zu
	 */
	private PropertyChangeSupport prop = new PropertyChangeSupport(this);

	/**
	 * setzt den aktuellen Kontostand
	 *
	 * @param kontostand neuer Kontostand
	 */
	protected void setKontostand(double kontostand) {
		double hilfskontostand = this.kontostandProperty.get();
		this.kontostandProperty.set(kontostand);
		this.setPositiv();
		this.firePropertyChange("kontostand", hilfskontostand, kontostand);
	}

	/**
	 * Wenn das Konto gesperrt ist (gesperrt = true), können keine Aktionen daran mehr vorgenommen werden,
	 * die zum Schaden des Kontoinhabers wären (abheben, Inhaberwechsel)
	 */
	private final BooleanProperty gesperrtProperty;

	/**
	 * Gibt an ob der Kontostand im Plus ist, true = positiv, false = negativ, 0 ist auch positiv
	 */
	private final ReadOnlyBooleanWrapper positivProperty;

	/**
	 * Setzt die beiden Eigenschaften kontoinhaber und kontonummer auf die angegebenen Werte,
	 * der anfängliche Kontostand wird auf 0 gesetzt. + Währung anfänglich Euro, kann per Methode
	 * verändert werden
	 *
	 * @param inhaber     der Inhaber
	 * @param kontonummer die gewünschte Kontonummer
	 * @throws IllegalArgumentException wenn der inhaber null ist
	 */
	public Konto(Kunde inhaber, long kontonummer) {
		if (inhaber == null)
			throw new IllegalArgumentException("Inhaber darf nicht null sein!");
		this.inhaber = inhaber;
		this.nummer = kontonummer;
		this.kontostandProperty = new ReadOnlyDoubleWrapper(0);
		this.gesperrtProperty = new SimpleBooleanProperty(false);
		this.waehrung = Waehrung.EUR;
		this.positivProperty = new ReadOnlyBooleanWrapper(true);
	}

	/**
	 * gibt zurück ob der Kontostand positiv ist, 0 ist auch noch positiv
	 * @return true falls 0 oder größer, false falls negativ
	 */
	public boolean isPositiv(){
		return positivProperty.get();
	}

	/**
	 * setzt, ob Kontostand derzeit positi oder negativ ist
	 */
	public void setPositiv(){
		if(this.kontostandProperty.get() >= 0){
			positivProperty.set(true);
		}
		else{
			positivProperty.set(false);
		}
	}

	/**
	 * setzt alle Eigenschaften des Kontos auf Standardwerte
	 */
	public Konto() {
		this(Kunde.MUSTERMANN, 1234567);
	}

	/**
	 * liefert den Kontoinhaber zurück
	 *
	 * @return der Inhaber
	 */
	public Kunde getInhaber() {
		return this.inhaber;
	}

	/**
	 * setzt den Kontoinhaber
	 *
	 * @param kinh neuer Kontoinhaber
	 * @throws GesperrtException        wenn das Konto gesperrt ist
	 * @throws IllegalArgumentException wenn kinh null ist
	 */
	public final void setInhaber(Kunde kinh) throws GesperrtException {
		if (kinh == null)
			throw new IllegalArgumentException("Der Inhaber darf nicht null sein!");
		if (this.gesperrtProperty.get())
			throw new GesperrtException(this.nummer);
		this.inhaber = kinh;

	}

	/**
	 * liefert den aktuellen Kontostand
	 *
	 * @return Kontostand
	 */
	public double getKontostand()
	{
		return kontostandProperty.get();
	}

	/**
	 * liefert die Kontonummer zurück
	 *
	 * @return Kontonummer
	 */
	public final long getKontonummer() {
		return nummer;
	}

	/**
	 * liefert zurück, ob das Konto gesperrt ist oder nicht
	 *
	 * @return true, wenn das Konto gesperrt ist
	 */
	public boolean isGesperrt() {
		return gesperrtProperty.get();
	}

	/**
	 * Erhöht den Kontostand um den eingezahlten Betrag.
	 *
	 * @param betrag double
	 * @throws IllegalArgumentException wenn der betrag negativ ist
	 */

	public void einzahlen(double betrag) {
		if (betrag < 0 || !Doubles.isFinite(betrag)) {
			throw new IllegalArgumentException("Falscher Betrag");
		}
		setKontostand(getKontostand() + betrag);
	}

	@Override
	public String toString() {
		String ausgabe;
		ausgabe = "Kontonummer: " + this.getKontonummerFormatiert()
				+ System.getProperty("line.separator");
		ausgabe += "Inhaber: " + this.inhaber;
		ausgabe += "Aktueller Kontostand: " + getKontostandFormatiert() + " ";
		ausgabe += this.getGesperrtText() + System.getProperty("line.separator");
		return ausgabe;
	}


	/**
	 * überprüft Klassenspezifisch was vor dem Abheben geprüft werden muss
	 */
	protected void vorAbhebenToDo(){};

	/**
	 * überprüft Klassenspezifisch ob nach dem Abheben noch etwas Kontointern zu erledigen ist
	 * @param betrag wird übergeben um damit weiterzuarbeiten
	 */
	protected void nachAbhebenToDo(double betrag){};

	/**
	 * prüft generelle Bedinungen von Unterkonten
	 * @param betrag wird mitgegeben um höhenspezifische Bedinungen überprüfen zu können
	 * @return true falls alle Bedingungen erfüllt  wurden
	 */
	protected abstract boolean kontenspezifischeAbhebebedingungen(double betrag);


	/**
	 * hebt Betrag von Konto ab, ist Struktur für spezifischer Unterklassen die diese Methode auch verwenden
	 * @param betrag ist die höhe der Abhebung
	 * @return true falls abheben erfolgreich war
	 * @throws GesperrtException falls Konto gesperrt ist
	 * @throws IllegalArgumentException falls Betrag unter 0 ist
	 */
	public boolean abheben(double betrag) throws GesperrtException {
		if(betrag < 0|| Double.isNaN(betrag) || Double.isInfinite(betrag)){
			throw new IllegalArgumentException("Betrag muss größer 0 sein");
		}
		if(this.isGesperrt()){
			throw new GesperrtException(this.getKontonummer());
		}
		vorAbhebenToDo();
		if(kontenspezifischeAbhebebedingungen(betrag)){
			this.setKontostand(this.getKontostand()-betrag);
			nachAbhebenToDo(betrag);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * sperrt das Konto, Aktionen zum Schaden des Benutzers sind nicht mehr möglich.
	 */
	public final void sperren() {
		this.gesperrtProperty.set(true);
	}

	/**
	 * entsperrt das Konto, alle Kontoaktionen sind wieder möglich.
	 */
	public final void entsperren() {
		this.gesperrtProperty.set(false);
	}


	/**
	 * liefert eine String-Ausgabe, wenn das Konto gesperrt ist
	 *
	 * @return "GESPERRT", wenn das Konto gesperrt ist, ansonsten ""
	 */
	public final String getGesperrtText() {
		if (this.gesperrtProperty.get()) {
			return "GESPERRT";
		} else {
			return "";
		}
	}

	/**
	 * liefert die ordentlich formatierte Kontonummer
	 *
	 * @return auf 10 Stellen formatierte Kontonummer
	 */
	public String getKontonummerFormatiert() {
		return String.format("%10d", this.nummer);
	}

	/**
	 * liefert den ordentlich formatierten Kontostand
	 *
	 * @return formatierter Kontostand mit 2 Nachkommastellen und Währungssymbol
	 */
	public String getKontostandFormatiert() {
		if (this.waehrung == Waehrung.BGN) {
			return String.format("%10.2f BGN", this.getKontostand());
		}
		if (this.waehrung == Waehrung.MKD) {
			return String.format("%10.2f Д", this.getKontostand());
		}
		if (this.waehrung == Waehrung.DKK) {
			return String.format("%10.2f kr", this.getKontostand());
		} else {
			return String.format("%10.2f €", this.getKontostand());
		}

		//return String.format("%10.2f %s",
		//this.getKontostand() Waehrung
		//schöner: in Enum als zweiten Parameter das jeweilige Symbol eintragen und dieses hier ohne Switch case abfragen

	}

	/**
	 * Vergleich von this mit other; Zwei Konten gelten als gleich,
	 * wen sie die gleiche Kontonummer haben
	 *
	 * @param other das Vergleichskonto
	 * @return true, wenn beide Konten die gleiche Nummer haben
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (this.getClass() != other.getClass())
			return false;
		if (this.nummer == ((Konto) other).nummer)
			return true;
		else
			return false;
	}

	@Override
	public int hashCode() {
		return 31 + (int) (this.nummer ^ (this.nummer >>> 32));
	}

	@Override
	public int compareTo(Konto other) {
		if (other.getKontonummer() > this.getKontonummer())
			return -1;
		if (other.getKontonummer() < this.getKontonummer())
			return 1;
		return 0;
	}



	/**
	 * @param betrag den man abheben möchte und spezielle Währung möchte
	 * @param w      währung, in der abgehoben werden soll. Hier wird automatisch über die Hilfsfunktion umgerechnet,
	 *               falls Währungen verschieden sind
	 * @return true falls geklappt, überschreibt bisherige abheben Methode
	 * @throws GesperrtException
	 */
	public boolean abheben(double betrag, Waehrung w) throws GesperrtException {
		return this.abheben(this.waehrung.festgelegteWaehrungInAngegebeneWaehrung(betrag, w));
	}

	/**
	 * Nutzt bisherige Einzahlfunktion und zahlt Betrag in angeforderter Währung ein
	 *
	 * @param betrag Höhe der Einzahlung
	 * @param w      Währung der angeforderten Abhebung
	 */
	public void einzahlen(double betrag, Waehrung w) {
		this.einzahlen(this.waehrung.festgelegteWaehrungInAngegebeneWaehrung(betrag, w));
	}


	/**
	 * gibt aktuelle Währung des Kontos aus
	 *
	 * @return aktuelle Währung
	 */
	public Waehrung getAktuelleWaehrung() {
		return this.waehrung;
	}

	/**
	 * wechselt Währung
	 *
	 * @param neu neue Währung
	 */
	public void waehrungswechsel(Waehrung neu) {

		this.kontostandProperty.set(DoubleRounder.round(this.waehrung.festgelegteWaehrungInAngegebeneWaehrung(this.kontostandProperty.get(), neu), 2));
		this.waehrung = neu;
	}


	/**
	 * Hier kann ein Kaufauftrag für eine Aktie erstellt wird, der so lange läuft bis er ausgeführt wurde der abbricht
	 * @param a übergebenes Aktienobjekt, für das ein Kaufauftrag erstellt werden soll
	 * @param anzahl die gewünschte Anzahl an Aktien
	 * @param hoechstpreis das Obere Limit der Aktienorder
	 * @return den Kaufpreis den man letztendlich gezahlt hat falls Auftrag ausgeführt wurde
	 */
	public Future<Double> kaufauftrag(Aktie a, int anzahl, double hoechstpreis) {
		if (a == null) {
			throw new NullPointerException("Übergebene Aktie muss existieren");
		}
		if (anzahl <= 0 || hoechstpreis <= 0) {
			throw new IllegalArgumentException("Anzahl und Hoechstpreis des Aktienkaufs muss positiv und größer 0 sein");
		}

		service = Executors.newCachedThreadPool();

		Future<Double> kaufpreis = service.submit(() -> {

			double preis = 0;

			a.getLock().lock();

			while (a.getKurs() > hoechstpreis) {
				a.getCondition().await();
			}

			if (this.getKontostand() >= a.getKurs() * anzahl) {
				preis = a.getKurs() * anzahl;

				setKontostand(getKontostand() - preis);

				if (depot.containsKey(a) == false) {
					depot.put(a, anzahl);
				} else {
					Integer gesamt = anzahl + depot.get(a);
					depot.put(a, gesamt);
				}
			}
			a.getLock().unlock();
			return preis;
		});
		return kaufpreis;
	}


	/**
	 * Verkaufsauftrag einer gehaltenen Aktie, der so lange läuft bis er abbricht oder ausgeführt wurde
	 * @param wkn die WKN der zu verkaufenden Aktie
	 * @param minimalpreis Minimalpreis für den man verkaufen möchte
	 * @return ob erfolgreich ausgeführt wurde
	 */
	public Future<Double> verkaufauftrag(String wkn, double minimalpreis) {
		if (wkn == null) {
			throw new NullPointerException("Angegebene WKN darf nicht leer sein!");
		}
		if (minimalpreis <= 0) {
			throw new IllegalArgumentException("Der Verkaufspreis muss positiv und größer 0 sein");
		}

		service = Executors.newCachedThreadPool();
		Callable<Double> verkaufsauftrag = () -> {

			double auszahlung = 0;

			Aktie a = this.depot.keySet().stream().filter(aktie -> aktie.getWertpapierKennnummer().equals(wkn)).findFirst().orElse(null);
			if (a == null)
				return 0.D;

			a.getLock().lock();
			while (a.getKurs() < minimalpreis) {
				try {
					a.getCondition().await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			auszahlung = depot.get(a) * a.getKurs();
			this.einzahlen(auszahlung);
			this.depot.remove(a);
			a.getLock().unlock();
			return auszahlung;


		};
		return executorService.submit(verkaufsauftrag);
	}

	/**
	 * meldet einen Listener an, der dann über Kontostand Änderungen benachrichtigt wird
	 * @param l zuhörer der Klasse PropertyChangeListener
	 */
	public void anmelden(PropertyChangeListener l)
	{

		prop.addPropertyChangeListener(l);
	}

	/**
	 * meldet einen Listener ab.
	 * @param l zuhörer der Klasse PropertyChangeListener
	 */
	public void abmelden(PropertyChangeListener l)
	{
		prop.removePropertyChangeListener(l);
	}

	/**
	 * gibt allen Listender bescheid, dass eine Änderung an dem zu Beobachtenden Objekt stattgefunden hat.
	 * @param name Bezeichnung des Beobachterattributs
	 * @param alt alter Wert des Attributs
	 * @param neu neuer Wert des Attributs
	 */
	protected void firePropertyChange(String name, Object alt, Object neu)
	{
		prop.firePropertyChange(name, alt, neu);
	}

	/**
	 * Kontostand wird vom Wrapper zu einem "normalen" Read Only Property umgewandelt
	 * @return Kontostand als Read only Objekt
	 */
	public ReadOnlyDoubleProperty kontostandProperty() {
		return this.kontostandProperty.getReadOnlyProperty();
	}

	/**
	 * Gibt zurück, ob das Konto derzeit gesperrt ist oder nicht
	 * @return true falls gesperrt
	 */
	public BooleanProperty gesperrtProperty(){
		return this.gesperrtProperty;
	}

	/**
	 * erlaubt Zugriff auf das Property, ob der Kontostand positiv (oder 0) ist
	 * @return boolean ob Kontostand positiv ist oder nicht
	 */
	public ReadOnlyBooleanProperty positivProperty(){
		return this.positivProperty.getReadOnlyProperty();
	}
}


/**
 * 3 Kennzeichen von Objekt im Observer
 * anmelden, abmelden, benachrichtigen hinzufügen
 * entweder Interface selbst oder über integration lösen
 * benachritigen aufrufen, nach setKontostand
 *
 * in neuer Klasse Ausgabe
 * muss sich subscriben und muss sich updaten
 * propertychangelistener oder interface implementieren
 *
 * Konto testen, alles andere mocken, ob benachrichtigen aufgerufen wurde
 */

/**
 * Maven Projekt:
 * mvn javafx:run
 * anmelden abmelden, benachrichtigen muss in Observer Pattern
 * views müssen
 * Controller: Logik des Systems, verändert Attribute, meldet Listener bei Model an, bzw Abmelden, erzeugt Objekt oder bekommt sie
 *
 */


