package MarketStokYonetimi;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GirisController {

    @FXML
    public void btnYoneticiTiklandi() { Ac("AdminPanel.fxml", "Yönetici Paneli"); }

    @FXML
    public void btnMusteriTiklandi() { Ac("Musteri.fxml", "Müşteri Alışveriş Ekranı"); }

    private void Ac(String dosya, String baslik) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(dosya));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(baslik);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}