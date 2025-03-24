package co.edu.itp.svu.service;

import co.edu.itp.svu.domain.ArchivoAdjunto;
import co.edu.itp.svu.domain.Oficina;
import co.edu.itp.svu.domain.Pqrs;
import co.edu.itp.svu.repository.ArchivoAdjuntoRepository;
import co.edu.itp.svu.repository.OficinaRepository;
import co.edu.itp.svu.repository.PqrsRepository;
import co.edu.itp.svu.service.dto.ArchivoAdjuntoDTO;
import co.edu.itp.svu.service.dto.OficinaDTO;
import co.edu.itp.svu.service.dto.PqrsDTO;
import co.edu.itp.svu.service.mapper.ArchivoAdjuntoMapper;
import co.edu.itp.svu.service.mapper.OficinaMapper;
import co.edu.itp.svu.service.mapper.PqrsMapper;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link co.edu.itp.svu.domain.Pqrs}.
 */
@Service
public class PqrsService {

    private static final Logger LOG = LoggerFactory.getLogger(PqrsService.class);

    private final PqrsMapper pqrsMapper;

    private final ArchivoAdjuntoMapper archivoAdjuntoMapper;

    private final PqrsRepository pqrsRepository;

    private final OficinaRepository oficinaRepository;

    private final ArchivoAdjuntoRepository archivoAdjuntoRepository;
    private OficinaMapper oficinaMapper;

    private final MongoTemplate mongoTemplate; // Para operaciones más avanzadas

    public PqrsService(
        PqrsRepository pqrsRepository,
        PqrsMapper pqrsMapper,
        ArchivoAdjuntoMapper archivoAdjuntoMapper,
        OficinaRepository oficinaRepository,
        ArchivoAdjuntoRepository archivoAdjuntoRepository,
        OficinaMapper oficinaMapper,
        MongoTemplate mongoTemplate
    ) {
        this.pqrsRepository = pqrsRepository;
        this.pqrsMapper = pqrsMapper;
        this.archivoAdjuntoMapper = archivoAdjuntoMapper;
        this.oficinaRepository = oficinaRepository;
        this.archivoAdjuntoRepository = archivoAdjuntoRepository;
        this.oficinaMapper = oficinaMapper;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Partially update a pqrs.
     *
     * @param pqrsDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PqrsDTO> partialUpdate(PqrsDTO pqrsDTO) {
        LOG.debug("Request to partially update Pqrs : {}", pqrsDTO);

        return pqrsRepository
            .findById(pqrsDTO.getId())
            .map(existingPqrs -> {
                pqrsMapper.partialUpdate(existingPqrs, pqrsDTO);

                return existingPqrs;
            })
            .map(pqrsRepository::save)
            .map(pqrsMapper::toDto);
    }

    /**
     * Get all the pqrs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<PqrsDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Pqrs");
        return pqrsRepository.findAll(pageable).map(pqrsMapper::toDto);
    }

    /**
     * Get one pqrs by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<PqrsDTO> findOne(String id) {
        LOG.debug("Request to get Pqrs : {}", id);
        return pqrsRepository.findById(id).map(pqrsMapper::toDto);
    }

    /**
     * Delete the pqrs by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete Pqrs : {}", id);
        pqrsRepository.deleteById(id);
    }

    ////////Cambios aqui //////////////////////////77

    /**
     * Get all the pqrs with oficina.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<PqrsDTO> findAllOficina(Pageable pageable) {
        LOG.debug("Request to get all Pqrs");

        Page<Pqrs> pqrsPage = pqrsRepository.findAll(pageable);

        return pqrsPage.map(pqrs -> {
            PqrsDTO dto = pqrsMapper.toDto(pqrs);

            // Obtener la oficina
            Optional<Oficina> oficinaOpt = this.oficinaRepository.findById(dto.getOficinaResponder().getId());

            // Si la oficina existe, mapearla a OficinaDTO y establecerla en el DTO de Pqrs
            oficinaOpt.ifPresent(oficina -> {
                OficinaDTO oficinaDTO = oficinaMapper.toDto(oficina); //mapper para Oficina
                dto.setOficinaResponder(oficinaDTO); // Establecer la oficinaDTO en el DTO
            });

            return dto;
        });
    }

    public Optional<PqrsDTO> findOneOficina(String id) {
        LOG.debug("Request to get Pqrs : {}", id);
        return pqrsRepository
            .findById(id)
            .map(pqrs -> {
                PqrsDTO dto = pqrsMapper.toDto(pqrs);

                // Obtener la oficina
                Optional<Oficina> oficinaOpt = this.oficinaRepository.findById(dto.getOficinaResponder().getId());

                // Si la oficina existe, mapearla a OficinaDTO y establecerla en el DTO de Pqrs
                oficinaOpt.ifPresent(oficina -> {
                    OficinaDTO oficinaDTO = oficinaMapper.toDto(oficina); // Mapper para Oficina
                    dto.setOficinaResponder(oficinaDTO); // Establecer la oficinaDTO en el DTO
                });

                return dto;
            });
    }

    public PqrsDTO create(PqrsDTO pqrsDTO) {
        Pqrs pqrs = pqrsMapper.toEntity(pqrsDTO);
        // Asociar archivos adjuntos a la PQRS usando sus IDs
        if (pqrsDTO.getArchivosAdjuntosDTO() != null && !pqrsDTO.getArchivosAdjuntosDTO().isEmpty()) {
            Set<String> archivosAdjuntosIds = pqrsDTO
                .getArchivosAdjuntosDTO()
                .stream()
                .map(ArchivoAdjuntoDTO::getId)
                .collect(Collectors.toSet());

            // Recuperar los archivos adjuntos desde la base de datos
            Set<ArchivoAdjunto> archivosAdjuntos = new HashSet<>(archivoAdjuntoRepository.findAllById(archivosAdjuntosIds));

            // Verificar que se encontraron todos los archivos adjuntos
            if (archivosAdjuntos.size() != archivosAdjuntosIds.size()) {
                throw new RuntimeException("Algunos archivos adjuntos no fueron encontrados.");
            }

            // Asociar los archivos adjuntos a la PQRS
            pqrs.setArchivosAdjuntos(archivosAdjuntos);
        }
        pqrs = pqrsRepository.save(pqrs);
        return pqrsMapper.toDto(pqrs);
    }

    public PqrsDTO update(PqrsDTO pqrsDTO) {
        Pqrs pqrs = pqrsMapper.toEntity(pqrsDTO);

        // Asociar archivos adjuntos a la PQRS usando sus IDs
        if (pqrsDTO.getArchivosAdjuntosDTO() != null) {
            Set<String> archivosAdjuntosIds = pqrsDTO
                .getArchivosAdjuntosDTO()
                .stream()
                .map(ArchivoAdjuntoDTO::getId)
                .collect(Collectors.toSet());

            Set<ArchivoAdjunto> archivosAdjuntos = new HashSet<>(archivoAdjuntoRepository.findAllById(archivosAdjuntosIds));
            pqrs.setArchivosAdjuntos(archivosAdjuntos);
        }

        pqrs = pqrsRepository.save(pqrs);
        return pqrsMapper.toDto(pqrs);
    }
}
