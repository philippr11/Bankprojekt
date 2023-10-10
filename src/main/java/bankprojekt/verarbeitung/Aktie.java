package bankprojekt.verarbeitung;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.Executors.newScheduledThreadPool;

/**
 * Diese Klasse implement das Objekt der Aktie, die gehandelt werden kann
 */
public class Aktie {

    /**
     * eine Sperre um die Threads der Aktie zu blockieren
     */
    private Lock lock = new ReentrantLock();
    /**
     * eine Bedingung durch die Threads gesperrt werden können
     */
    private Condition condition = lock.newCondition();
    /**
     * Der Name der Aktie
     */
    public String name;
    /**
     * Die WKN der Aktie im String Format
     */
    public String wertpapierKennnummer;
    /**
     * der Kurs der Aktie als Double repräsentiert
     */
    public double kurs;


    /**
     * Konstruktor der Aktie
     * @param name enthält den Namen der Aktie
     * @param wertpapierKennnummer enthält die WKN der Aktie
     * @param kurs enthält den Kurs, der sich jede Sekunde ändert zufällig um +-3% ändert und das dann allen mitteilt
     */
    public Aktie(String name, String wertpapierKennnummer, double kurs){
        this.name = name;
        this.wertpapierKennnummer = wertpapierKennnummer;
        this.kurs = kurs;

        ScheduledExecutorService service = Executors.newScheduledThreadPool(0);
        Runnable task = () -> {
            lock.lock();
            this.kurs = this.kurs * (1 + (erzeugeZufallszahl() * 0.01));
            condition.signalAll();
            System.out.println(this.name + ": "+ this.kurs);
            lock.unlock();
        };
        service.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * transportiert die Bedingung nach außen
     * @return
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * Hilfsfunktion um eine Zufallszahl zwischen -3 und 3 zu erzeugen
     * @return Zufallszahl zwischen -3 und 3
     */
    private double erzeugeZufallszahl(){
        Random random = new Random();
        return random.nextDouble(-3, 3);
    }

    /**
     * derzeitigen Lockzustand anzeigen
     * @return derzeitigen Lock Zustand
     */
    public Lock getLock() {
        return lock;
    }

    /**
     * gibt den Kurs aus
     * @return Kurs der Aktie
     */
    public double getKurs() {
        return this.kurs;
    }

    /**
     * gibt den Namen der Aktie aus
     * @return Aktienname
     */
    public String getName() {
        return name;
    }

    /**
     * gibt WKN aus
     * @return WKN der Aktie
     */
    public String getWertpapierKennnummer() {
        return wertpapierKennnummer;
    }
}


/**
 * Notizen:
 * ScheduledExecutorService verwenden im Konstruktor
 * schedulewithfixeddelay
 *
 *
 * Konto:
 * public Future<Double> kaufauftrag (...){
 *     Future<Double> zurück;
 *     zurück = service.submit() ->
 *     {
 *     solange Kurs zu hoch condition.await()       //Gegenstück dazu ist Signal
 *     Aktien kaufen
 *     return kaufpreis;
 *     );
 *
 * }
 *return zurueck
 *
 *
 *
 * in Aktie fehlt noch setKurs
 * kursAendern
 * condition.signal
 * getKondition
 */

