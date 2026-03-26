package MarketStokYonetimi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Baslangic extends Application {


    public static Islemler ortakSistem = new Islemler();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
 
            new DosyaYoneticisi().VerileriYukle(ortakSistem);


            Parent root = FXMLLoader.load(getClass().getResource("Giris.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Market Otomasyonu");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}