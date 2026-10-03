package ba.asco.prava.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDate;

/**
 * Jedan red podataka. Radi jednostavnosti sve tri vrste podataka su u jednoj
 * tabeli, a razlikuju se po koloni "tip". U pravoj aplikaciji bi Prijava,
 * Narudžba i Ponuda bile posebne klase sa svojim poljima.
 */
@Entity
public class Zapis {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipPodatka tip;

    private String opis;

    private String autor;

    private LocalDate datum;

    protected Zapis() {
        // JPA traži prazan konstruktor
    }

    public Zapis(TipPodatka tip, String opis, String autor) {
        this.tip = tip;
        this.opis = opis;
        this.autor = autor;
        this.datum = LocalDate.now();
    }

    public Long getId() { return id; }
    public TipPodatka getTip() { return tip; }
    public String getOpis() { return opis; }
    public String getAutor() { return autor; }
    public LocalDate getDatum() { return datum; }
}
