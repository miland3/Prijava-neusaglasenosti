package ba.asco.prava;

import ba.asco.prava.model.TipPodatka;
import ba.asco.prava.model.Zapis;
import ba.asco.prava.repository.ZapisRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/** Nekoliko primjera podataka da aplikacija ne bude prazna pri pokretanju. */
@Component
public class PocetniPodaci implements CommandLineRunner {

    private final ZapisRepository repo;

    public PocetniPodaci(ZapisRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        repo.saveAll(List.of(
                new Zapis(TipPodatka.PRIJAVE, "Pukotina na zavaru nosača", "sistem"),
                new Zapis(TipPodatka.PRIJAVE, "Pogrešna dimenzija lima, odstupanje 3 mm", "sistem"),
                new Zapis(TipPodatka.NARUDZBE, "Motor 5,5 kW, 2 kom", "sistem"),
                new Zapis(TipPodatka.NARUDZBE, "Ležajevi 6205, 10 kom", "sistem"),
                new Zapis(TipPodatka.PONUDE, "Ponuda za frekventni pretvarač", "sistem"),
                new Zapis(TipPodatka.PONUDE, "Ponuda za transportnu traku 800 mm", "sistem")
        ));
    }
}
