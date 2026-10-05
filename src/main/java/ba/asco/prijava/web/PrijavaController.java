package ba.asco.prijava.web;

import ba.asco.prijava.dto.PrijavaRequest;
import ba.asco.prijava.dto.PrijavaResponse;
import ba.asco.prijava.dto.StatusRequest;
import ba.asco.prijava.model.Slika;
import ba.asco.prijava.model.Status;
import ba.asco.prijava.service.PrijavaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

/**
 * REST API za prijave.
 *
 * GET    /api/prijave                  lista (opciono ?status=NOVA)
 * GET    /api/prijave/{id}             jedna prijava
 * POST   /api/prijave                  nova prijava (JSON)
 * PATCH  /api/prijave/{id}/status      promjena statusa
 * DELETE /api/prijave/{id}             brisanje
 * POST   /api/prijave/{id}/slike       upload slika (multipart, polje "slike")
 * GET    /api/prijave/{id}/slike/{sid} sama slika
 */
@RestController
@RequestMapping("/api/prijave")
public class PrijavaController {

    private final PrijavaService service;

    public PrijavaController(PrijavaService service) {
        this.service = service;
    }

    @GetMapping
    public List<PrijavaResponse> sve(@RequestParam(required = false) Status status) {
        return service.sve(status);
    }

    @GetMapping("/{id}")
    public PrijavaResponse jedna(@PathVariable Long id) {
        return service.jedna(id);
    }

    @PostMapping
    public ResponseEntity<PrijavaResponse> kreiraj(@Valid @RequestBody PrijavaRequest request) {
        PrijavaResponse nova = service.kreiraj(request);
        return ResponseEntity.created(URI.create("/api/prijave/" + nova.id())).body(nova);
    }

    @PatchMapping("/{id}/status")
    public PrijavaResponse status(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return service.promijeniStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void obrisi(@PathVariable Long id) {
        service.obrisi(id);
    }

    @PostMapping(path = "/{id}/slike", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PrijavaResponse dodajSlike(@PathVariable Long id, @RequestParam("slike") List<MultipartFile> slike) {
        return service.dodajSlike(id, slike);
    }

    @GetMapping("/{id}/slike/{slikaId}")
    public ResponseEntity<byte[]> slika(@PathVariable Long id, @PathVariable Long slikaId) {
        Slika s = service.slika(id, slikaId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(s.getContentType()))
                .body(s.getPodaci());
    }
}
