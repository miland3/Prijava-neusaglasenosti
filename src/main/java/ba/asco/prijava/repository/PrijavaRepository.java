package ba.asco.prijava.repository;

import ba.asco.prijava.model.Prijava;
import ba.asco.prijava.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrijavaRepository extends JpaRepository<Prijava, Long> {

    List<Prijava> findAllByOrderByKreiranoDesc();

    List<Prijava> findByStatusOrderByKreiranoDesc(Status status);
}
