package co.edu.itp.svu.service;

import co.edu.itp.svu.config.ApplicationProperties;
import co.edu.itp.svu.domain.ArchivoAdjunto;
import co.edu.itp.svu.repository.ArchivoAdjuntoRepository;
import co.edu.itp.svu.service.dto.ArchivoAdjuntoDTO;
import co.edu.itp.svu.service.mapper.ArchivoAdjuntoMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final String uploadDir;

    public ArchivoAdjuntoService(
        ArchivoAdjuntoRepository archivoAdjuntoRepository,
        ArchivoAdjuntoMapper archivoAdjuntoMapper,
        ApplicationProperties appProperties
    ) {
        this.archivoAdjuntoRepository = archivoAdjuntoRepository;
        this.archivoAdjuntoMapper = archivoAdjuntoMapper;
        this.uploadDir = appProperties.getFile().getUploadDir();
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

    /**
     * Delete the archivoAdjunto by urlArchivo.
     *
     * @param urlArchivo the urlArchivo of the entity.
     */
    public void deleteByFileURL(String fileURL) {
        LOG.debug("Request to delete ArchivoAdjunto : {}", fileURL);
        archivoAdjuntoRepository.deleteByUrlArchivo(fileURL);
    }

    public File downloadFile(String fileName) throws IOException {
        LOG.debug("Request to download a file");

        Path rootLocation = Path.of(this.uploadDir);
        Path filePath = rootLocation.resolve(fileName);

        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            return filePath.toFile();
        } else {
            throw new IOException("File doesn't exist or isn't a valid file");
        }
    }

    public void deleteFile(String fileName) throws IOException {
        LOG.debug("Request to delete a file");

        Path rootLocation = Path.of(this.uploadDir);
        Path filePath = rootLocation.resolve(fileName);

        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            Files.delete(filePath);
        } else {
            throw new IOException("File doesn't exist or isn't a valid file");
        }
    }

    public ArchivoAdjuntoDTO saveFile(MultipartFile file) {
        LOG.debug("Request to save one or more files");
        Path rootLocation = Path.of(this.uploadDir);

        String originalName = Objects.requireNonNull(file.getOriginalFilename());
        String extension = originalName.substring(originalName.lastIndexOf('.'));
        String uniqueName = originalName + "_" + UUID.randomUUID() + extension;
        Path pathFile = rootLocation.resolve(uniqueName);

        if (!Files.exists(rootLocation)) {
            try {
                Files.createDirectories(rootLocation);
            } catch (IOException e) {
                throw new RuntimeException("It was not possible to create the file", e);
            }
        }

        try {
            file.transferTo(pathFile);
        } catch (IOException e) {
            throw new RuntimeException("Error when saving the file", e);
        }

        ArchivoAdjunto attachedFile = new ArchivoAdjunto()
            .nombre(originalName)
            .tipo(file.getContentType())
            .urlArchivo(uniqueName)
            .fechaSubida(Instant.now());

        ArchivoAdjuntoDTO attachedFileDTO = convertToDTO(attachedFile);
        attachedFile = this.archivoAdjuntoRepository.save(attachedFile);
        attachedFileDTO = this.archivoAdjuntoMapper.toDto(attachedFile);
        return attachedFileDTO;
    }

    private ArchivoAdjuntoDTO convertToDTO(ArchivoAdjunto archivo) {
        ArchivoAdjuntoDTO dto = new ArchivoAdjuntoDTO();
        dto.setId(archivo.getId());
        dto.setNombre(archivo.getNombre());
        dto.setTipo(archivo.getTipo());
        dto.setUrlArchivo(archivo.getUrlArchivo());
        dto.setFechaSubida(archivo.getFechaSubida());
        return dto;
    }
}
