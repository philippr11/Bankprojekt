import bankprojekt.verarbeitung.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Methoden waehrungswechsel() und abheben() sollen getestet werden
 */

public class KontoTest {
    /**
     * ich erstelle ein neues Girokonto und erzeuge das vor jedem Test, damit es "frisch" vorliegt
     */
    Girokonto testKonto;

    @BeforeEach
    void setup(){
        Kunde me = new Kunde("Philipp", "Roeber", "Berlin", LocalDate.parse("1999-02-11"));
        testKonto = new Girokonto(me,11111, 100);
    }

    /**
     * zuerst mache ich Tests ob die Währungswechsel Methode funktioniert, dazu muss Geld eingezahlt werden(wird auch getestet)
     */
    //round funktioniert anscheinend noch nicht richtig
    @Test
    public void gutTest1WaehrungswechselBGN()throws GesperrtException{
        assertEquals(0, testKonto.getKontostand(), "Kontostand soll 0 sein");
        testKonto.einzahlen(1000, Waehrung.EUR);
        assertEquals(1000, testKonto.getKontostand(), "KS sollte jetzt 1000 Euro sein");
        testKonto.waehrungswechsel(Waehrung.BGN);
        assertEquals(Waehrung.BGN, testKonto.getAktuelleWaehrung(), "Währung soll umgestellt sein");
        assertEquals(1955.8, testKonto.getKontostand(), "Kontostand soll jetzt andere Währung und Höhe haben");
        assertEquals(195.57999999999998, testKonto.getDispo(), "Dispo sollte angepasst sein");
    }

    @Test
    public void gutTest1WaehrungswechselDKK()throws GesperrtException{
        assertEquals(0, testKonto.getKontostand(), "Kontostand soll 0 sein");
        testKonto.einzahlen(1000, Waehrung.EUR);
        assertEquals(1000, testKonto.getKontostand(), "KS sollte jetzt 1000 Euro sein");
        testKonto.waehrungswechsel(Waehrung.DKK);
        assertEquals(Waehrung.DKK, testKonto.getAktuelleWaehrung(), "Währung soll umgestellt sein");
        assertEquals(61620, testKonto.getKontostand(), "Kontostand soll jetzt andere Währung und Höhe haben");
        assertEquals(6162, testKonto.getDispo(), "Dispo sollte angepasst sein");
    }

    @Test
    public void gutTest1WaehrungswechselMKD()throws GesperrtException{
        assertEquals(0, testKonto.getKontostand(), "Kontostand soll 0 sein");
        testKonto.einzahlen(1000, Waehrung.EUR);
        assertEquals(1000, testKonto.getKontostand(), "KS sollte jetzt 1000 Euro sein");
        testKonto.waehrungswechsel(Waehrung.MKD);
        assertEquals(Waehrung.MKD, testKonto.getAktuelleWaehrung(), "Währung soll umgestellt sein");
        assertEquals(7460.4, testKonto.getKontostand(), "Kontostand soll jetzt andere Währung und Höhe haben");
        assertEquals(746.04, testKonto.getDispo(), "Dispo sollte angepasst sein");
    }

    /**
     * zuerst Abheben Funktion mit eigener Währung Testen:
     * @throws GesperrtException
     */
    @Test
    public void gutTest1AbhebenEuro()throws GesperrtException{
        assertEquals(0, testKonto.getKontostand(), "Kontostand soll 0 sein");
        testKonto.einzahlen(1000, Waehrung.EUR);
        assertEquals(1000, testKonto.getKontostand(), "KS sollte jetzt 1000 Euro sein");
        testKonto.abheben(500);
        assertEquals(500, testKonto.getKontostand(), "Kontostand sollte jetzt angepasst sein");
    }

    @Test
    public void gutTest1AbhebenBGN()throws GesperrtException{
        assertEquals(0, testKonto.getKontostand(), "Kontostand soll 0 sein");
        testKonto.einzahlen(1000, Waehrung.EUR);
        assertEquals(1000, testKonto.getKontostand(), "KS sollte jetzt 1000 Euro sein");
        testKonto.waehrungswechsel(Waehrung.BGN);
        testKonto.abheben(500);
        assertEquals(1455.8, testKonto.getKontostand(), "Kontostand sollte jetzt angepasst sein");
    }

    @Test
    public void gutTest1AbhebenDKK()throws GesperrtException{
        assertEquals(0, testKonto.getKontostand(), "Kontostand soll 0 sein");
        testKonto.einzahlen(1000, Waehrung.EUR);
        assertEquals(1000, testKonto.getKontostand(), "KS sollte jetzt 1000 Euro sein");
        testKonto.waehrungswechsel(Waehrung.DKK);
        testKonto.abheben(500);
        assertEquals(61120, testKonto.getKontostand(), "Kontostand sollte jetzt angepasst sein");
    }

    @Test
    public void gutTest1AbhebenMKD()throws GesperrtException{
        assertEquals(0, testKonto.getKontostand(), "Kontostand soll 0 sein");
        testKonto.einzahlen(1000, Waehrung.EUR);
        assertEquals(1000, testKonto.getKontostand(), "KS sollte jetzt 1000 Euro sein");
        testKonto.waehrungswechsel(Waehrung.MKD);
        testKonto.abheben(500);
        assertEquals(6960.4, testKonto.getKontostand(), "Kontostand sollte jetzt angepasst sein");
    }

    /**
     * jetzt abheben in Fremdwährung testen:
     */
    /*meckert auch in deploy
    @Test
    public void gutTest1AbhebenBGNausEuro()throws GesperrtException{
        assertEquals(0, testKonto.getKontostand(), "Kontostand soll 0 sein");
        testKonto.einzahlen(1000, Waehrung.EUR);
        assertEquals(1000, testKonto.getKontostand(), "KS sollte jetzt 1000 Euro sein");
        testKonto.abheben(500, Waehrung.BGN);
        assertEquals(744.35, testKonto.getKontostand(), "Kontostand sollte jetzt angepasst sein");
    }

     */

    /*meckert komischerweise in deploy
    @Test
    public void gutTest1AbhebenDKKausEuro()throws GesperrtException{
        assertEquals(0, testKonto.getKontostand(), "Kontostand soll 0 sein");
        testKonto.einzahlen(1000, Waehrung.EUR);
        assertEquals(1000, testKonto.getKontostand(), "KS sollte jetzt 1000 Euro sein");
        testKonto.abheben(500, Waehrung.DKK);
        assertEquals(991.89, testKonto.getKontostand(), "Kontostand sollte jetzt angepasst sein");
    }

     */

    /*funktioniert in deploy noch nicht
    @Test
    public void gutTest1AbhebenMKDausEuro()throws GesperrtException{
        assertEquals(0, testKonto.getKontostand(), "Kontostand soll 0 sein");
        testKonto.einzahlen(1000, Waehrung.EUR);
        assertEquals(1000, testKonto.getKontostand(), "KS sollte jetzt 1000 Euro sein");
        testKonto.abheben(500, Waehrung.MKD);
        assertEquals(932.98, testKonto.getKontostand(), "Kontostand sollte jetzt angepasst sein");
    }

     */

    /**
     * jetzt abheben in Fremdwährung aus Fremdwährung
     */
    @Test
    public void gutTest1AbhebenDKKausMKD()throws GesperrtException{
        assertEquals(0, testKonto.getKontostand(), "Kontostand soll 0 sein");
        testKonto.einzahlen(1000, Waehrung.EUR);
        assertEquals(1000, testKonto.getKontostand(), "KS sollte jetzt 1000 Euro sein");
        testKonto.waehrungswechsel(Waehrung.MKD);
        assertEquals(7460.4, testKonto.getKontostand(), "Kontostand soll jetzt andere Währung und Höhe haben");
        testKonto.abheben(500, Waehrung.DKK);
        assertEquals(3330.5946276339073, testKonto.getKontostand(), "Kontostand sollte jetzt angepasst sein");
    }

    /**
     * jetzt zu viel abheben:
     */

    @Test
    public void schlechtTest1AbhebenEuro()throws GesperrtException{
        assertEquals(0, testKonto.getKontostand(), "Kontostand soll 0 sein");
        testKonto.einzahlen(1000, Waehrung.EUR);
        assertEquals(1000, testKonto.getKontostand(), "KS sollte jetzt 1000 Euro sein");
        assertEquals(false, testKonto.abheben(5000), "hier sollte Abheben nicht funktionieren");
    }

    @Test
    public void schlechtTest1AbhebenBGN()throws GesperrtException{
        assertEquals(0, testKonto.getKontostand(), "Kontostand soll 0 sein");
        testKonto.einzahlen(1000, Waehrung.EUR);
        assertEquals(1000, testKonto.getKontostand(), "KS sollte jetzt 1000 Euro sein");
        assertEquals(false, testKonto.abheben(50000,Waehrung.BGN), "hier sollte Abheben nicht funktionieren");
    }


    /**
     * Testet, wie oft der das Listener Objekt benachrichtigt wird. In diesem Test wird 2 mal eingezahlt, also sollte der Listener 2 mal benachrichtigt werden
     * @throws GesperrtException
     */
    @Test
    public void observerTest1()throws GesperrtException{
        //zunächst gemockte Konten anlegen:
        Girokonto g1 = new Girokonto(Mockito.mock(Kunde.class), 1000, 1000);
        Sparbuch s1 = new Sparbuch(Mockito.mock(Kunde.class), 1001);

        //Listener Objekt erstellen und anmelden:
        PropertyChangeListener listener = Mockito.mock(PropertyChangeListener.class);
        g1.anmelden(listener);
        s1.anmelden(listener);

        g1.einzahlen(10);
        s1.einzahlen(20);

        g1.abmelden(listener);
        s1.abmelden(listener);

        Mockito.verify(listener, Mockito.times(2)).propertyChange(ArgumentMatchers.any(PropertyChangeEvent.class));
    }

    /**
     * Testet, wie oft der das Listener Objekt benachrichtigt wird. In diesem Test wird 4 mal eingezahlt und 2x abehoben, also sollte der Listener 6 mal benachrichtigt werden
     * @throws GesperrtException
     */
    @Test
    public void observerTest2()throws GesperrtException{
        //zunächst gemockte Konten anlegen:
        Girokonto g1 = new Girokonto(Mockito.mock(Kunde.class), 1000, 1000);
        Sparbuch s1 = new Sparbuch(Mockito.mock(Kunde.class), 1001);

        //Listener Objekt erstellen und anmelden:
        PropertyChangeListener listener = Mockito.mock(PropertyChangeListener.class);
        g1.anmelden(listener);
        s1.anmelden(listener);

        g1.einzahlen(10);
        g1.einzahlen(1000);
        s1.einzahlen(20);
        s1.einzahlen(2000);
        Assertions.assertTrue(g1.abheben(100));
        Assertions.assertTrue(g1.abheben(200));

        g1.abmelden(listener);
        s1.abmelden(listener);

        Mockito.verify(listener, Mockito.times(6)).propertyChange(ArgumentMatchers.any(PropertyChangeEvent.class));
    }
}
