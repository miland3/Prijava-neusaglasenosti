package ba.asco.prijava.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Prijava {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String proizvod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VrstaGreske vrstaGreske;

    private String dobavljac;

    private int kolicinaSerija = 1;
    private int kolicinaProvjereno = 1;
    private int kolicinaNeispravno = 1;

    @Enumerated(EnumType.STRING)
    private NacinUocavanja uoceno = NacinUocavanja.VIZUELNO;

    private String izmjereneVrijednosti;

    @Column(nullable = false, length = 4000)
    private String opis;

    @ElementCollection
    @CollectionTable(name = "prijava_uzrok", joinColumns = @JoinColumn(name = "prijava_id"))
    @Column(name = "uzrok")
    private List<String> uzroci = new ArrayList<>();

    private boolean ponavljajuca;

    @Column(length = 4000)
    private String uradjeno;

    private String prijavio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.NOVA;

    @Column(nullable = false, updatable = false)
    private Instant kreirano = Instant.now();

    @OneToMany(mappedBy = "prijava", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<Slika> slike = new ArrayList<>();

    public void dodajSliku(Slika slika) {
        slika.setPrijava(this);
        slike.add(slika);
    }

    public Long getId() { return id; }
    public String getProizvod() { return proizvod; }
    public void setProizvod(String proizvod) { this.proizvod = proizvod; }
    public VrstaGreske getVrstaGreske() { return vrstaGreske; }
    public void setVrstaGreske(VrstaGreske vrstaGreske) { this.vrstaGreske = vrstaGreske; }
    public String getDobavljac() { return dobavljac; }
    public void setDobavljac(String dobavljac) { this.dobavljac = dobavljac; }
    public int getKolicinaSerija() { return kolicinaSerija; }
    public void setKolicinaSerija(int kolicinaSerija) { this.kolicinaSerija = kolicinaSerija; }
    public int getKolicinaProvjereno() { return kolicinaProvjereno; }
    public void setKolicinaProvjereno(int kolicinaProvjereno) { this.kolicinaProvjereno = kolicinaProvjereno; }
    public int getKolicinaNeispravno() { return kolicinaNeispravno; }
    public void setKolicinaNeispravno(int kolicinaNeispravno) { this.kolicinaNeispravno = kolicinaNeispravno; }
    public NacinUocavanja getUoceno() { return uoceno; }
    public void setUoceno(NacinUocavanja uoceno) { this.uoceno = uoceno; }
    public String getIzmjereneVrijednosti() { return izmjereneVrijednosti; }
    public void setIzmjereneVrijednosti(String izmjereneVrijednosti) { this.izmjereneVrijednosti = izmjereneVrijednosti; }
    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }
    public List<String> getUzroci() { return uzroci; }
    public void setUzroci(List<String> uzroci) { this.uzroci = uzroci; }
    public boolean isPonavljajuca() { return ponavljajuca; }
    public void setPonavljajuca(boolean ponavljajuca) { this.ponavljajuca = ponavljajuca; }
    public String getUradjeno() { return uradjeno; }
    public void setUradjeno(String uradjeno) { this.uradjeno = uradjeno; }
    public String getPrijavio() { return prijavio; }
    public void setPrijavio(String prijavio) { this.prijavio = prijavio; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Instant getKreirano() { return kreirano; }
    public List<Slika> getSlike() { return slike; }
}
