package co.edu.itp.svu.repository;

import co.edu.itp.svu.domain.Pqrs;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Pqrs entity.
 */
@Repository
public interface PqrsRepository extends MongoRepository<Pqrs, String> {
    List<Pqrs> findByOficinaResponder_Id(String oficinaId);
    List<Pqrs> findByEstadoAndFechaCreacionLessThanEqual(String state, LocalDate date, Pageable pageable);
    Page<Pqrs> findAllByEstadoNotAndFechaCreacionLessThanEqual(String state, LocalDate date, Pageable pageable);
    Page<Pqrs> findAllByEstadoAndFechaCreacionLessThanEqual(String state, LocalDate date, Pageable pageable);
    Page<Pqrs> findByEstadoNotAndOficinaResponder_IdAndFechaCreacionLessThanEqual(
        String state,
        String officeId,
        LocalDate date,
        Pageable pageable
    );
    Page<Pqrs> findByEstadoAndOficinaResponder_IdAndFechaCreacionLessThanEqual(
        String state,
        String officeId,
        LocalDate date,
        Pageable pageable
    );
}
