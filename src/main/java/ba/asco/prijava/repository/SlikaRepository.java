package ba.asco.prijava.repository;

import ba.asco.prijava.model.Slika;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SlikaRepository extends JpaRepository<Slika, Long> {

    Optional<Slika> findByIdAndPrijavaId(Long id, Long prijavaId);
}
