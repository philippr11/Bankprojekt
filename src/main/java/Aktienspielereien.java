import bankprojekt.verarbeitung.Aktie;
import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Kunde;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Aktienspielereien {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Aktie testaktie1 = new Aktie("BASF", "WKNBASF", 30.0);
        //Aktie testaktie2 = new Aktie("BAYER", "WKNBAYER", 300.0);
        //Aktie testaktie3 = new Aktie("BMW", "WKNBMW", 10.0);

        Kunde ich = new Kunde();
        Girokonto testkonto = new Girokonto(ich, 1234,1000);
        testkonto.einzahlen(1000);
        Future<Double> kaufauftrag1 = testkonto.kaufauftrag(testaktie1, 10, 31);
        System.out.println("Kaufauftrag 1: " + kaufauftrag1.get());
        Future<Double> verkaufauftrag1 = testkonto.verkaufauftrag("WKNBASF", 32);
        System.out.println("Verkaufauftrag 1: " + verkaufauftrag1.get());
    }
}
