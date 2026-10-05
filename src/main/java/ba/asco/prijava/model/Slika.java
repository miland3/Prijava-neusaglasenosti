package ba.asco.prijava.model;

import jakarta.persistence.*;

@Entity
public class Slika {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Prijava prijava;

    private String naziv;

    private String contentType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] podaci;

    protected Slika() {
    }

    public Slika(String naziv, String contentType, byte[] podaci) {
        this.naziv = naziv;
        this.contentType = contentType;
        this.podaci = podaci;
    }

    public Long getId() { return id; }
    public Prijava getPrijava() { return prijava; }
    void setPrijava(Prijava prijava) { this.prijava = prijava; }
    public String getNaziv() { return naziv; }
    public String getContentType() { return contentType; }
    public byte[] getPodaci() { return podaci; }
}
