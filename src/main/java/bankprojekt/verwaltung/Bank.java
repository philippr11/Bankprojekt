package bankprojekt.verwaltung;

import bankprojekt.verarbeitung.*;

import javax.print.DocFlavor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.io.*;

/**
 * wir erstellen eine Klasse Bank, in welcher die jeweiligen Konten der Bank gespeichert werden und sie wird durch BLZ identifiert
 * long myZaehlwert ist Zählvariable für Bank, wie viele Kunden sie hat, daraus wird Kontonummer abgeleitet
 * kontenHashMap ist der Speicherort für alle Konten, ich nehme eine Map und caste, anstatt in Überweisungsfähig und nicht zu unterteilen
 */
public class Bank implements Cloneable, Serializable{



    private HashMap<Long, Konto> kontenHashMap = new HashMap<>();
    private long myZaehlwert = 1;
    private final long bankleitzahl;

    public Bank(long bankleitzahl){
        this.bankleitzahl = bankleitzahl;
    }

    public long getBankleitzahl(){
        return this.bankleitzahl;
    }


    /**
     * erstellt Konto nach der Abstract Factory Methode
     * @param fabrik ist für klassifizierung des Kontos zuständig
     * @param inhaber Inhaber des jeweiligen Kontos
     * @return Kontonummer des Kontos
     */
    public long kontoErstellen(Kontofabrik fabrik, Kunde inhaber){
        long zaehler = this.myZaehlwert;
        Konto konto = fabrik.erzeugeKonto(inhaber, zaehler);
        kontenHashMap.put(myZaehlwert, konto);
        this.myZaehlwert ++;
        return zaehler;
    }

    /**
     * Iteration durch unsere Hashmap, ich versuche das hier mal mit einem leeren String builder.
     * @return wird ein String mit allen Kontonummern und den dazugehörigen Kontoständen
     */
    public String getAlleKonten(){
        StringBuilder alleKonten = new StringBuilder();
        for(Map.Entry<Long, Konto> entry : kontenHashMap.entrySet()){
            String kontonummer = String.valueOf(entry.getKey());
            String kontostand = entry.getValue().getKontostandFormatiert();
            alleKonten.append(kontonummer).append(": ").append(kontostand).append(System.lineSeparator());
        }
        return alleKonten.toString();

    }

    /**
     * Diese Funktion gibt eine Liste mit allen Kontonummern zurück, die in der Bank vorhanden sind
     * @return Liste mit gültigen Kontonummern
     * kürzeste Variante:
     * return new ArrayList<>(konten.keySet());
     */
    public List<Long> getAlleKontonummern(){
        ArrayList kontonummernListe = new ArrayList<>();
        for(Long myZaehlwert : kontenHashMap.keySet()){
            kontonummernListe.add(myZaehlwert);
        }
        return kontonummernListe;
    }

    /**
     * diese Funktion hebt Geld von der angegebenen Kontonummer in angegebener Höhe ab
     * @param von ist das Konto von dem abgehoben wird
     * @param betrag die Höhe des Auszahlungsbetrags
     * @return true wenn Abheben erfolgreich
     * @throws GesperrtException wenn Konto bereits gesperrt ist
     * @throws KontoNummerNichtVorhandenException wenn zu angegebenem Key kein Objekt vorhanden ist
     */
    public boolean geldAbheben(long von, double betrag)throws GesperrtException, KontoNummerNichtVorhandenException {

        if(kontenHashMap.get(von) == null){
            throw new KontoNummerNichtVorhandenException();
        }
        if((kontenHashMap.get(von)).isGesperrt()){
            throw new GesperrtException(von);
        }
        else{
            return(kontenHashMap.get(von).abheben(betrag));
        }
    }

    /**
     * Diese Funktion zahlt einen Betrag auf angegebene Kontonummer ein
     * @param auf Key / Kontonummer des Einzahlungskontos
     * @param betrag Höhe der Einzahlung
     * @throws KontoNummerNichtVorhandenException wenn Konto nicht vorhanden ist
     */
    public void geldEinzahlen(long auf, double betrag)throws KontoNummerNichtVorhandenException{
        if(kontenHashMap.get(auf) == null){
            throw new KontoNummerNichtVorhandenException();
        }
        else{
            kontenHashMap.get(auf).einzahlen(betrag);
        }
    }

    /**
     * diese Funktion löscht ein Konto aus unserer Bank
     * @param nummer gewünschte zu löschende Kontonummer
     * @return true wenn es erfolgreich war
     * @throws KontoNummerNichtVorhandenException wenn Kontonummer nicht vorhanden ist
     */
    public boolean kontoLoeschen(long nummer) throws KontoNummerNichtVorhandenException{
        if ( ! kontenHashMap.containsKey(nummer)){
            throw new KontoNummerNichtVorhandenException();
        }
        else{
            kontenHashMap.remove(nummer);
            return true;
        }
    }

    /**
     * Diese Funktion gibt uns den Kontostand des gewünschten Kontos zurück
     * @param nummer ist Kontonummer von gewünschtem Konto
     * @return die Höhe des kontostandes in double format
     * @throws KontoNummerNichtVorhandenException falls Konto so nicht vorhanden
     */
    public double getKontostand(long nummer) throws KontoNummerNichtVorhandenException{
        if(kontenHashMap.get(nummer) == null){
            throw new KontoNummerNichtVorhandenException();
        }
        else{
            return kontenHashMap.get(nummer).getKontostand();
        }
    }

    /**
     * Diese Funktion überweist Geld von A nach B und benutzt dafür das Interface Ueberweisungsfähig
     * @param vonKontonr dieses Konto ist der Ursprung der Überweisung
     * @param nachKontonr diese Konto ist das Ziel der Überweisung
     * @param betrag das ist die Höhe der Überweisung
     * @param verwendungszweck der Grund der Überweisung als String
     * @return true wenn Überweisung geklappt hat, false wenn nicht
     * @throws KontoNummerNichtVorhandenException wenn es eins der beiden Konton nicht gibt
     * @throws GesperrtException wenn das Ursprungskonto gesperrt ist
     */
    public boolean geldUeberweisen(long vonKontonr, long nachKontonr, double betrag, String verwendungszweck)throws KontoNummerNichtVorhandenException, GesperrtException{
        if(kontenHashMap.get(vonKontonr) == null || kontenHashMap.get(nachKontonr) == null){
            throw new KontoNummerNichtVorhandenException();
        }

        if(kontenHashMap.get(vonKontonr).isGesperrt()){
            throw new GesperrtException(vonKontonr);
        }

        else {
            Konto ursprung = kontenHashMap.get(vonKontonr);
            Konto ziel = kontenHashMap.get(nachKontonr);
            //prüfen, ob beide Konten überhaupt überweisen dürfen:
            if(ursprung instanceof Ueberweisungsfaehig && ziel instanceof Ueberweisungsfaehig){
                Ueberweisungsfaehig darfueberweisenvon =  (Ueberweisungsfaehig)ursprung;
                Ueberweisungsfaehig darfueberweisennach = (Ueberweisungsfaehig)ziel;
                if(darfueberweisenvon.ueberweisungAbsenden(betrag, ursprung.getInhaber().getName(), ursprung.getKontonummer(), this.getBankleitzahl(), verwendungszweck)){
                    darfueberweisennach.ueberweisungEmpfangen(betrag, ursprung.getInhaber().getName(), ursprung.getKontonummer(), this.getBankleitzahl(), verwendungszweck);
                    return true;
                }

            }
        }
        //falls der Code hier landet, und nicht oben bei true aus der Funktion springt, soll false ausgegeben werden
        return false;
    }

    /**
     * Diese Funktion sperrt alle Konten, deren Kontostand negativ ist.
     */
    public void pleitegeierSperren(){
        kontenHashMap.values().stream()
                .filter(konto -> konto.getKontostand() < 0)
                .forEach(konto -> konto.sperren());
    }

    /**
     * gibt eine Liste zurück mit Kunden die mindestens den übergebenen Betrag auf ihrem Konto haben
     * @param minimum ist der Betrag der mindestens auf dem Konto sein muss
     * @return Liste mit Kunden, die den Mindestbetrag auf ihrem Konto haben
     */
    public List<Kunde> getKundenMitVollemKonto(double minimum){
        List<Kunde> hilfsKunde = kontenHashMap.values().stream()
                .filter(konto -> konto.getKontostand() > minimum)
                .map(konto -> konto.getInhaber())
                .toList();
        return hilfsKunde;
    }

    /**
     * gibt Vornamen, Nachnamen und Adresse aller Kunden aus
     * @return String mit Vorname, Nachname, Adresse der Bankkunden
     */
    public String getKundenadressen(){

        return kontenHashMap.values().stream()
                .map(konto -> konto.getInhaber())
                .distinct()
                .sorted((kunde1, kunde2) -> kunde1.compareTo(kunde2))
                .map(kunde -> kunde.getVorname() + " " + kunde.getNachname() + " " + kunde.getAdresse())
                .collect(Collectors.joining(", "));
    }

    /**
     * erstellt eine Liste mit allen Kontonummern aus, die im Bereich der bereits vergebenen Kontonummern sind, aber nichtmehr mit einem Konto belegt sind
     * @return Liste mit diesen freien Kontonummern
     */
    public List<Long> getKontonummernLuecken(){
        //Liste mit allen möglichen Kontonummern:
        List <Long> allemoeglichenKontonummern = LongStream.rangeClosed(1, myZaehlwert)
                .boxed()
                .toList();
        //benutzte Kontonummern:
        List <Long> benutzteKontonummern = kontenHashMap.values().stream()
                .map(Konto::getKontonummer)
                .toList();
        //Luecken:
        return allemoeglichenKontonummern.stream()
                .filter(i -> !benutzteKontonummern.contains(i))
                .toList();
    }

    @Override
    /**
     * Die clone() Methode wird überschrieben und kann genutzt werden, um von einer Bank eine persistente Kopie zu machen
     */
    public Bank clone() {

        ByteArrayOutputStream ziel = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(ziel)) {
            oos.writeObject(this);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        byte[] output = ziel.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream(output);
        Bank bank;
        try (ObjectInputStream outcome = new ObjectInputStream(bais)) {
            bank = (Bank) outcome.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return bank;


        /**
         * clonen durch Serialisierung
         * schauen ob alle Attribute serialisierbar sind
         * this verwenden nicht
         *
         * zweite Aufgabe:
         * printf("einText %d : ..." variable);
         * dazwischen dann formattieren
         *
         */

    }
}

