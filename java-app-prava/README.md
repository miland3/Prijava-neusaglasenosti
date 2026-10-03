# java-app-prava

Mala Spring Boot aplikacija za učenje: **3 korisnika, 3 funkcije, 3 vrste podataka**, s pravima pristupa po matrici.

## Matrica prava

| Korisnik / lozinka | Pregled | Unos | Izvoz | Prijave | Narudžbe | Ponude |
|---|:-:|:-:|:-:|:-:|:-:|:-:|
| `sef` / `sef123` | ✅ | | | ✅ | ✅ | ✅ |
| `nabavka` / `nab123` | ✅ | ✅ | | | ✅ | ✅ |
| `revizor` / `rev123` | | | ✅ | ✅ | ✅ | ✅ |

Akcija je dozvoljena samo ako korisnik ima **i funkciju i vrstu podatka**. U suprotnom server vraća `403`.

## Pokretanje

Potrebno: **Java 21** i **Maven**.

```bash
cd java-app-prava
mvn spring-boot:run
```

Zatim otvori http://localhost:8080 i prijavi se kao jedan od korisnika iz tabele.

## Testovi

```bash
mvn test
```

`MatricaPravaTest` provjerava svih 27 kombinacija (3 korisnika × 3 funkcije × 3 vrste podataka) i pristup bez prijave.

## Probaj iz terminala

```bash
curl -u nabavka:nab123 localhost:8080/api/podaci/NARUDZBE        # 200
curl -u nabavka:nab123 localhost:8080/api/podaci/PRIJAVE         # 403
curl -u revizor:rev123 localhost:8080/api/podaci/PRIJAVE/izvoz   # 200 (CSV)
curl -u nabavka:nab123 localhost:8080/api/ja                     # moje značke
```

## Gdje je šta

| Fajl | Uloga |
|---|---|
| `security/SecurityConfig.java` | Korisnici i njihove značke (`F_*` funkcije, `D_*` podaci) |
| `controller/PodaciController.java` | Tri funkcije, svaka s `@PreAuthorize` provjerom |
| `controller/KorisnikController.java` | `/api/ja` (moje značke) i `/api/csrf` (token za POST) |
| `model/Zapis.java`, `model/TipPodatka.java` | Podaci |
| `PocetniPodaci.java` | Primjeri podataka pri pokretanju |
| `resources/static/index.html` | Frontend |
| `test/.../MatricaPravaTest.java` | Test cijele matrice |

## Napomene

- Korisnici su u memoriji, a baza (H2) se briše pri gašenju. U pravoj aplikaciji bi oboje bilo u trajnoj bazi.
- Frontend samo **sakriva** dugmad. Zabranu sprovodi backend, što možeš provjeriti opcijom "Prikaži i zabranjeno".
