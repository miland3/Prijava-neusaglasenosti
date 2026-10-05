package ba.asco.prijava.service;

import ba.asco.prijava.dto.PrijavaRequest;
import ba.asco.prijava.dto.PrijavaResponse;
import ba.asco.prijava.model.*;
import ba.asco.prijava.repository.PrijavaRepository;
import ba.asco.prijava.repository.SlikaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/** Poslovna logika: kontroler samo prima HTTP zahtjev i prosljeđuje ovdje. */
@Service
@Transactional
public class PrijavaService {

    private final PrijavaRepository prijave;
    private final SlikaRepository slike;

    public PrijavaService(PrijavaRepository prijave, SlikaRepository slike) {
        this.prijave = prijave;
        this.slike = slike;
    }

    @Transactional(readOnly = true)
    public List<PrijavaResponse> sve(Status status) {
        List<Prijava> lista = status == null
                ? prijave.findAllByOrderByKreiranoDesc()
                : prijave.findByStatusOrderByKreiranoDesc(status);
        return lista.stream().map(PrijavaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PrijavaResponse jedna(Long id) {
        return PrijavaResponse.from(nadji(id));
    }

    public PrijavaResponse kreiraj(PrijavaRequest r) {
        Prijava p = new Prijava();
        p.setProizvod(r.proizvod().trim());
        p.setVrstaGreske(r.vrstaGreske());
        p.setDobavljac(r.vrstaGreske() == VrstaGreske.EKSTERNA ? prazno(r.dobavljac()) : null);
        p.setKolicinaSerija(ili(r.kolicinaSerija(), 1));
        p.setKolicinaProvjereno(ili(r.kolicinaProvjereno(), 1));
        p.setKolicinaNeispravno(ili(r.kolicinaNeispravno(), 1));
        NacinUocavanja uoceno = r.uoceno() == null ? NacinUocavanja.VIZUELNO : r.uoceno();
        p.setUoceno(uoceno);
        p.setIzmjereneVrijednosti(uoceno == NacinUocavanja.MJERENJE ? prazno(r.izmjereneVrijednosti()) : null);
        p.setOpis(r.opis().trim());
        p.setUzroci(r.uzroci() == null ? new ArrayList<>() : new ArrayList<>(r.uzroci()));
        p.setPonavljajuca(r.ponavljajuca());
        p.setUradjeno(prazno(r.uradjeno()));
        p.setPrijavio(prazno(r.prijavio()));
        return PrijavaResponse.from(prijave.save(p));
    }

    public PrijavaResponse promijeniStatus(Long id, Status status) {
        Prijava p = nadji(id);
        p.setStatus(status);
        return PrijavaResponse.from(p);
    }

    public void obrisi(Long id) {
        prijave.delete(nadji(id));
    }

    public PrijavaResponse dodajSlike(Long id, List<MultipartFile> fajlovi) {
        Prijava p = nadji(id);
        for (MultipartFile f : fajlovi) {
            String tip = f.getContentType();
            if (tip == null || !tip.startsWith("image/")) {
                throw new IllegalArgumentException("Dozvoljene su samo slike: " + f.getOriginalFilename());
            }
            try {
                p.dodajSliku(new Slika(f.getOriginalFilename(), tip, f.getBytes()));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        prijave.flush();
        return PrijavaResponse.from(p);
    }

    @Transactional(readOnly = true)
    public Slika slika(Long prijavaId, Long slikaId) {
        return slike.findByIdAndPrijavaId(slikaId, prijavaId)
                .orElseThrow(() -> new NotFoundException("Slika " + slikaId + " ne postoji"));
    }

    private Prijava nadji(Long id) {
        return prijave.findById(id)
                .orElseThrow(() -> new NotFoundException("Prijava " + id + " ne postoji"));
    }

    private static String prazno(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private static int ili(Integer v, int def) {
        return v == null ? def : v;
    }
}
