package co.edu.itp.svu.web.rest;

import co.edu.itp.svu.domain.ArchivoAdjunto;
import co.edu.itp.svu.repository.PqrsRepository;
import co.edu.itp.svu.service.ArchivoAdjuntoService;
import co.edu.itp.svu.service.PqrsService;
import co.edu.itp.svu.service.dto.ArchivoAdjuntoDTO;
import co.edu.itp.svu.service.dto.PqrsDTO;
import co.edu.itp.svu.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link co.edu.itp.svu.domain.Pqrs}.
 */
@RestController
@RequestMapping("/api/pqrs")
public class PqrsResource {

    private static final Logger LOG = LoggerFactory.getLogger(PqrsResource.class);

    private static final String ENTITY_NAME = "pqrs";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PqrsService pqrsService;

    private final PqrsRepository pqrsRepository;
    private ArchivoAdjuntoService archivosAdjuntoService;

    public PqrsResource(PqrsService pqrsService, PqrsRepository pqrsRepository, ArchivoAdjuntoService archivosAdjuntoService) {
        this.pqrsService = pqrsService;
        this.pqrsRepository = pqrsRepository;
        this.archivosAdjuntoService = archivosAdjuntoService;
    }

    /**
     * {@code POST  /pqrs} : Create a new pqrs.
     *
     * @param pqrsDTO the pqrsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pqrsDTO, or with status {@code 400 (Bad Request)} if the pqrs has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    /*   @PostMapping("")
    public ResponseEntity<PqrsDTO> createPqrs(@Valid @RequestBody PqrsDTO pqrsDTO) throws URISyntaxException {
        LOG.debug("REST request to save Pqrs : {}", pqrsDTO);
        if (pqrsDTO.getId() != null) {
            throw new BadRequestAlertException("A new pqrs cannot already have an ID", ENTITY_NAME, "idexists");
        }
        pqrsDTO = pqrsService.save(pqrsDTO);
        return ResponseEntity.created(new URI("/api/pqrs/" + pqrsDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, pqrsDTO.getId()))
            .body(pqrsDTO);
    }*/

    /**
     * {@code PUT  /pqrs/:id} : Updates an existing pqrs.
     *
     * @param id the id of the pqrsDTO to save.
     * @param pqrsDTO the pqrsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pqrsDTO,
     * or with status {@code 400 (Bad Request)} if the pqrsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pqrsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    /*
    @PutMapping("/{id}")
    public ResponseEntity<PqrsDTO> updatePqrs(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody PqrsDTO pqrsDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Pqrs : {}, {}", id, pqrsDTO);
        if (pqrsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pqrsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pqrsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        pqrsDTO = pqrsService.update(pqrsDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pqrsDTO.getId()))
            .body(pqrsDTO);
    }

     */

    /**
     * {@code PATCH  /pqrs/:id} : Partial updates given fields of an existing pqrs, field will ignore if it is null
     *
     * @param id the id of the pqrsDTO to save.
     * @param pqrsDTO the pqrsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pqrsDTO,
     * or with status {@code 400 (Bad Request)} if the pqrsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pqrsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pqrsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PqrsDTO> partialUpdatePqrs(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody PqrsDTO pqrsDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Pqrs partially : {}, {}", id, pqrsDTO);
        if (pqrsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pqrsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pqrsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PqrsDTO> result = pqrsService.partialUpdate(pqrsDTO);

        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pqrsDTO.getId()));
    }

    /**
     * {@code GET  /pqrs} : get all the pqrs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pqrs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PqrsDTO>> getAllPqrs(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Pqrs");
        //Page<PqrsDTO> page = pqrsService.findAll(pageable);
        Page<PqrsDTO> page = pqrsService.findAllOficina(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pqrs/:id} : get the "id" pqrs.
     *
     * @param id the id of the pqrsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pqrsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PqrsDTO> getPqrs(@PathVariable("id") String id) {
        LOG.debug("REST request to get Pqrs : {}", id);
        //Optional<PqrsDTO> pqrsDTO = pqrsService.findOne(id);
        Optional<PqrsDTO> pqrsDTO = pqrsService.findOneOficina(id);
        return ResponseUtil.wrapOrNotFound(pqrsDTO);
    }

    /**
     * {@code DELETE  /pqrs/:id} : delete the "id" pqrs.
     *
     * @param id the id of the pqrsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePqrs(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Pqrs : {}", id);
        pqrsService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    //Modificaciones desde aqui
    //ya existe createPqrs pero esa la voy a dejar para mas adelante para users anomimos
    /* @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PqrsDTO> registrarPqr(
        @RequestPart("pqrDTO") PqrsDTO pqrDTO,
        @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos
    ) throws URISyntaxException {
        if (pqrDTO.getId() != null) {
            throw new BadRequestAlertException("A new PQRS cannot already have an ID", ENTITY_NAME, "idexists");
        }

        // Procesar los archivos adjuntos si existen
        List<ArchivoAdjuntoDTO> archivosAdjuntos = new ArrayList<>();
        if (archivos != null && !archivos.isEmpty()) {
            archivosAdjuntos = archivos
                .stream()
                .map(file -> {
                    ArchivoAdjuntoDTO adjunto = new ArchivoAdjuntoDTO();
                    adjunto.setNombre(file.getOriginalFilename());
                    adjunto.setTipo(file.getContentType());
                    adjunto.setFechaSubida(Instant.now());

                    try {
                        ArchivoAdjunto archivoAdjunto = archivosAdjuntoService.save(file);
                        adjunto.setUrlArchivo(archivoAdjunto.getUrlArchivo());
                    } catch (IOException e) {
                        throw new RuntimeException("Error al guardar el archivo adjunto", e);
                    }

                    return adjunto;
                })
                .collect(Collectors.toList());
        }

        LOG.debug("Solicitud para crear una PQR por ADMIN: {}", pqrDTO);
        PqrsDTO nuevaPqr = pqrsService.create(pqrDTO, archivosAdjuntos);

        return ResponseEntity.created(new URI("/api/pqrs/" + nuevaPqr.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, nuevaPqr.getId().toString()))
            .body(nuevaPqr);
    }

    */
    // Crear una nueva PQRS
    @PostMapping
    public ResponseEntity<PqrsDTO> createPqrs(@RequestBody PqrsDTO pqrsDTO) throws IOException {
        LOG.debug("REST request to save Pqrs: {}", pqrsDTO);
        PqrsDTO result = pqrsService.create(pqrsDTO);
        return ResponseEntity.ok(result);
    }

    // Actualizar una PQRS existente
    @PutMapping("/{id}")
    public ResponseEntity<PqrsDTO> updatePqrs(@PathVariable String id, @RequestBody PqrsDTO pqrsDTO) {
        LOG.debug("REST request to update Pqrs: {}", pqrsDTO);
        pqrsDTO.setId(id); // Asegurar que el ID de la PQRS sea el correcto
        PqrsDTO result = pqrsService.update(pqrsDTO);
        return ResponseEntity.ok(result);
    }
}
