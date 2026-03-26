package MarketStokYonetimi;

import java.time.LocalDate;
import java.io.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Islemler {
    Urun bas;
    Urun son;
    DosyaYoneticisi dosyaYoneticisi = new DosyaYoneticisi();

    public Islemler() {
        bas = null;
        son = null;
    }

    public ObservableList<Urun> VerileriListeyeCevir() {
        ObservableList<Urun> liste = FXCollections.observableArrayList();
        Urun gezgin = bas;
        while (gezgin != null) {
            liste.add(gezgin);
            gezgin = gezgin.ileri;
        }
        return liste;
    }

    private String LimitKontrolu(int miktar, double fiyat) {
        if (miktar > 1000) {
            return "Hata: Stok miktarı 1000 adedi geçemez!";
        }
        if (fiyat > 5000) {
            return "Hata: Ürün fiyatı 5000 TL'yi geçemez!";
        }
        if (miktar < 0 || fiyat < 0) {
            return "Hata: Negatif değer girilemez!";
        }
        return null;
    }

    private boolean stringlerEsitMi(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        }
        if (str1.length() != str2.length()) {
            return false;
        }
        char[] dizi1 = str1.toCharArray();
        char[] dizi2 = str2.toCharArray();
        for (int i = 0; i < dizi1.length; i++) {
            if (dizi1[i] != dizi2[i]) {
                return false;
            }
        }
        return true;
    }

    // --- TEMEL İŞLEMLER ---

    public String Push(Urun yeni) {
        String limitHata = LimitKontrolu(yeni.miktar, yeni.fiyat);
        if (limitHata != null) {
            return limitHata;
        }
        
        // TARİH KONTROLLERİ
        if (TarihGecmisMi(yeni.stt)) {
            return "Hata: Ürünün tarihi geçmiş! Ekleme iptal edildi.";
        }
        if (TarihSiniriAsildiMi(yeni.stt)) {
            return "Hata: Tarih 01.01.2035'ten büyük olamaz!";
        }

        if (!EklemeUygunMu(yeni)) {
            return "Hata: Bilgiler hatalı veya barkod çakışması.";
        }
        
        if (bas == null) {
            bas = yeni;
            son = yeni;
        } else {
            yeni.ileri = bas;
            bas = yeni;
        }
        IslemSonrasi("Ekleme (Push)", yeni);
        return "Başarılı: " + yeni.ad + " başa eklendi.";
    }

    public String SonaEkle(Urun yeni, boolean kaydet) {
        if (kaydet) {
            String limitHata = LimitKontrolu(yeni.miktar, yeni.fiyat);
            if (limitHata != null) {
                return limitHata;
            }
            if (TarihGecmisMi(yeni.stt)) {
                return "Hata: Ürünün tarihi geçmiş!";
            }
            if (TarihSiniriAsildiMi(yeni.stt)) {
                return "Hata: Tarih 01.01.2035'ten büyük olamaz!";
            }
            if (BarkodVarMi(yeni.barkod)) {
                return "Hata: Barkod zaten var.";
            }
        }
        yeni.ileri = null;
        if (bas == null) {
            bas = yeni;
            son = yeni;
        } else {
            son.ileri = yeni;
            son = yeni;
        }
        if (kaydet) {
            IslemSonrasi("Ekleme (Sona)", yeni);
        }
        return "Başarılı: " + yeni.ad + " sona eklendi.";
    }

    public String ArayaEkle(Urun yeni, int index) {
        String limitHata = LimitKontrolu(yeni.miktar, yeni.fiyat);
        if (limitHata != null) {
            return limitHata;
        }
        if (TarihGecmisMi(yeni.stt)) {
            return "Hata: Ürünün tarihi geçmiş!";
        }
        if (TarihSiniriAsildiMi(yeni.stt)) {
            return "Hata: Tarih 01.01.2035'ten büyük olamaz!";
        }
        if (!EklemeUygunMu(yeni)) {
            return "Hata: Bilgiler uygun değil.";
        }
        if (index <= 0) {
            return Push(yeni);
        }
        Urun temp = bas;
        int sayac = 0;
        while (temp != null && sayac < index - 1) {
            temp = temp.ileri;
            sayac++;
        }
        if (temp == null) {
            return SonaEkle(yeni, true);
        } else {
            yeni.ileri = temp.ileri;
            temp.ileri = yeni;
            if (yeni.ileri == null) {
                son = yeni;
            }
            IslemSonrasi("Ekleme (Araya)", yeni);
            return "Başarılı: " + yeni.ad + " araya eklendi.";
        }
    }

    // --- SİLME İŞLEMLERİ ---

    public String Pop() {
        if (bas == null) {
            return "Liste boş.";
        }
        Urun silinen = bas;
        bas = bas.ileri;
        if (bas == null) {
            son = null;
        }
        IslemSonrasi("Silme (Pop)", silinen);
        return "Silindi (Pop): " + silinen.ad;
    }

    public String SondanSil() {
        if (bas == null) {
            return "Liste boş.";
        }
        String ad = son.ad;
        if (bas == son) {
            bas = null;
            son = null;
        } else {
            Urun temp = bas;
            while (temp.ileri != son) {
                temp = temp.ileri;
            }
            temp.ileri = null;
            son = temp;
        }
        dosyaYoneticisi.VerileriKaydet(bas);
        return "Silindi (Sondan): " + ad;
    }

    public String Silme(String barkod) {
        if (bas == null) {
            return "Liste boş.";
        }
        if (stringlerEsitMi(bas.barkod, barkod)) {
            return Pop();
        }
        Urun onceki = bas;
        Urun a = onceki.ileri;
        while (a != null && !stringlerEsitMi(a.barkod, barkod)) {
            onceki = onceki.ileri;
            a = a.ileri;
        }
        if (a != null) {
            onceki.ileri = a.ileri;
            if (a.ileri == null) {
                son = onceki;
            }
            IslemSonrasi("Silme (Barkod)", a);
            return "Silindi: " + a.ad;
        }
        return "Ürün bulunamadı.";
    }

    public String IndistenSil(int index) {
        if (bas == null || index < 0) {
            return "Hatalı işlem.";
        }
        if (index == 0) {
            return Pop();
        }
        Urun onceki = bas;
        int sayac = 0;
        while (onceki != null && sayac < index - 1) {
            onceki = onceki.ileri;
            sayac++;
        }
        if (onceki != null && onceki.ileri != null) {
            Urun silinen = onceki.ileri;
            onceki.ileri = silinen.ileri;
            if (onceki.ileri == null) {
                son = onceki;
            }
            IslemSonrasi("Silme (İndeks)", silinen);
            return "Silindi: " + silinen.ad;
        }
        return "Belirtilen indekste ürün yok.";
    }

    public String Guncelle(String eskiBarkod, int miktar, double fiyat) {
        String limitHata = LimitKontrolu(miktar, fiyat);
        if (limitHata != null) {
            return limitHata;
        }
        Urun a = bas;
        while (a != null) {
            if (stringlerEsitMi(a.barkod, eskiBarkod)) {
                a.miktar = miktar;
                a.fiyat = fiyat;
                dosyaYoneticisi.VerileriKaydet(bas);
                return "Güncellendi: " + a.ad;
            }
            a = a.ileri;
        }
        return "Ürün bulunamadı.";
    }

    public boolean SatisYap(String barkod, int adet) {
        Urun a = bas;
        while (a != null) {
            if (stringlerEsitMi(a.barkod, barkod)) {
                if (a.miktar >= adet) {
                    a.miktar -= adet;
                    dosyaYoneticisi.LogEkle("SATIŞ: " + a.ad + " satıldı.");
                    dosyaYoneticisi.VerileriKaydet(bas);
                    return true;
                }
                return false;
            }
            a = a.ileri;
        }
        return false;
    }

    // --- SIRALAMA (AĞAÇ) ---

    public String MiktaraGoreSirala() {
        return AgaciKurVeListele(1, "Miktara Göre Sıralı Liste");
    }

    public String FiyataGoreSirala() {
        return AgaciKurVeListele(2, "Fiyata Göre Sıralı Liste");
    }

    public String TariheGoreSirala() {
        return AgaciKurVeListele(3, "Tarihe Göre Sıralı Liste");
    }

    private String AgaciKurVeListele(int mod, String baslik) {
        if (bas == null) {
            return "Liste boş.";
        }
        Urun agacKok = null;
        Urun gezgin = bas;
        while (gezgin != null) {
            gezgin.sol = null;
            gezgin.sag = null;
            if (mod == 1) {
                agacKok = EkleMiktar(agacKok, gezgin);
            } else if (mod == 2) {
                agacKok = EkleFiyat(agacKok, gezgin);
            } else if (mod == 3) {
                agacKok = EkleTarih(agacKok, gezgin);
            }
            gezgin = gezgin.ileri;
        }
        String sonuc = "--- " + baslik + " ---\n\n";
        sonuc = sonuc + AgaciYazdir(agacKok);
        return sonuc;
    }

    private Urun EkleMiktar(Urun kok, Urun yeni) {
        if (kok == null) {
            return yeni;
        }
        if (yeni.miktar < kok.miktar) {
            kok.sol = EkleMiktar(kok.sol, yeni);
        } else {
            kok.sag = EkleMiktar(kok.sag, yeni);
        }
        return kok;
    }

    private Urun EkleFiyat(Urun kok, Urun yeni) {
        if (kok == null) {
            return yeni;
        }
        if (yeni.fiyat < kok.fiyat) {
            kok.sol = EkleFiyat(kok.sol, yeni);
        } else {
            kok.sag = EkleFiyat(kok.sag, yeni);
        }
        return kok;
    }

    private Urun EkleTarih(Urun kok, Urun yeni) {
        if (kok == null) {
            return yeni;
        }
        if (yeni.stt != null && new DateCompare().compare(yeni.stt, kok.stt) < 0) {
            kok.sol = EkleTarih(kok.sol, yeni);
        } else {
            kok.sag = EkleTarih(kok.sag, yeni);
        }
        return kok;
    }

    class DateCompare {
        public int compare(LocalDate d1, LocalDate d2) {
            String s1 = d1.toString();
            String s2 = d2.toString();
            for (int i = 0; i < s1.length(); i++) {
                if (s1.charAt(i) != s2.charAt(i)) {
                    return s1.charAt(i) - s2.charAt(i);
                }
            }
            return 0;
        }
    }

    private String AgaciYazdir(Urun kok) {
        if (kok == null) {
            return "";
        }
        String sol = AgaciYazdir(kok.sol);
        String orta = String.format("%-15s | Stok: %-3d | Fiyat: %-6.2f | Tarih: %s\n",
                kok.ad, kok.miktar, kok.fiyat, kok.stt);
        String sag = AgaciYazdir(kok.sag);
        return sol + orta + sag;
    }

    // --- SORGULAMA ---

    public String BarkodileSorgulama(String barkod) {
        Urun temp = bas;
        while (temp != null) {
            if (stringlerEsitMi(temp.barkod, barkod)) {
                return "BULUNDU:\nAd: " + temp.ad + "\nFiyat: " + temp.fiyat + "\nStok: " + temp.miktar;
            }
            temp = temp.ileri;
        }
        return "Bu barkoda ait ürün bulunamadı.";
    }

    public String FiyatileSorgula(double fiyat) {
        String sonuc = "--- Fiyatı " + fiyat + " Olanlar ---\n";
        Urun temp = bas;
        boolean bulundu = false;
        while (temp != null) {
            if (temp.fiyat == fiyat) {
                sonuc = sonuc + "-> " + temp.ad + "\n";
                bulundu = true;
            }
            temp = temp.ileri;
        }
        if (bulundu) {
            return sonuc;
        } else {
            return "Bu fiyatta ürün yok.";
        }
    }

    public String KategoriSorgula(String kat) {
        String sonuc = "--- Kategori: " + kat + " ---\n";
        Urun temp = bas;
        boolean bulundu = false;
        while (temp != null) {
            if (stringlerEsitMi(temp.kategori, kat)) {
                sonuc = sonuc + "-> " + temp.ad + " (" + temp.fiyat + " TL)\n";
                bulundu = true;
            }
            temp = temp.ileri;
        }
        if (bulundu) {
            return sonuc;
        } else {
            return "Bu kategoride ürün yok.";
        }
    }

    public String AdIleDetayliSorgula(String ad) {
        Urun temp = bas;
        while (temp != null) {
            if (stringlerEsitMi(temp.ad, ad)) {
                return "DETAY:\nBarkod: " + temp.barkod + "\nStok: " + temp.miktar + "\nS.T.T: " + temp.stt;
            }
            temp = temp.ileri;
        }
        return "İsimle eşleşen ürün bulunamadı.";
    }

    // --- YARDIMCILAR ---

    public String GenelStokRaporuAl() {
        if (bas == null) {
            return "Stokta hiç ürün yok.";
        }
        String rapor = "=== DETAYLI MARKET STOK RAPORU ===\n";
        rapor = rapor + "Tarih: " + LocalDate.now() + "\n\n";
        rapor = rapor + String.format("%-15s | %-10s | %-10s | %-15s\n", "ÜRÜN", "STOK", "BİRİM FİYAT", "TOPLAM DEĞER");
        rapor = rapor + "-------------------------------------------------------------\n";
        Urun temp = bas;
        double genelToplamDeger = 0;
        int toplamUrunAdedi = 0;
        while (temp != null) {
            double urunToplamDegeri = temp.miktar * temp.fiyat;
            genelToplamDeger += urunToplamDegeri;
            toplamUrunAdedi += temp.miktar;
            rapor = rapor + String.format("%-15s | %-10d | %-10.2f TL | %-15.2f TL\n",
                    temp.ad, temp.miktar, temp.fiyat, urunToplamDegeri);
            temp = temp.ileri;
        }
        rapor = rapor + "-------------------------------------------------------------\n";
        rapor = rapor + "TOPLAM ÜRÜN ADEDİ : " + toplamUrunAdedi + "\n";
        rapor = rapor + "DÜKKANIN TOPLAM DEĞERİ: " + String.format("%.2f", genelToplamDeger) + " TL\n";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("genel_rapor.txt"));
            writer.write(rapor);
            writer.close();
        } catch (IOException e) {
            System.out.println("Rapor yazılamadı.");
        }
        return rapor;
    }

    private boolean EklemeUygunMu(Urun yeni) {
        if (yeni.fiyat < 0 || yeni.miktar < 0) {
            return false;
        }
        if (BarkodVarMi(yeni.barkod)) {
            return false;
        }
        return true;
    }

    public boolean BarkodVarMi(String barkod) {
        Urun a = bas;
        while (a != null) {
            if (stringlerEsitMi(a.barkod, barkod)) {
                return true;
            }
            a = a.ileri;
        }
        return false;
    }

    public boolean TarihGecmisMi(LocalDate stt) {
        if (stt == null) {
            return false;
        }
      
        return new DateCompare().compare(stt, LocalDate.now()) < 0;
    }

  
    public boolean TarihSiniriAsildiMi(LocalDate stt) {
        if (stt == null) {
            return false;
        }
   
        LocalDate limit = LocalDate.of(2035, 1, 1);
        
        return new DateCompare().compare(stt, limit) > 0;
    }

    private void IslemSonrasi(String mesaj, Urun u) {
        dosyaYoneticisi.LogEkle(mesaj + ": " + u.ad);
        dosyaYoneticisi.VerileriKaydet(bas);
    }

    public String SonKullanmaTarihiYakinlaraIndirimYap() {
        Urun temp = bas;
        LocalDate bugun = LocalDate.now();
        int sayac = 0;
        while (temp != null) {
            if (temp.stt != null) {
                int yilFark = temp.stt.getYear() - bugun.getYear();
                int gunFark = temp.stt.getDayOfYear() - bugun.getDayOfYear();
                int toplamFark = (yilFark * 365) + gunFark;
                if (toplamFark > 0 && toplamFark <= 7) {
                    temp.fiyat *= 0.8;
                    sayac++;
                }
            }
            temp = temp.ileri;
        }
        if (sayac > 0) {
            dosyaYoneticisi.VerileriKaydet(bas);
            return sayac + " ürüne indirim uygulandı.";
        }
        return "İndirim yapılacak ürün yok.";
    }

    public void StokGoster(String ad) {
    }
}