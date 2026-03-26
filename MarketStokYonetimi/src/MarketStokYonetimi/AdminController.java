package MarketStokYonetimi;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import java.time.LocalDate;

public class AdminController {

    Islemler sistem = Baslangic.ortakSistem;

    @FXML private TextField txtAd, txtBarkod, txtFiyat, txtMiktar, txtKategori, txtIndex, txtArama;
    @FXML private DatePicker datePicker;
    @FXML private TableView<Urun> tabloUrunler;
    @FXML private TableColumn<Urun, String> colAd, colKategori, colBarkod;
    @FXML private TableColumn<Urun, Double> colFiyat;
    @FXML private TableColumn<Urun, Integer> colMiktar;
    @FXML private TableColumn<Urun, LocalDate> colTarih;

    @FXML
    public void initialize() {
        colAd.setCellValueFactory(new PropertyValueFactory<>("ad"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colBarkod.setCellValueFactory(new PropertyValueFactory<>("barkod"));
        colFiyat.setCellValueFactory(new PropertyValueFactory<>("fiyat"));
        colMiktar.setCellValueFactory(new PropertyValueFactory<>("miktar"));
        colTarih.setCellValueFactory(new PropertyValueFactory<>("stt"));
        
        TabloyuYenile();
    }

    private void TabloyuYenile() {
        tabloUrunler.setItems(sistem.VerileriListeyeCevir());
        tabloUrunler.refresh();
    }
    private boolean GecerliMetinMi(String metin) {
        if (metin == null || metin.isEmpty()) return false;
        return metin.matches("[a-zA-Z0-9 ğüşöçİĞÜŞÖÇ]+");
    }

    private boolean SadeceHarfMi(String metin) {
        if (metin == null || metin.isEmpty()) return false;
        return metin.matches("[a-zA-Z ğüşöçİĞÜŞÖÇ]+");
    }

    // --- BUTONLAR ---
    @FXML public void btnPushTiklandi() { 
        Urun yeni = UrunOlustur(); 
        if (yeni != null) {
            String sonuc = sistem.Push(yeni); 
            BilgiVer(sonuc); 
            TabloyuYenile(); 
        }
    }
    
    @FXML public void btnSonaEkleTiklandi() { 
        Urun yeni = UrunOlustur(); 
        if (yeni != null) {
            String sonuc = sistem.SonaEkle(yeni, true); 
            BilgiVer(sonuc); 
            TabloyuYenile(); 
        }
    }
    
    @FXML public void btnArayaEkleTiklandi() { 
        try { 
            int index = Integer.parseInt(txtIndex.getText());
            Urun yeni = UrunOlustur(); 
            if (yeni != null) {
                String sonuc = sistem.ArayaEkle(yeni, index); 
                BilgiVer(sonuc); 
                TabloyuYenile(); 
            }
        } catch(NumberFormatException e) { 
            Alert("HATA: İndeks kutusuna sadece SAYI giriniz!"); 
        }
    }

    @FXML public void btnPopTiklandi() { 
        String sonuc = sistem.Pop(); 
        BilgiVer(sonuc); 
        TabloyuYenile(); 
    }
    
    @FXML public void btnSondanSilTiklandi() { 
        String sonuc = sistem.SondanSil(); 
        BilgiVer(sonuc); 
        TabloyuYenile(); 
    }
    
    @FXML public void btnIndextenSilTiklandi() { 
        try { 
            String sonuc = sistem.IndistenSil(Integer.parseInt(txtIndex.getText())); 
            BilgiVer(sonuc); 
            TabloyuYenile(); 
        } catch(NumberFormatException e) { 
            Alert("HATA: İndeks kutusuna sadece SAYI giriniz!"); 
        }
    }
    
    @FXML public void btnBarkodSilTiklandi() { 
        String barkod = txtBarkod.getText();
        if (!GecerliMetinMi(barkod)) {
            Alert("HATA: Barkod alanında özel karakter (*,?,/,-) kullanılamaz!");
            return;
        }
        String sonuc = sistem.Silme(barkod); 
        BilgiVer(sonuc); 
        TabloyuYenile(); 
    }

    // --- GÜNCELLEME ---
    @FXML public void btnGuncelleTiklandi() {
        try {
            if (!GecerliMetinMi(txtBarkod.getText())) {
                Alert("HATA: Barkod alanında özel karakter (*,?,/,-) kullanılamaz!");
                return;
            }

            int miktar = Integer.parseInt(txtMiktar.getText());
            double fiyat = Double.parseDouble(txtFiyat.getText());
            
            String sonuc = sistem.Guncelle(txtBarkod.getText(), miktar, fiyat);
            BilgiVer(sonuc);
            TabloyuYenile();
        } catch(NumberFormatException e) { 
            Alert("HATA: Fiyat ve Miktar alanlarına sadece SAYI giriniz!"); 
        }
    }

    // --- SIRALAMA VE RAPORLAMA ---
    @FXML public void btnSiralaMiktar() { String liste = sistem.MiktaraGoreSirala(); UzunBilgiVer("Sıralama Sonuçları (Miktar)", liste); }
    @FXML public void btnSiralaFiyat() { String liste = sistem.FiyataGoreSirala(); UzunBilgiVer("Sıralama Sonuçları (Fiyat)", liste); }
    @FXML public void btnSiralaTarih() { String liste = sistem.TariheGoreSirala(); UzunBilgiVer("Sıralama Sonuçları (Tarih)", liste); }

    @FXML public void btnIndirimYap() { String sonuc = sistem.SonKullanmaTarihiYakinlaraIndirimYap(); BilgiVer(sonuc); TabloyuYenile(); }
    
    @FXML public void btnRaporTiklandi() { 
        String raporSonucu = sistem.GenelStokRaporuAl(); 
        UzunBilgiVer("Genel Stok Durumu ve Ciro", raporSonucu); 
        Alert("Rapor ayrıca 'genel_rapor.txt' dosyasına kaydedildi.");
    }
    
    @FXML public void btnCikisTiklandi() { ((javafx.stage.Stage)txtAd.getScene().getWindow()).close(); }

    // --- ARAMA BUTONLARI ---
    @FXML public void btnAraIsim() { 
        if(!GecerliMetinMi(txtArama.getText())) { Alert("HATA: Özel karakter ile arama yapılamaz!"); return; }
        UzunBilgiVer("Arama Sonucu", sistem.AdIleDetayliSorgula(txtArama.getText())); 
    } 
    @FXML public void btnAraBarkod() { 
        if(!GecerliMetinMi(txtArama.getText())) { Alert("HATA: Özel karakter ile arama yapılamaz!"); return; }
        UzunBilgiVer("Arama Sonucu", sistem.BarkodileSorgulama(txtArama.getText())); 
    }
    @FXML public void btnAraKategori() { 
        if(!SadeceHarfMi(txtArama.getText())) { Alert("HATA: Kategori aramasında rakam veya sembol kullanılamaz!"); return; }
        UzunBilgiVer("Arama Sonucu", sistem.KategoriSorgula(txtArama.getText())); 
    }
    @FXML public void btnAraFiyat() { 
        try { UzunBilgiVer("Arama Sonucu", sistem.FiyatileSorgula(Double.parseDouble(txtArama.getText()))); }
        catch(NumberFormatException e) { Alert("HATA: Fiyat kısmına sadece sayı giriniz!"); }
    }

    // --- YARDIMCILAR ---
    private Urun UrunOlustur() {
        try {

            if (txtAd.getText().isEmpty() || txtBarkod.getText().isEmpty() || 
                txtFiyat.getText().isEmpty() || txtMiktar.getText().isEmpty() || txtKategori.getText().isEmpty()) {
                Alert("Lütfen tüm alanları doldurun.");
                return null;
            }


            if (!GecerliMetinMi(txtAd.getText())) {
                Alert("HATA: Ürün Adı'nda sembol (*, ?, /) kullanılamaz!");
                return null;
            }
            if (!GecerliMetinMi(txtBarkod.getText())) {
                Alert("HATA: Barkod alanında sembol (*, ?, /) kullanılamaz!");
                return null;
            }


            if (!SadeceHarfMi(txtKategori.getText())) {
                Alert("HATA: Kategori alanına RAKAM veya SEMBOL girilemez!\nSadece harf kullanın (Örn: Gida, Temizlik).");
                return null;
            }


            double fiyat = Double.parseDouble(txtFiyat.getText());
            int miktar = Integer.parseInt(txtMiktar.getText());
            
      
            if(sistem.BarkodVarMi(txtBarkod.getText())) {
                Alert("Bu barkod (" + txtBarkod.getText() + ") zaten sistemde var!");
                return null;
            }

            LocalDate tarih = datePicker.getValue();
            if (tarih == null) tarih = LocalDate.now();

            return new Urun(txtAd.getText(), fiyat, miktar, txtKategori.getText(), txtBarkod.getText(), tarih);
            
        } catch (NumberFormatException e) { 
            Alert("HATA: Fiyat ve Miktar alanlarına sadece SAYI giriniz!"); 
            return null; 
        } catch (Exception e) {
            Alert("Bir hata oluştu: " + e.getMessage());
            return null;
        }
    }
    
    private void BilgiVer(String m) {
        if(m == null) return;
        Alert a = new Alert(Alert.AlertType.INFORMATION); 
        a.setTitle("Bilgi"); a.setHeaderText(null); a.setContentText(m); a.show();
    }
    
    private void UzunBilgiVer(String baslik, String icerik) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(baslik); alert.setHeaderText(null);
        
        TextArea textArea = new TextArea(icerik);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
    
    private void Alert(String m) {
        Alert a = new Alert(Alert.AlertType.WARNING); 
        a.setTitle("Hata / Uyarı"); a.setHeaderText(null); a.setContentText(m); 
        a.show();
    }
}