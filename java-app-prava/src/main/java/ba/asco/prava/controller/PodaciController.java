package ba.asco.prava.controller;

import ba.asco.prava.model.TipPodatka;
import ba.asco.prava.model.Zapis;
import ba.asco.prava.repository.ZapisRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tri funkcije: Pregled, Unos, Izvoz.
 *
 * Svaka funkcija provjerava DVIJE stvari:
 *   1. ima li korisnik značku za FUNKCIJU  (F_...)
 *   2. ima li korisnik značku za PODATAK   (D_ + tip iz URL-a)
 * Ako bilo šta fali, Spring vraća 403 Forbidden.
 */
@RestController
@RequestMapping("/api/podaci/{tip}")
public class PodaciController {

    /** Tijelo zahtjeva za unos: samo opis, ostalo popunjava server. */
    public record UnosZahtjev(String opis) {}

    private final ZapisRepository repo;

    public PodaciController(ZapisRepository repo) {
        this.repo = repo;
    }

    // FUNKCIJA 1: Pregled
    @GetMapping
    @PreAuthorize("hasAuthority('F_PREGLED') and hasAuthority('D_' + #tip.name())")
    public List<Zapis> pregled(@PathVariable TipPodatka tip) {
        return repo.findByTipOrderByIdAsc(tip);
    }

    // FUNKCIJA 2: Unos
    @PostMapping
    @PreAuthorize("hasAuthority('F_UNOS') and hasAuthority('D_' + #tip.name())")
    public Zapis unos(@PathVariable TipPodatka tip,
                      @RequestBody UnosZahtjev zahtjev,
                      Authentication korisnik) {
        if (zahtjev.opis() == null || zahtjev.opis().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Opis ne smije biti prazan");
        }
        // Autor dolazi iz provjerenog logina, ne iz forme
        return repo.save(new Zapis(tip, zahtjev.opis().strip(), korisnik.getName()));
    }

    // FUNKCIJA 3: Izvoz u CSV (otvara se u Excelu)
    @GetMapping("/izvoz")
    @PreAuthorize("hasAuthority('F_IZVOZ') and hasAuthority('D_' + #tip.name())")
    public ResponseEntity<byte[]> izvoz(@PathVariable TipPodatka tip) {
        String redovi = repo.findByTipOrderByIdAsc(tip).stream()
                .map(z -> z.getId() + ";" + z.getDatum() + ";" + z.getAutor() + ";"
                        + z.getOpis().replace(";", ","))
                .collect(Collectors.joining("\n"));
        // ﻿ (BOM) da Excel ispravno prikaže č, ć, š, ž, đ
        String csv = "﻿ID;Datum;Autor;Opis\n" + redovi;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + tip + ".csv")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }
}
