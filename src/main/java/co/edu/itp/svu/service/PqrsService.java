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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    private final ArchivoAdjuntoService archivoAdjuntoService;

    private OficinaMapper oficinaMapper;

    private final MongoTemplate mongoTemplate; // Para operaciones más avanzadas

    // Ruta base donde se guardarán los archivos (podría venir de application.properties)
    // @Value("${app.file.upload-dir:/home/adrian/Adr/svufiles/}")
    private String uploadDir;

    public PqrsService(
        PqrsRepository pqrsRepository,
        PqrsMapper pqrsMapper,
        ArchivoAdjuntoMapper archivoAdjuntoMapper,
        OficinaRepository oficinaRepository,
        ArchivoAdjuntoRepository archivoAdjuntoRepository,
        ArchivoAdjuntoService archivoAdjuntoService,
        OficinaMapper oficinaMapper,
        MongoTemplate mongoTemplate
    ) {
        this.pqrsRepository = pqrsRepository;
        this.pqrsMapper = pqrsMapper;
        this.archivoAdjuntoMapper = archivoAdjuntoMapper;
        this.oficinaRepository = oficinaRepository;
        this.archivoAdjuntoRepository = archivoAdjuntoRepository;
        this.archivoAdjuntoService = archivoAdjuntoService;
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
        // 1. Convertir la PQRS principal
        Pqrs pqrs = pqrsMapper.toEntity(pqrsDTO);
        pqrs.setEstado("Pendiente");

        Instant globalCurrentDate = Instant.now();
        pqrs.setFechaCreacion(globalCurrentDate);
        ZoneId zoneSystem = ZoneId.systemDefault();
        LocalDateTime currentDate = LocalDateTime.ofInstant(globalCurrentDate, zoneSystem);
        LocalDateTime dueDate = currentDate.plusDays(15);
        pqrs.setFechaLimiteRespuesta(dueDate);

        Oficina office = oficinaRepository.findByNombre("Secretaría General");
        pqrs.setOficinaResponder(office);

        // 2. Procesar archivos adjuntos (convertir DTOs a entidades)
        if (pqrsDTO.getArchivosAdjuntosDTO() != null) {
            Set<ArchivoAdjunto> archivosAdjuntos = pqrsDTO
                .getArchivosAdjuntosDTO()
                .stream()
                .map(this::convertToEntity) // Método de conversión personalizado
                .collect(Collectors.toSet());

            pqrs.setArchivosAdjuntos(archivosAdjuntos);
        }

        // 3. Guardar todo (incluye archivos adjuntos)
        pqrs = pqrsRepository.save(pqrs);

        // 4. Retornar DTO con todos los datos
        return pqrsMapper.toDto(pqrs);
    }

    /*
    public PqrsDTO create(PqrsDTO pqrsDTO) {
        // 1. Convertir y guardar PQRS principal
        Pqrs pqrs = pqrsMapper.toEntity(pqrsDTO);
        //pqrs = pqrsRepository.save(pqrs);
    
        // 2. Procesar archivos adjuntos (solo metadatos)
        if (pqrsDTO.getArchivosAdjuntosDTO() != null) {
            Set<ArchivoAdjunto> archivosAdjuntos = pqrsDTO.getArchivosAdjuntosDTO()
                .stream()
                .map(dto -> {
                    // Generar nombre único
                    String nombreOriginal = dto.getNombre();
                    String nombreBase = nombreOriginal.substring(0, nombreOriginal.lastIndexOf('.'));
                    String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf('.'));
                    String nombreUnico = nombreBase + "_" + UUID.randomUUID() + extension;
    
                    // Crear entidad ArchivoAdjunto
                    return new ArchivoAdjunto()
                        .nombre(nombreOriginal)
                        .tipo(dto.getTipo())
                        .urlArchivo("/home/adrian/Adr/svufiles/" + nombreUnico)
                        .fechaSubida(Instant.now());
                        
                })
                .map(archivoAdjuntoRepository::save) // Guardar cada archivo
                .collect(Collectors.toSet());
    
            pqrs.setArchivosAdjuntos(archivosAdjuntos);
            pqrsRepository.save(pqrs);
           
        }
    
        return pqrsMapper.toDto(pqrs);
    }
*/

    /*public PqrsDTO create(PqrsDTO pqrsDTO) throws IOException {
    uploadDir = new String ("/home/adrian/Adr/svufiles/") ;
    // Validar entrada
    if (pqrsDTO == null) {
        throw new IllegalArgumentException("PQRSDTO no puede ser nulo");
    }

    Pqrs pqrs = pqrsMapper.toEntity(pqrsDTO);
    
    // Procesar archivos
    if (pqrsDTO.getArchivosAdjuntosDTO() != null) {
        String directorioBase = uploadDir;
        Path directorioPath = Paths.get(directorioBase);
        
        // Crear directorio si no existe
        if (!Files.exists(directorioPath)) {
            Files.createDirectories(directorioPath);
        }

        Set<ArchivoAdjunto> archivosAdjuntos = new HashSet<>();
        
        for (ArchivoAdjuntoDTO dto : pqrsDTO.getArchivosAdjuntosDTO()) {
            // Validar archivo
            if (dto.getContenidoArchivo() == null || dto.getContenidoArchivo().length == 0) {
                continue;
            }

            // Generar nombre único
            String nombreOriginal = Objects.requireNonNull(dto.getNombre());
            String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf('.'));
            String nombreUnico = UUID.randomUUID() + extension;
            Path rutaArchivo = directorioPath.resolve(nombreUnico);

            // Guardar archivo
           // Files.write(rutaArchivo, dto.getContenidoArchivo());

            // Crear entidad
            ArchivoAdjunto archivo = new ArchivoAdjunto()
                .nombre(nombreOriginal)
                .tipo(dto.getTipo())
                .urlArchivo(rutaArchivo.toString())
                .fechaSubida(Instant.now());

            archivosAdjuntos.add(archivoAdjuntoRepository.save(archivo));
        }
        
        pqrs.setArchivosAdjuntos(archivosAdjuntos);
    }
    
    pqrs = pqrsRepository.save(pqrs);
    return pqrsMapper.toDto(pqrs);
}
*/

    private ArchivoAdjunto convertToEntity(ArchivoAdjuntoDTO dto) {
        ArchivoAdjunto archivo = new ArchivoAdjunto();
        // Mapear todos los campos (no solo el ID)
        archivo.setId(dto.getId());
        archivo.setNombre(dto.getNombre());
        archivo.setTipo(dto.getTipo());
        archivo.setUrlArchivo(dto.getUrlArchivo());
        archivo.setFechaSubida(dto.getFechaSubida());
        // ... otros campos si existen

        return archivo;
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
