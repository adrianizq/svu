package co.edu.itp.svu.repository;

import co.edu.itp.svu.domain.Oficina;
import co.edu.itp.svu.domain.Pqrs;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Oficina entity.
 */
@Repository
public interface OficinaRepository extends MongoRepository<Oficina, String> {
    Oficina findByResponsable_Id(String userId); // Método para encontrar Oficina por usuario
    Oficina findByResponsable_Login(String login); // Método para encontrar Oficina por usuario
    Oficina findByNombre(String name);
}
