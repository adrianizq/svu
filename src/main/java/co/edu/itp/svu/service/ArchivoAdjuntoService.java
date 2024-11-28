package co.edu.itp.svu.service;

import co.edu.itp.svu.config.ApplicationProperties;
import co.edu.itp.svu.domain.ArchivoAdjunto;
import co.edu.itp.svu.repository.ArchivoAdjuntoRepository;
import co.edu.itp.svu.service.dto.ArchivoAdjuntoDTO;
import co.edu.itp.svu.service.mapper.ArchivoAdjuntoMapper;
import co.edu.itp.svu.service.util.FileUtils;
import co.edu.itp.svu.service.util.MimeTypes;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing {@link co.edu.itp.svu.domain.ArchivoAdjunto}.
 */
@Service
public class ArchivoAdjuntoService {

    private static final Logger LOG = LoggerFactory.getLogger(ArchivoAdjuntoService.class);

    private final ArchivoAdjuntoRepository archivoAdjuntoRepository;

    private final ArchivoAdjuntoMapper archivoAdjuntoMapper;

    private final Logger log = LoggerFactory.getLogger(ArchivoAdjuntoService.class);

    private final ApplicationProperties appProperties;

    public ArchivoAdjuntoService(
        ArchivoAdjuntoRepository archivoAdjuntoRepository,
        ArchivoAdjuntoMapper archivoAdjuntoMapper,
        ApplicationProperties appProperties
    ) {
        this.archivoAdjuntoRepository = archivoAdjuntoRepository;
        this.archivoAdjuntoMapper = archivoAdjuntoMapper;
        this.appProperties = appProperties;
    }

    /**
     * Save a archivoAdjunto.
     *
     * @param archivoAdjuntoDTO the entity to save.
     * @return the persisted entity.
     */
    public ArchivoAdjuntoDTO save(ArchivoAdjuntoDTO archivoAdjuntoDTO) {
        LOG.debug("Request to save ArchivoAdjunto : {}", archivoAdjuntoDTO);
        ArchivoAdjunto archivoAdjunto = archivoAdjuntoMapper.toEntity(archivoAdjuntoDTO);
        archivoAdjunto = archivoAdjuntoRepository.save(archivoAdjunto);
        return archivoAdjuntoMapper.toDto(archivoAdjunto);
    }

    /**
     * Update a archivoAdjunto.
     *
     * @param archivoAdjuntoDTO the entity to save.
     * @return the persisted entity.
     */
    public ArchivoAdjuntoDTO update(ArchivoAdjuntoDTO archivoAdjuntoDTO) {
        LOG.debug("Request to update ArchivoAdjunto : {}", archivoAdjuntoDTO);
        ArchivoAdjunto archivoAdjunto = archivoAdjuntoMapper.toEntity(archivoAdjuntoDTO);
        archivoAdjunto = archivoAdjuntoRepository.save(archivoAdjunto);
        return archivoAdjuntoMapper.toDto(archivoAdjunto);
    }

    /**
     * Partially update a archivoAdjunto.
     *
     * @param archivoAdjuntoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ArchivoAdjuntoDTO> partialUpdate(ArchivoAdjuntoDTO archivoAdjuntoDTO) {
        LOG.debug("Request to partially update ArchivoAdjunto : {}", archivoAdjuntoDTO);

        return archivoAdjuntoRepository
            .findById(archivoAdjuntoDTO.getId())
            .map(existingArchivoAdjunto -> {
                archivoAdjuntoMapper.partialUpdate(existingArchivoAdjunto, archivoAdjuntoDTO);

                return existingArchivoAdjunto;
            })
            .map(archivoAdjuntoRepository::save)
            .map(archivoAdjuntoMapper::toDto);
    }

    /**
     * Get all the archivoAdjuntos.
     *
     * @return the list of entities.
     */
    public List<ArchivoAdjuntoDTO> findAll() {
        LOG.debug("Request to get all ArchivoAdjuntos");
        return archivoAdjuntoRepository
            .findAll()
            .stream()
            .map(archivoAdjuntoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one archivoAdjunto by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<ArchivoAdjuntoDTO> findOne(String id) {
        LOG.debug("Request to get ArchivoAdjunto : {}", id);
        return archivoAdjuntoRepository.findById(id).map(archivoAdjuntoMapper::toDto);
    }

    /**
     * Delete the archivoAdjunto by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete ArchivoAdjunto : {}", id);
        archivoAdjuntoRepository.deleteById(id);
    }

    public ArchivoAdjunto save(MultipartFile file, String nombrePqrs) throws IOException {
        Path rootLocation = Path.of("/home/adrian/Adr/svufiles");
        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation); // Crear el directorio si no existe
        }

        String fileName = file.getOriginalFilename();
        Path destinationPath = rootLocation.resolve(fileName);

        // Guardar el archivo
        file.transferTo(destinationPath);

        // Crear la URL o ruta para almacenar en la base de datos
        String urlArchivo = "uploads/" + fileName;

        // Crear la instancia de ArchivoAdjunto
        ArchivoAdjunto archivoAdjunto = new ArchivoAdjunto()
            .nombre(fileName)
            .tipo(file.getContentType())
            .urlArchivo(urlArchivo)
            .fechaSubida(Instant.now());

        // Aquí deberías asociar el archivo a un Pqrs o Respuesta si es necesario
        // archivoAdjunto.setPqrs(pqrs);  // Ejemplo de asignación

        // Guardar en la base de datos
        return archivoAdjuntoRepository.save(archivoAdjunto);
    }
    /*
    public void attachFile(ArchivoAdjuntoDTO dto, byte[] file, String contentType) {
        ArchivoAdjunto entity = archivoAdjuntoMapper.toEntity(dto);
        //Guarda el archivo en el directorio asignado
        //File rootDir = new File("/home/jltovarg/test/dudo");
        try {
            Path rootDir = Paths.get(appProperties.getUpload().getRoot().getDir());
            Path filesDir = rootDir.resolve(appProperties.getUpload().getAdjunto().getDir());
            log.debug("Dir upload: {}, , filesDir: {}, mimetype: {}", rootDir, filesDir, contentType);
            String ext = MimeTypes.getDefaultExt(contentType);
            String nameFile = FileUtils.buildFileName(entity.getId(), ext);

            //FileUtils.writeByteArrayToFile(announcementDir.resolve(nameFile), file );
            Files.write(filesDir.resolve(nameFile), file, StandardOpenOption.CREATE);

            entity.setArchivo(nameFile);
            archivoAdjuntoRepository.save(entity);
        } catch (Exception e) {
            log.error("Error attah file", e);
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

 */
    /**
     * Carga un archivo del directorio base
     * @param dto
     * @return
     * @throws Exception
     */
    /*
    public Resource loadFileAsResource(ArchivoAdjuntoDTO dto) throws Exception {

        try {

            Path rootDir = Paths.get(appProperties.getUpload().getRoot().getDir());
            Path filesDir = rootDir.resolve(appProperties.getUpload().getAdjunto().getDir());
            log.debug("Dir upload: {}, , filesDir: {}, file: {}", rootDir, filesDir, dto.getFile());

            Path filePath = filesDir.resolve(dto.getFile()).normalize();

            Resource resource = new UrlResource(filePath.toUri());

            if(resource.exists()) {

                return resource;

            } else {

                throw new FileNotFoundException("File not found " + dto.getFile());

            }

        } catch (MalformedURLException ex) {

            throw new FileNotFoundException("File not found " + dto.getFile());

        }

    }

 */

}
