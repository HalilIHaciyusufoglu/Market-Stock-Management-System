package MarketStokYonetimi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MusteriController {

    Islemler sistem = Baslangic.ortakSistem;
    private ObservableList<Urun> sepet = FXCollections.observableArrayList();

    @FXML private TableView<Urun> tabloUrunler;
    @FXML private TableColumn<Urun, String> colUrunAd;
    @FXML private TableColumn<Urun, Double> colUrunFiyat;
    @FXML private TableColumn<Urun, Integer> colUrunStok;

    @FXML private TableView<Urun> tabloSepet;
    @FXML private TableColumn<Urun, String> colSepetAd;
    @FXML private TableColumn<Urun, Double> colSepetFiyat;
    
    @FXML private Label lblToplam;

    @FXML
    public void initialize() {
        colUrunAd.setCellValueFactory(new PropertyValueFactory<>("ad"));
        colUrunFiyat.setCellValueFactory(new PropertyValueFactory<>("fiyat"));
        colUrunStok.setCellValueFactory(new PropertyValueFactory<>("miktar"));
        
        colSepetAd.setCellValueFactory(new PropertyValueFactory<>("ad"));
        colSepetFiyat.setCellValueFactory(new PropertyValueFactory<>("fiyat"));
        
        tabloUrunler.setItems(sistem.VerileriListeyeCevir());
        tabloSepet.setItems(sepet);
    }
    @FXML
    public void btnSepeteEkle() {
        Urun secili = tabloUrunler.getSelectionModel().getSelectedItem();
        if (secili == null) {
            Uyari("Lütfen önce listeden bir ürün seçin.");
            return;
        }
        if (secili.getMiktar() <= 0) {
            Uyari("Bu ürünün stoğu tükenmiş!");
            return;
        }
        int sepettekiAdet = 0;
        for (Urun u : sepet) {

            if (u.getBarkod().equals(secili.getBarkod())) {
                sepettekiAdet++;
            }
        }
        if (sepettekiAdet + 1 > secili.getMiktar()) {
            Uyari("Stok Yetersiz!\nBu üründen stokta sadece " + secili.getMiktar() + " adet var.\nSepetinizde zaten " + sepettekiAdet + " adet mevcut.");
            return; 
        }
        sepet.add(secili);
        Hesapla();
    }
    @FXML
    public void btnSatinAl() {
        if (sepet.isEmpty()) return;
        for (Urun u : sepet) {
            sistem.SatisYap(u.getBarkod(), 1);
        }
        sepet.clear();
        Hesapla();
        tabloUrunler.refresh();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Satın alma başarılı!");
        alert.show();
    }

    private void Hesapla() {
        double toplam = 0;
        for (Urun u : sepet) toplam += u.getFiyat();
        lblToplam.setText("TOPLAM : " + String.format("%.2f", toplam) + " TL");
    }
    private void Uyari(String mesaj) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Uyarı");
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }
}