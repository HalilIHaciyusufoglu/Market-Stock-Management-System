package MarketStokYonetimi;

import java.time.LocalDate;

public class Urun {
    String ad;
    double fiyat;
    int miktar;
    String kategori;
    String barkod;
    LocalDate stt;
    
  
    Urun ileri;
    Urun sag;
    Urun sol;

    public Urun(String ad, double fiyat, int miktar, String kategori, String barkod, LocalDate stt) {
        this.ad = ad;
        this.fiyat = fiyat;
        this.miktar = miktar;
        this.kategori = kategori;
        this.barkod = barkod;
        this.stt = stt;
        
        this.ileri = null;
        this.sag = null;
        this.sol = null;
    }
    
  
    public String getAd() { return ad; }
    public double getFiyat() { return fiyat; }
    public int getMiktar() { return miktar; }
    public String getKategori() { return kategori; }
    public String getBarkod() { return barkod; }
    public LocalDate getStt() { return stt; }
}