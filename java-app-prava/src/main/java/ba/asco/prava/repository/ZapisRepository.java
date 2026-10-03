package ba.asco.prava.repository;

import ba.asco.prava.model.TipPodatka;
import ba.asco.prava.model.Zapis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZapisRepository extends JpaRepository<Zapis, Long> {

    // Spring iz imena metode sam napravi SQL: SELECT ... WHERE tip = ?
    List<Zapis> findByTipOrderByIdAsc(TipPodatka tip);
}
