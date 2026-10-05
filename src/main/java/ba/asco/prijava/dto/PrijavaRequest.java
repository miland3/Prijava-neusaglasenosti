package ba.asco.prijava.dto;

import ba.asco.prijava.model.NacinUocavanja;
import ba.asco.prijava.model.VrstaGreske;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Ono što frontend šalje na POST /api/prijave. */
public record PrijavaRequest(
        @NotBlank(message = "Proizvod je obavezan") String proizvod,
        @NotNull(message = "Vrsta greške je obavezna") VrstaGreske vrstaGreske,
        String dobavljac,
        @Min(0) Integer kolicinaSerija,
        @Min(0) Integer kolicinaProvjereno,
        @Min(0) Integer kolicinaNeispravno,
        NacinUocavanja uoceno,
        String izmjereneVrijednosti,
        @NotBlank(message = "Opis je obavezan") @Size(max = 4000) String opis,
        List<String> uzroci,
        boolean ponavljajuca,
        @Size(max = 4000) String uradjeno,
        String prijavio
) {
}
