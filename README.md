# ASCO — Prijava problema (QC)

Mobilna web-forma za montažere: prijava problema/reklamacije sa slikama. Popuni → dodaj slike → napravi **PDF report** → pošalji na Viber/Telegram (sve u jednom fajlu).

Sve radi u browseru (bez servera). Podaci se **ne šalju** ni na kakav server — PDF se pravi na telefonu i dijeli ručno.

## Objava na GitHub Pages
1. Napravi novi **Public** repozitorij.
2. Uploaduj `prijava-problema.html` (i ovaj `README.md`).
3. **Settings → Pages → Deploy from a branch → `main` / `/ (root)` → Save**.
4. Za ~1 min dobiješ link:
   `https://<korisnik>.github.io/<repo>/prijava-problema.html`
   — taj link zakači (pin) u Viber/Telegram grupu.

## Korištenje (montažer)
- Popuni polja. Obavezno: **Proizvod**, **Vrsta greške**, **Opis**.
- Dodaj ili uslikaj slike (detalj greške + prije/poslije).
- **Napravi report** → **Pošalji PDF** (bira Viber/Telegram) ili **Preuzmi**.

## Ažuriranje forme
Zamijeni `prijava-problema.html` novom verzijom: *Add file → Upload files → Commit*. Link ostaje isti.

## Prilagođavanje
- **Boja:** promijeni `--accent` u `<style>` (trenutno `#1e6fd9`).
- **Logo / naziv:** uredi blok `.brand` (u `<header>`) i `.rbrand` (u JS funkciji `buildReportNode`). Trenutno je ASCO tekstualni logo + "sito" ikonica; može se ubaciti pravi logo (`<img src="...">`).
- **Polja:** dodaj/ukloni `.card` blokove u formi; ista polja se odražavaju u PDF-u kroz funkciju `rows()`.

## Napomene
- Za generisanje PDF-a potreban je internet (učitava `html2canvas` i `jsPDF` sa cdnjs). Stranica je ionako na linku, pa je to u redu.
- Slike se automatski smanjuju prije ubacivanja u PDF (brže + manji fajl).

## Fajlovi
- `prijava-problema.html` — kompletna forma (jedan fajl, sve uključeno)
- `README.md` — ovo uputstvo

---

# Verzija sa serverom: Java Spring Boot + REST API

Pored statične forme (`index.html` u rootu, za GitHub Pages) repo sadrži i **Spring Boot** aplikaciju gdje frontend (HTML/CSS/JS) sve radi **preko API-ja**, a prijave i slike se čuvaju u bazi.

## Pokretanje
Treba: **Java 21** i **Maven**.
```bash
mvn spring-boot:run
```
- Forma: http://localhost:8080/
- Pregled prijava (promjena statusa, brisanje): http://localhost:8080/pregled.html
- Baza (H2 konzola): http://localhost:8080/h2-console — JDBC URL `jdbc:h2:file:./data/prijave`, user `sa`

Gotov `.jar` za server: `mvn package` → `java -jar target/prijava-neusaglasenosti-0.1.0.jar`

## API
| Metoda | Putanja | Šta radi |
|---|---|---|
| GET | `/api/prijave?status=NOVA` | lista (status opciono) |
| GET | `/api/prijave/{id}` | jedna prijava |
| POST | `/api/prijave` | nova prijava (JSON) → `201 Created` |
| PATCH | `/api/prijave/{id}/status` | `{"status":"U_OBRADI"}` |
| DELETE | `/api/prijave/{id}` | brisanje → `204` |
| POST | `/api/prijave/{id}/slike` | upload slika (multipart, polje `slike`) |
| GET | `/api/prijave/{id}/slike/{slikaId}` | sama slika |

Primjer:
```bash
curl -X POST localhost:8080/api/prijave -H 'Content-Type: application/json' \
  -d '{"proizvod":"SD compact","vrstaGreske":"INTERNA","opis":"Rupe pomjerene"}'
```

## Struktura
```
src/main/java/ba/asco/prijava/
  model/        JPA entiteti (Prijava, Slika) + enumi        → tabele u bazi
  repository/   Spring Data interfejsi                       → SQL bez pisanja SQL-a
  dto/          PrijavaRequest / PrijavaResponse (JSON oblik) → ugovor sa frontendom
  service/      poslovna logika
  web/          REST kontroler + obrada grešaka               → HTTP ulaz
src/main/resources/
  application.properties   port, baza, limit uploada
  static/                  frontend koji Spring servira
    index.html, pregled.html
    css/style.css
    js/api.js      jedino mjesto sa fetch() pozivima
    js/forma.js    logika forme
    js/pregled.js  logika liste
```
Testovi: `mvn test`
