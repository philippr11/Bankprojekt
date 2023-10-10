import bankprojekt.verarbeitung.*;
import bankprojekt.verwaltung.Bank;
import bankprojekt.verwaltung.KontoNummerNichtVorhandenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;


public class BankTest {

    Bank bank1;
    Bank leer;
    Konto mock1;
    Konto mock2;

    Konto mock3;

    Konto mock4;

    Kontofabrik mock;

    long mock2kontonummer;
    long mock4nummer;
    long mock3nummer;



    /**
     * Hier wird vor jedem Mal eine Test Bank erstellt, eine leere Bank, mock1 als Funktionales Konto und mock2 als GesperrtesKonto im Minus
     * mock 3 und 4 sind ueberweisungsfähige Konten mit Integriertem Interface Ueberweisungsfähig
     * @throws GesperrtException falls Testkonten gesperrt sind
     */
    @BeforeEach
    public void mockObjekteErstellen() throws GesperrtException {
        bank1 = new Bank(1234);
        leer = new Bank(2345);
        Kontofabrik mock = Mockito.mock(Kontofabrik.class);
        Kunde kundemock = Mockito.mock(Kunde.class);

        mock1 = Mockito.mock(Konto.class);
        Mockito.when(mock1.getKontostand()).thenReturn(200.0);
        Mockito.when(mock1.abheben(ArgumentMatchers.anyDouble())).thenReturn(true);
        Mockito.when(mock1.getKontostandFormatiert()).thenReturn("200.0");
        Mockito.when(mock1.isGesperrt()).thenReturn(false);

        mock2 = Mockito.mock(Girokonto.class);
        Mockito.when(mock2.getKontostand()).thenReturn(-150.0);
        Mockito.when(mock2.abheben(ArgumentMatchers.anyDouble())).thenReturn(false);
        Mockito.when(mock2.getKontostandFormatiert()).thenReturn("-150");
        Mockito.when(mock2.isGesperrt()).thenReturn(true);

        mock3 =  Mockito.mock(Girokonto.class, Mockito.withSettings().extraInterfaces(Ueberweisungsfaehig.class));
        Kunde k3 = Mockito.mock(Kunde.class);
        Mockito.when(mock3.getInhaber()).thenReturn(k3);
        Mockito.when(k3.getName()).thenReturn("Tina Turner");


        mock4 = Mockito.mock(Girokonto.class, Mockito.withSettings().extraInterfaces(Ueberweisungsfaehig.class));
        Ueberweisungsfaehig mVierUeberweisungsfaehig = (Ueberweisungsfaehig) mock3;
        Kunde k4 = Mockito.mock(Kunde.class);
        Mockito.when(mock4.getInhaber()).thenReturn(k4);
        Mockito.when(k4.getName()).thenReturn("Peter Maffey");

        Mockito.when(mock.erzeugeKonto(ArgumentMatchers.any(Kunde.class), ArgumentMatchers.anyLong())).thenReturn(mock1);
        bank1.kontoErstellen(mock, kundemock);

        Mockito.when(mock.erzeugeKonto(ArgumentMatchers.any(Kunde.class), ArgumentMatchers.anyLong())).thenReturn(mock2);
        bank1.kontoErstellen(mock, kundemock);

        Mockito.when(mock.erzeugeKonto(ArgumentMatchers.any(Kunde.class), ArgumentMatchers.anyLong())).thenReturn(mock3);
        mock3nummer = bank1.kontoErstellen(mock, kundemock);

        Mockito.when(mock.erzeugeKonto(ArgumentMatchers.any(Kunde.class), ArgumentMatchers.anyLong())).thenReturn(mock4);
        mock4nummer = bank1.kontoErstellen(mock, kundemock);

    }

    /**
     * wir testen ob alleKontoen Funktion wie gewollt funktioniert
     */
    @Test
    public void gutTestGetAlleKonten(){
        StringBuilder getKonten = new StringBuilder();

        assertEquals((getKonten.append("1: 200.0").append(System.lineSeparator()).append("2: -150").append(System.lineSeparator())).toString(), bank1.getAlleKonten());
    }

    /**
     * es wird getestet ob in der leeren Bank Konten ausgegeben werden können
     */
    @Test
    public void getAlleKontenLeereBank() {
        assertEquals("", leer.getAlleKonten());
    }

    /**
     * alle Kontonummern werden ausgegeben aus der funktionalen Bank
     */
    @Test
    public void gutTestGetAlleKontonummern() {
        ArrayList nummernliste = new ArrayList();
        nummernliste.add(1L);
        nummernliste.add(2L);

        assertEquals(nummernliste, bank1.getAlleKontonummern());
    }

    /**
     * es wird versucht aus der leeren Bank alle Kontonummern auszugeben
     */
    @Test
    public void getAlleKontonummernLeereBank() {
        ArrayList nummernliste = new ArrayList();
        assertEquals(nummernliste, leer.getAlleKontonummern());
    }

    /**
     * dieser Test schaut ob die abheben Funktion mit normalen Parametern das gewünschte Outcome hat
     * @throws GesperrtException falls Konto gesperrt
     * @throws KontoNummerNichtVorhandenException falls Konto nicht vorhanden
     */
    @Test
    public void gutTestAbheben() throws GesperrtException, KontoNummerNichtVorhandenException {
        assertTrue(bank1.geldAbheben(1, 100));
        Mockito.verify(mock1).abheben(ArgumentMatchers.anyDouble());
    }

    /**
     * testet ob gesperrt Exception wirklich bei gesperrtem Konto geworfen wird und abheben dann 0 mal ausgeführt wird
     * @throws GesperrtException falls Konto gesperrt
     */
    @Test
    public void abhebenKontoGesperrt() throws GesperrtException {
        assertThrows(GesperrtException.class,
                () -> {bank1.geldAbheben(2, 100);});
        Mockito.verify(mock1, Mockito.times(0)).abheben(ArgumentMatchers.anyDouble());
    }

    /**
     * testet ob von null Konto abgehoben werden kann, sollte 0 mal ausgeführt werden
     * @throws GesperrtException falls angegebenes Konto gesperrt
     */
    @Test
    public void abhebenKontoGibtsNicht() throws GesperrtException {
        assertThrows(KontoNummerNichtVorhandenException.class,
                () -> {bank1.geldAbheben(3, 200);});
        Mockito.verify(mock1, Mockito.times(0)).abheben(ArgumentMatchers.anyDouble());
    }

    /**
     * testet Normalparameter von einzahlen() und prüft ob erfolgreich ausgeführt wird
     * @throws KontoNummerNichtVorhandenException
     */
    @Test
    public void gutTestEinzahlen() throws KontoNummerNichtVorhandenException {
        bank1.geldEinzahlen(1, 100);
        Mockito.verify(mock1, Mockito.times(1)).einzahlen(ArgumentMatchers.anyDouble());
    }

    /**
     * test, ob Exception verlässlich geworfen wird wenn das Konto nicht vorhanden ist auf das man einzahlen will
     * @throws KontoNummerNichtVorhandenException wenn Konto nicht existent
     */
    @Test
    public void kontogibtsnichtEinzahlen() throws KontoNummerNichtVorhandenException{
        assertThrows(KontoNummerNichtVorhandenException.class,
                () -> {bank1.geldEinzahlen(3, 200);});
        Mockito.verify(mock1, Mockito.times(0)).einzahlen(ArgumentMatchers.anyDouble());
    }

    /**
     * Testen ob wirklich Exception geworfen wird wenn ein nicht vorhandenes Konto gelöscht wird
     * @throws KontoNummerNichtVorhandenException falls Konto nicht vorhanden
     */
    @Test
    public void kontoNichtVorhandenLoeschen() throws KontoNummerNichtVorhandenException{
        assertThrows(KontoNummerNichtVorhandenException.class,
                () -> {bank1.kontoLoeschen(3);});


    }

    /**
     * testet ob Kontostand wie erwartet ausgegeben wird
     * @throws KontoNummerNichtVorhandenException
     */
    @Test
    public void kontoStandAusgebenGutTest() throws KontoNummerNichtVorhandenException{
        assertEquals(bank1.getKontostand(1), 200);

    }

    /**
     * testet ob wirklich Exception von getKontostand geworfen wird wenn Kontonummer nicht vorhanden ist
     * @throws KontoNummerNichtVorhandenException
     */
    @Test
    public void kontoStandAusgebenSchlechtTest() throws KontoNummerNichtVorhandenException{
        assertThrows(KontoNummerNichtVorhandenException.class,
                () -> {bank1.getKontostand(3);});
    }

    /**
     * Versuch ob ein gesperrtes Konto überweisen darf, sollte Exception werfen
     * @throws GesperrtException bei Versuch des Überweisens
     */
    @Test
    public void einKontonichtueberweisungsfaehig() throws GesperrtException{
        assertThrows(GesperrtException.class,
                () -> {bank1.geldUeberweisen(2, 1, 100, "bekannt");});

    }

    /**
     * Versuch auf ein nicht vorhandenes Konto zu überweisen, muss Exception werfen
     * @throws KontoNummerNichtVorhandenException
     */
    @Test
    public void einKontonichtvorhanden() throws KontoNummerNichtVorhandenException{
        assertThrows(KontoNummerNichtVorhandenException.class,
                () -> {bank1.geldUeberweisen(1, 3, 100, "bekannt");});

    }


    /**
     * testet ob Geldueberweisen wie geplant funktioniert
     * @throws GesperrtException falls ein Konto gesperrt ist
     * @throws KontoNummerNichtVorhandenException falls konto nicht vorhanden ist
     */
    @Test
    public void wurdegesendetundempfangenGutTest() throws GesperrtException, KontoNummerNichtVorhandenException {
        Ueberweisungsfaehig mDreiUeberweisungsfaehig = (Ueberweisungsfaehig) mock3;
        Ueberweisungsfaehig mVierUeberweisungsfaehig = (Ueberweisungsfaehig) mock4;

        Mockito.when(mDreiUeberweisungsfaehig.ueberweisungAbsenden(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(true);
        Assertions.assertTrue(bank1.geldUeberweisen(mock3nummer, mock4nummer, 100, "bekannt"));
        Mockito.verify(mDreiUeberweisungsfaehig, Mockito.times(1)).ueberweisungAbsenden(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
        Mockito.verify(mVierUeberweisungsfaehig, Mockito.times(1)).ueberweisungEmpfangen(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());

    }

    /**
     * es wird getestet ob von einem gesperrten Konto überwiesen werden darf. Exception sollte hier geworfen werden
     * @throws GesperrtException wenn Gesperrtes Konto überweisen möchte
     * @throws KontoNummerNichtVorhandenException wenn Kotonummer nicht vorhanden ist
     */
    @Test
    public void ueberweisenGesperrtSchlechttest1() throws GesperrtException, KontoNummerNichtVorhandenException {
        Ueberweisungsfaehig mDreiUeberweisungsfaehig = (Ueberweisungsfaehig) mock3;
        Ueberweisungsfaehig mZweiGesperrt = (Ueberweisungsfaehig) mock2;

        Mockito.when(mZweiGesperrt.ueberweisungAbsenden(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(true);
        Assertions.assertThrows(GesperrtException.class, () -> this.bank1.geldUeberweisen(mock2kontonummer, mock3nummer, 100.0, "bekannt"));
        Mockito.verify(mZweiGesperrt, Mockito.times(0)).ueberweisungAbsenden(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
        Mockito.verify(mDreiUeberweisungsfaehig, Mockito.times(0)).ueberweisungEmpfangen(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());

    }

    /**
     * überprüft ob an nicht vorhandene Kontonummer überwiesen werden kann bzw wurde, soll Exception werfen
     * @throws GesperrtException wenn Überweisungskonto gesperrt ist
     * @throws KontoNummerNichtVorhandenException wenn ein angegebenes Konto nicht vorhanden ist.
     */
    @Test
    public void ueberweisenNichtvorhandenSchlechttest1() throws GesperrtException, KontoNummerNichtVorhandenException {
        Ueberweisungsfaehig mDreiUeberweisungsfaehig = (Ueberweisungsfaehig) mock3;
        long fremdeKontonummer = 1234;

        Mockito.when(mDreiUeberweisungsfaehig.ueberweisungAbsenden(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(true);
        Assertions.assertThrows(KontoNummerNichtVorhandenException.class, () -> this.bank1.geldUeberweisen(mock3nummer, fremdeKontonummer, 100.0, "bekannt"));
        Mockito.verify(mDreiUeberweisungsfaehig, Mockito.times(0)).ueberweisungAbsenden(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
    }

    /**
     * Tests um die Serialisierung zu garantieren:
     */

    /**
     * Dieser Test stellt sicher, dass das geklonte Objekt dem Original entspricht, dazu wird zuerst verglichen ob Original und
     * Klon gleich sind, dann wird dem Original Geld überwiesen und wieder der Klon abgefragt
     */
    @Test
    public void serialiserierungsTestGut1() throws KontoNummerNichtVorhandenException {
        //Bank mit Kunden erstellen und Geld einzahlen:
        Bank b1test = new Bank(102030);
        Kunde testkunde = new Kunde();
        long girokontoTest = b1test.kontoErstellen(mock, testkunde);
        b1test.geldEinzahlen(girokontoTest, 1000);

        //Bank klonen:
        Bank bankKlon = b1test.clone();

        //überprüfen ob Klon mit Bank identisch:

        Assertions.assertTrue(b1test.getAlleKonten().equals(bankKlon.getAlleKonten()));
        Assertions.assertTrue(b1test.getBankleitzahl()==(bankKlon.getBankleitzahl()));
        Assertions.assertEquals(b1test.getKontostand(girokontoTest), bankKlon.getKontostand(girokontoTest));


        //UrsprungsBank verändern und schauen ob immernoch identisch:
        b1test.geldEinzahlen(girokontoTest, 999);
        Assertions.assertFalse(b1test.getKontostand(girokontoTest) == bankKlon.getKontostand(girokontoTest));
    }






}

/*
Notizen aus Übung:
Bank erstellt neue Kontonummer testen (mehr als 1 mal um zu schauen dass nicht doppelt, Konten löschen und neu erstellen)
Transaktionen:
    gibt rückgabewert -> sollte true sein (wenn überweisungsfähig beide)
                       -> false sein wenn 2 nicht überweisungsfähig
                      -> ueberweisungAbsenden auf Absender Konto mit richtigem Betrag aufgerufen (Konto ist für Kontostand zuständig, nicht Bank also nicht Kontostand prüfen)
                      prüfen ob Methode aufgerufen wurde: Mockito.verify(...)

                      überweisung empfangen auch testen, darf nichts empfangen wenn senden false war
                      Mockito.when(...)then return...


Verbesserungen aus Übung:
vorgehen richtig, 4 Verschiedene Mock Konten erstellen

 */