package co.edu.itp.svu.web.rest;

import co.edu.itp.svu.domain.ArchivoAdjunto;
import co.edu.itp.svu.repository.ArchivoAdjuntoRepository;
import co.edu.itp.svu.service.ArchivoAdjuntoService;
import co.edu.itp.svu.service.dto.ArchivoAdjuntoDTO;
import co.edu.itp.svu.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link co.edu.itp.svu.domain.ArchivoAdjunto}.
 */
@RestController
@RequestMapping("/api/archivo-adjuntos")
public class ArchivoAdjuntoResource {

    private static final Logger LOG = LoggerFactory.getLogger(ArchivoAdjuntoResource.class);

    private static final String ENTITY_NAME = "archivoAdjunto";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ArchivoAdjuntoService archivoAdjuntoService;

    private final ArchivoAdjuntoRepository archivoAdjuntoRepository;

    public ArchivoAdjuntoResource(ArchivoAdjuntoService archivoAdjuntoService, ArchivoAdjuntoRepository archivoAdjuntoRepository) {
        this.archivoAdjuntoService = archivoAdjuntoService;
        this.archivoAdjuntoRepository = archivoAdjuntoRepository;
    }

    /**
     * {@code POST  /archivo-adjuntos} : Create a new archivoAdjunto.
     *
     * @param archivoAdjuntoDTO the archivoAdjuntoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new archivoAdjuntoDTO, or with status {@code 400 (Bad Request)} if the archivoAdjunto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ArchivoAdjuntoDTO> createArchivoAdjunto(@Valid @RequestBody ArchivoAdjuntoDTO archivoAdjuntoDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ArchivoAdjunto : {}", archivoAdjuntoDTO);
        if (archivoAdjuntoDTO.getId() != null) {
            throw new BadRequestAlertException("A new archivoAdjunto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        archivoAdjuntoDTO = archivoAdjuntoService.save(archivoAdjuntoDTO);
        return ResponseEntity.created(new URI("/api/archivo-adjuntos/" + archivoAdjuntoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, archivoAdjuntoDTO.getId()))
            .body(archivoAdjuntoDTO);
    }

    /**
     * {@code PUT  /archivo-adjuntos/:id} : Updates an existing archivoAdjunto.
     *
     * @param id the id of the archivoAdjuntoDTO to save.
     * @param archivoAdjuntoDTO the archivoAdjuntoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated archivoAdjuntoDTO,
     * or with status {@code 400 (Bad Request)} if the archivoAdjuntoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the archivoAdjuntoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ArchivoAdjuntoDTO> updateArchivoAdjunto(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody ArchivoAdjuntoDTO archivoAdjuntoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ArchivoAdjunto : {}, {}", id, archivoAdjuntoDTO);
        if (archivoAdjuntoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, archivoAdjuntoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!archivoAdjuntoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        archivoAdjuntoDTO = archivoAdjuntoService.update(archivoAdjuntoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, archivoAdjuntoDTO.getId()))
            .body(archivoAdjuntoDTO);
    }

    /**
     * {@code PATCH  /archivo-adjuntos/:id} : Partial updates given fields of an existing archivoAdjunto, field will ignore if it is null
     *
     * @param id the id of the archivoAdjuntoDTO to save.
     * @param archivoAdjuntoDTO the archivoAdjuntoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated archivoAdjuntoDTO,
     * or with status {@code 400 (Bad Request)} if the archivoAdjuntoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the archivoAdjuntoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the archivoAdjuntoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ArchivoAdjuntoDTO> partialUpdateArchivoAdjunto(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody ArchivoAdjuntoDTO archivoAdjuntoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ArchivoAdjunto partially : {}, {}", id, archivoAdjuntoDTO);
        if (archivoAdjuntoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, archivoAdjuntoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!archivoAdjuntoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ArchivoAdjuntoDTO> result = archivoAdjuntoService.partialUpdate(archivoAdjuntoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, archivoAdjuntoDTO.getId())
        );
    }

    /**
     * {@code GET  /archivo-adjuntos} : get all the archivoAdjuntos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of archivoAdjuntos in body.
     */
    @GetMapping("")
    public List<ArchivoAdjuntoDTO> getAllArchivoAdjuntos() {
        LOG.debug("REST request to get all ArchivoAdjuntos");
        return archivoAdjuntoService.findAll();
    }

    /**
     * {@code GET  /archivo-adjuntos/:id} : get the "id" archivoAdjunto.
     *
     * @param id the id of the archivoAdjuntoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the archivoAdjuntoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArchivoAdjuntoDTO> getArchivoAdjunto(@PathVariable("id") String id) {
        LOG.debug("REST request to get ArchivoAdjunto : {}", id);
        Optional<ArchivoAdjuntoDTO> archivoAdjuntoDTO = archivoAdjuntoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(archivoAdjuntoDTO);
    }

    /**
     * {@code DELETE  /archivo-adjuntos/:id} : delete the "id" archivoAdjunto.
     *
     * @param id the id of the archivoAdjuntoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArchivoAdjunto(@PathVariable("id") String id) {
        LOG.debug("REST request to delete ArchivoAdjunto : {}", id);
        archivoAdjuntoService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    ///////////////////////////////////////////////////////////////////////77
    @PostMapping("/subir")
    public ResponseEntity<ArchivoAdjunto> subirArchivo(@RequestParam("file") MultipartFile file) {
        try {
            ArchivoAdjunto archivoAdjunto = archivoAdjuntoService.save(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(archivoAdjunto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    ///////////////////////////////////////////////////////////////////////7
    // Endpoint para descargar archivo
    @GetMapping("/api/archivos/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        // Llama al servicio para obtener el archivo
        File file = archivoAdjuntoService.downloadFile(fileName);

        // Crea un Resource a partir del archivo
        Path path = file.toPath();
        Resource resource = new UrlResource(path.toUri());

        // Configura los encabezados para indicar que es una descarga
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        try {
            archivoAdjuntoService.deleteFile(fileName);
            return ResponseEntity.ok("Archivo eliminado: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar archivo: " + e.getMessage());
        }
    }

    /*  @PostMapping("/upload")
    public List<String> handleFileUpload(@RequestParam("files") List<MultipartFile> files) {
        return files
            .stream()
            .map(archivoAdjuntoService::saveFile) // Guardar cada archivo y obtener su ID
            .collect(Collectors.toList());
    }*/
    @PostMapping("/upload")
    public List<ArchivoAdjuntoDTO> handleFileUpload(@RequestParam("files") List<MultipartFile> files) {
        return files
            .stream()
            .map(archivoAdjuntoService::saveFile) // Ahora saveFile retorna DTO
            .collect(Collectors.toList());
    }
}
