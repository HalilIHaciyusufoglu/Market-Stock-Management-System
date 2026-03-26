# 🛒 Market Stok Yönetimi ve Algoritma Optimizasyonu

![Status](https://img.shields.io/badge/Durum-Tamamlandı-blue?style=for-the-badge)

Bu proje, bir işletmenin envanter ve stok takibini gerçek zamanlı olarak yönetmek amacıyla geliştirilmiş Java tabanlı bir masaüstü uygulamasıdır. Projenin temel mühendislik odak noktası; büyük veri setleri üzerinde işlem yaparken bellek yönetimini sağlamak ve arama/sıralama operasyonlarının zaman karmaşıklığını (Time Complexity) minimuma indirmektir.

### 🚀 Mühendislik Yaklaşımı ve Veri Yapıları
Bu projede hazır kütüphaneler yerine, temel veri yapıları sıfırdan implemente edilerek sisteme entegre edilmiştir:

* **Ağaç (Tree) Yapıları:** Binlerce ürün arasından milisaniyeler içinde spesifik bir ürünü bulmak (hızlı arama algoritmaları) için kullanılmıştır.
* **Bağlı Listeler (Linked List):** Dinamik olarak büyüyüp küçülebilen stok listelerinin bellek (memory) dostu bir şekilde yönetilmesi sağlanmıştır.
* **Yığın ve Kuyruk (Stack & Queue):** Kasa/müşteri işlem sıralamaları (FIFO) ve stok üzerinde yapılan hatalı işlemleri geri alma (Undo - LIFO) mekanizmaları için kurgulanmıştır.
* **Algoritma Optimizasyonu:** Ürünleri fiyat, kategori veya isme göre listelemek için verimli sıralama (Sorting) algoritmaları kullanılmıştır.

### 🛠️ Kullanılan Teknolojiler
* **Programlama Dili:** Java
* **Temel Kavramlar:** Veri Yapıları (Data Structures), Algoritma Analizi, Nesne Yönelimli Programlama (OOP), Temiz Kod (Clean Code)
