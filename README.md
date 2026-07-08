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
