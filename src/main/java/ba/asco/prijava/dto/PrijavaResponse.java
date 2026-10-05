package ba.asco.prijava.dto;

import ba.asco.prijava.model.NacinUocavanja;
import ba.asco.prijava.model.Prijava;
import ba.asco.prijava.model.Status;
import ba.asco.prijava.model.VrstaGreske;

import java.time.Instant;
import java.util.List;

/** Ono što API vraća frontendu (bez bajtova slika — samo linkovi na njih). */
public record PrijavaResponse(
        Long id,
        String proizvod,
        VrstaGreske vrstaGreske,
        String dobavljac,
        int kolicinaSerija,
        int kolicinaProvjereno,
        int kolicinaNeispravno,
        NacinUocavanja uoceno,
        String izmjereneVrijednosti,
        String opis,
        List<String> uzroci,
        boolean ponavljajuca,
        String uradjeno,
        String prijavio,
        Status status,
        Instant kreirano,
        List<String> slike
) {
    public static PrijavaResponse from(Prijava p) {
        List<String> slike = p.getSlike().stream()
                .map(s -> "/api/prijave/" + p.getId() + "/slike/" + s.getId())
                .toList();
        return new PrijavaResponse(
                p.getId(), p.getProizvod(), p.getVrstaGreske(), p.getDobavljac(),
                p.getKolicinaSerija(), p.getKolicinaProvjereno(), p.getKolicinaNeispravno(),
                p.getUoceno(), p.getIzmjereneVrijednosti(), p.getOpis(), List.copyOf(p.getUzroci()),
                p.isPonavljajuca(), p.getUradjeno(), p.getPrijavio(), p.getStatus(), p.getKreirano(),
                slike);
    }
}
