import java.time.LocalDate;
import bankprojekt.verarbeitung.GesperrtException;
import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Kontoart;
import bankprojekt.verarbeitung.Kunde;
import bankprojekt.verarbeitung.Sparbuch;

/**
 * Testprogramm für Konten
 * @author Doro
 *
 */
public class KontenSpielereien {

	/**
	 * Testprogramm für Konten
	 * @param args wird nicht benutzt
	 */
	public static void main(String[] args) {
		Kontoart art = Kontoart.SPARBUCH;
		System.out.println(art.name() + " " + art.ordinal());
		
		System.out.println("Unser Angebot:");
		Kontoart[] alle = Kontoart.values();
		for(Kontoart vorhanden: alle)
		{
			System.out.println(vorhanden+ ": " + vorhanden.getWerbebotschaft());
		}
		
		String eingabe = "GIROKONTO";
		art = Kontoart.valueOf(eingabe);
		System.out.println(art.toString()+": "+ art.getWerbebotschaft());
		System.out.println("--------------");
		
		
		Kunde ich = new Kunde("Dorothea", "Hubrich", "zuhause", LocalDate.parse("1976-07-13"));

		Girokonto meinGiro = new Girokonto(ich, 1234, 1000.0);
		meinGiro.einzahlen(50);
		System.out.println(meinGiro);
		
		Konto meinSpar = new Sparbuch(ich, 9876);
		meinSpar.einzahlen(50);
		System.out.println("Ausprobiert: -----------");
		meinSpar.ausgeben(); //toString von Sparbuch!
		System.out.println("<<<<<<<<<<<<<<<<<");
		try
		{
			boolean hatGeklappt = meinSpar.abheben(70);
			System.out.println("Abhebung hat geklappt: " + hatGeklappt);
			System.out.println(meinSpar);
		}
		catch (GesperrtException e)
		{
			System.out.println("Zugriff auf gesperrtes Konto - Polizei rufen!");
		}
	}

}
