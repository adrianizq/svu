package co.edu.itp.svu.repository;

import co.edu.itp.svu.domain.ArchivoAdjunto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ArchivoAdjunto entity.
 */
@Repository
public interface ArchivoAdjuntoRepository extends MongoRepository<ArchivoAdjunto, String> {
    public void deleteByUrlArchivo(String fileURL);
}
