package MarketStokYonetimi;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DosyaYoneticisi {
    private static final String URUN_DOSYASI = "urunler.txt";
    private static final String LOG_DOSYASI = "islem_gecmisi.txt";

    public void VerileriKaydet(Urun bas) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(URUN_DOSYASI))) {
            Urun temp = bas;
            while (temp != null) {
                String tarih = (temp.stt != null) ? temp.stt.toString() : "null";
             
                String satir = temp.ad + "," + temp.fiyat + "," + temp.miktar + "," + 
                               temp.kategori + "," + temp.barkod + "," + tarih;
                writer.write(satir);
                writer.newLine();
                temp = temp.ileri;
            }
        } catch (IOException e) {
            System.out.println("Kaydetme hatası: " + e.getMessage());
        }
    }

    public void VerileriYukle(Islemler sistem) {
        File dosya = new File(URUN_DOSYASI);
        if (!dosya.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(dosya))) {
            String satir;
            while ((satir = reader.readLine()) != null) {
                String[] veri = satir.split(",");
                if (veri.length == 6) {
                    String ad = veri[0];
                    double fiyat = Double.parseDouble(veri[1]);
                    int miktar = Integer.parseInt(veri[2]);
                    String kategori = veri[3];
                    String barkod = veri[4];
                    LocalDate tarih = null;
                    if (!veri[5].equals("null")) {
                        try { tarih = LocalDate.parse(veri[5]); } catch (Exception e) {}
                    }
                    
                    sistem.SonaEkle(new Urun(ad, fiyat, miktar, kategori, barkod, tarih), false);
                }
            }
        } catch (Exception e) {
            System.out.println("Yükleme hatası: " + e.getMessage());
        }
    }

    public void LogEkle(String islemMesaji) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_DOSYASI, true))) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String zaman = dtf.format(LocalDateTime.now());
            writer.write("[" + zaman + "] " + islemMesaji);
            writer.newLine();
        } catch (IOException e) { }
    }

    public void SiparisDosyasiOlustur(String raporIcerigi) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("siparis_listesi.txt"))) {
            writer.write(raporIcerigi);
        } catch (IOException e) { }
    }
}