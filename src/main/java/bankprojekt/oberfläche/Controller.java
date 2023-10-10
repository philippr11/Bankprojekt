package bankprojekt.oberfläche;

import bankprojekt.verarbeitung.GesperrtException;
import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Kunde;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Diese Klasse stellt den Controller der Kontenviews dar.
 */
public class Controller extends Application {
    private Kunde kunde;
    private Girokonto girokonto;
    private KontoOberflaeche kontoOberflaeche;


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.kontoOberflaeche = new KontoOberflaeche(this);
        Scene scene = new Scene(this.kontoOberflaeche, 300, 275);
        primaryStage.setScene (scene);
        primaryStage.setTitle("Kontoanzeige:");

        this.kunde = new Kunde("Philipp", "Röber", "Berlin", "11.02.99");
        this.girokonto = new Girokonto(kunde, 12345678, 1000);

        this.kontoOberflaeche.setKontonummer(this.girokonto.getKontonummer());
        this.kontoOberflaeche.bindAdresseProperty(this.kunde.adresseProperty());
        this.kontoOberflaeche.bindGesperrtProperty(this.girokonto.gesperrtProperty());
        this.kontoOberflaeche.bindKontostandProperty(this.girokonto.kontostandProperty());

        primaryStage.show();
    }

    /**
     * mit dieser Methode kann der gewünschte Betrag eingezahlt werden (in double Form)
     * @param betrag höhe der Einzahlung
     */
    public void einzahlen(double betrag) {
        try {
            this.girokonto.einzahlen(betrag);
        } catch (IllegalArgumentException e) {
            this.kontoOberflaeche.setMeldung("Der Betrag muss positiv sein");
        }
    }

    /**
     * mit dieser Methode kann der gewünschte Betrag eingezahlt werden (in String Form)
     * @param betrag höhe der Einzahlung
     */
    public void einzahlen(String betrag){
        try{
            double betragDouble = Double.parseDouble(betrag);
            this.einzahlen(betragDouble);
        }catch (NumberFormatException | NullPointerException e) {
            this.kontoOberflaeche.setMeldung("Bitte Zahl eingeben");
        }
    }

    /**
     * mit dieser Methode kann der gewünschte Betrag abehoben werden (in String Form)
     * @param betrag höhe der Abhebung
     */
    public void abheben(String betrag){
        try{
            double betragDouble = Double.parseDouble(betrag);
            this.abheben(betragDouble);
        }catch (NumberFormatException | NullPointerException e) {
            this.kontoOberflaeche.setMeldung("Bitte Zahl eingeben");
        }
    }

    /**
     * mit dieser Methode kann der gewünschte Betrag abehoben werden (in double Form)
     * @param betrag höhe der Abhebung
     */
    public void abheben(double betrag) {
        try {
            this.girokonto.abheben(betrag);
        } catch (GesperrtException e) {
            this.kontoOberflaeche.setMeldung("Das Konto ist gesperrt");
        } catch (IllegalArgumentException e) {
            this.kontoOberflaeche.setMeldung("Der Betrag muss positiv sein");
        }
    }





}
