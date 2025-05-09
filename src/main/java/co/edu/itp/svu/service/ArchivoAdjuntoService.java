package co.edu.itp.svu.service;

import co.edu.itp.svu.config.ApplicationProperties;
import co.edu.itp.svu.domain.ArchivoAdjunto;
import co.edu.itp.svu.repository.ArchivoAdjuntoRepository;
import co.edu.itp.svu.service.dto.ArchivoAdjuntoDTO;
import co.edu.itp.svu.service.mapper.ArchivoAdjuntoMapper;
import co.edu.itp.svu.service.util.FileUtils;
import co.edu.itp.svu.service.util.MimeTypes;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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

    // Ruta base donde se guardarán los archivos (podría venir de application.properties)
    @Value("${app.file.upload-dir:/home/adrian/Adr/svufiles/}")
    private String uploadDir;

    public ArchivoAdjuntoService(
        ArchivoAdjuntoRepository archivoAdjuntoRepository,
        ArchivoAdjuntoMapper archivoAdjuntoMapper,
        ApplicationProperties appProperties
    ) { // Cambiado a FileStore
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

    public ArchivoAdjunto save(MultipartFile file) throws IOException {
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

    // Método para descargar el archivo

    public File downloadFile(String fileName) throws IOException {
        // Define la ruta base donde se almacenan los archivos
        Path rootLocation = Path.of("/home/adrian/Adr/svufiles");

        // Resuelve la ruta del archivo completo basado en el nombre del archivo
        Path filePath = rootLocation.resolve(fileName);

        // Verifica si el archivo existe y es un archivo regular
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            return filePath.toFile(); // Devuelve el archivo como un objeto File
        } else {
            throw new IOException("El archivo no existe o no es un archivo válido.");
        }
    }

    public void deleteFile(String fileName) throws IOException {
        // Define la ruta base donde se almacenan los archivos
        Path rootLocation = Path.of("/home/adrian/Adr/svufiles");

        // Resuelve la ruta completa del archivo basado en el nombre del archivo
        Path filePath = rootLocation.resolve(fileName);

        // Verifica si el archivo existe
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            Files.delete(filePath); // Borra el archivo
            System.out.println("Archivo eliminado: " + filePath);
        } else {
            throw new IOException("El archivo no existe o no es un archivo válido.");
        }
    }

    public ArchivoAdjuntoDTO saveFile(MultipartFile file, String pqrs_id) {
        // 1. Guardar archivo físicamente (tu código existente)
        Path rootLocation = Path.of("/home/adrian/Adr/svufiles");

        // Generar nombre único
        String nombreOriginal = Objects.requireNonNull(file.getOriginalFilename());
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf('.'));
        String nombreUnico = nombreOriginal + "_" + UUID.randomUUID() + extension;
        Path rutaArchivo = rootLocation.resolve(nombreUnico);
        if (!Files.exists(rootLocation)) {
            try {
                Files.createDirectories(rootLocation);
            } catch (IOException e) {
                throw new RuntimeException("No se pudo crear el directorio", e);
            }
        }
        // 2. Crear y guardar la entidad
        // String fileName = file.getOriginalFilename();
        // Path destinationPath = rootLocation.resolve(fileName);
        try {
            //file.transferTo(destinationPath);
            file.transferTo(rutaArchivo);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo", e);
        }
        // 3. Crear entidad con todos los campos
        ArchivoAdjunto archivoAdjunto = new ArchivoAdjunto()
            .nombre(nombreOriginal)
            .tipo(file.getContentType())
            //.urlArchivo("uploads/" + pqrs_id + "_" + nombreOriginal)
            .urlArchivo(nombreUnico)
            .fechaSubida(Instant.now());
        // 4. Guardar en MongoDB y retornar el objeto completo
        ArchivoAdjuntoDTO archivoAdjuntoDTO = convertToDto(archivoAdjunto);
        archivoAdjunto = archivoAdjuntoRepository.save(archivoAdjunto);
        archivoAdjuntoDTO = archivoAdjuntoMapper.toDto(archivoAdjunto);
        return archivoAdjuntoDTO;
    }

    private ArchivoAdjuntoDTO convertToDto(ArchivoAdjunto archivo) {
        ArchivoAdjuntoDTO dto = new ArchivoAdjuntoDTO();
        dto.setId(archivo.getId());
        dto.setNombre(archivo.getNombre());
        dto.setTipo(archivo.getTipo());
        dto.setUrlArchivo(archivo.getUrlArchivo());
        dto.setFechaSubida(archivo.getFechaSubida());
        return dto;
    }
}
