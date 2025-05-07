package co.edu.itp.svu.web.rest;

import static co.edu.itp.svu.domain.PqrsAsserts.*;
import static co.edu.itp.svu.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.edu.itp.svu.IntegrationTest;
import co.edu.itp.svu.domain.Pqrs;
import co.edu.itp.svu.repository.PqrsRepository;
import co.edu.itp.svu.service.dto.PqrsDTO;
import co.edu.itp.svu.service.mapper.PqrsMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link PqrsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PqrsResourceIT {

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA_CREACION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_CREACION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final LocalDateTime DEFAULT_FECHA_LIMITE_RESPUESTA = LocalDateTime.now();
    private static final LocalDateTime UPDATED_FECHA_LIMITE_RESPUESTA = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ESTADO = "AAAAAAAAAA";
    private static final String UPDATED_ESTADO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pqrs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PqrsRepository pqrsRepository;

    @Autowired
    private PqrsMapper pqrsMapper;

    @Autowired
    private MockMvc restPqrsMockMvc;

    private Pqrs pqrs;

    private Pqrs insertedPqrs;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pqrs createEntity() {
        return new Pqrs()
            .titulo(DEFAULT_TITULO)
            .descripcion(DEFAULT_DESCRIPCION)
            .fechaCreacion(DEFAULT_FECHA_CREACION)
            .fechaLimiteRespuesta(DEFAULT_FECHA_LIMITE_RESPUESTA)
            .estado(DEFAULT_ESTADO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pqrs createUpdatedEntity() {
        return new Pqrs()
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .fechaCreacion(UPDATED_FECHA_CREACION)
            .fechaLimiteRespuesta(UPDATED_FECHA_LIMITE_RESPUESTA)
            .estado(UPDATED_ESTADO);
    }

    @BeforeEach
    public void initTest() {
        pqrs = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPqrs != null) {
            pqrsRepository.delete(insertedPqrs);
            insertedPqrs = null;
        }
    }

    @Test
    void createPqrs() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Pqrs
        PqrsDTO pqrsDTO = pqrsMapper.toDto(pqrs);
        var returnedPqrsDTO = om.readValue(
            restPqrsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pqrsDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PqrsDTO.class
        );

        // Validate the Pqrs in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPqrs = pqrsMapper.toEntity(returnedPqrsDTO);
        assertPqrsUpdatableFieldsEquals(returnedPqrs, getPersistedPqrs(returnedPqrs));

        insertedPqrs = returnedPqrs;
    }

    @Test
    void createPqrsWithExistingId() throws Exception {
        // Create the Pqrs with an existing ID
        pqrs.setId("existing_id");
        PqrsDTO pqrsDTO = pqrsMapper.toDto(pqrs);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPqrsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pqrsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pqrs in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTituloIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pqrs.setTitulo(null);

        // Create the Pqrs, which fails.
        PqrsDTO pqrsDTO = pqrsMapper.toDto(pqrs);

        restPqrsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pqrsDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFechaCreacionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pqrs.setFechaCreacion(null);

        // Create the Pqrs, which fails.
        PqrsDTO pqrsDTO = pqrsMapper.toDto(pqrs);

        restPqrsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pqrsDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pqrs.setEstado(null);

        // Create the Pqrs, which fails.
        PqrsDTO pqrsDTO = pqrsMapper.toDto(pqrs);

        restPqrsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pqrsDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPqrs() throws Exception {
        // Initialize the database
        insertedPqrs = pqrsRepository.save(pqrs);

        // Get all the pqrsList
        restPqrsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pqrs.getId())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION.toString())))
            .andExpect(jsonPath("$.[*].fechaCreacion").value(hasItem(DEFAULT_FECHA_CREACION.toString())))
            .andExpect(jsonPath("$.[*].fechaLimiteRespuesta").value(hasItem(DEFAULT_FECHA_LIMITE_RESPUESTA.toString())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO)));
    }

    @Test
    void getPqrs() throws Exception {
        // Initialize the database
        insertedPqrs = pqrsRepository.save(pqrs);

        // Get the pqrs
        restPqrsMockMvc
            .perform(get(ENTITY_API_URL_ID, pqrs.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pqrs.getId()))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION.toString()))
            .andExpect(jsonPath("$.fechaCreacion").value(DEFAULT_FECHA_CREACION.toString()))
            .andExpect(jsonPath("$.fechaLimiteRespuesta").value(DEFAULT_FECHA_LIMITE_RESPUESTA.toString()))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO));
    }

    @Test
    void getNonExistingPqrs() throws Exception {
        // Get the pqrs
        restPqrsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingPqrs() throws Exception {
        // Initialize the database
        insertedPqrs = pqrsRepository.save(pqrs);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pqrs
        Pqrs updatedPqrs = pqrsRepository.findById(pqrs.getId()).orElseThrow();
        updatedPqrs
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .fechaCreacion(UPDATED_FECHA_CREACION)
            .fechaLimiteRespuesta(UPDATED_FECHA_LIMITE_RESPUESTA)
            .estado(UPDATED_ESTADO);
        PqrsDTO pqrsDTO = pqrsMapper.toDto(updatedPqrs);

        restPqrsMockMvc
            .perform(put(ENTITY_API_URL_ID, pqrsDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pqrsDTO)))
            .andExpect(status().isOk());

        // Validate the Pqrs in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPqrsToMatchAllProperties(updatedPqrs);
    }

    @Test
    void putNonExistingPqrs() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pqrs.setId(UUID.randomUUID().toString());

        // Create the Pqrs
        PqrsDTO pqrsDTO = pqrsMapper.toDto(pqrs);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPqrsMockMvc
            .perform(put(ENTITY_API_URL_ID, pqrsDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pqrsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pqrs in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPqrs() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pqrs.setId(UUID.randomUUID().toString());

        // Create the Pqrs
        PqrsDTO pqrsDTO = pqrsMapper.toDto(pqrs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPqrsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pqrsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pqrs in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPqrs() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pqrs.setId(UUID.randomUUID().toString());

        // Create the Pqrs
        PqrsDTO pqrsDTO = pqrsMapper.toDto(pqrs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPqrsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pqrsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pqrs in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePqrsWithPatch() throws Exception {
        // Initialize the database
        insertedPqrs = pqrsRepository.save(pqrs);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pqrs using partial update
        Pqrs partialUpdatedPqrs = new Pqrs();
        partialUpdatedPqrs.setId(pqrs.getId());

        partialUpdatedPqrs.descripcion(UPDATED_DESCRIPCION).estado(UPDATED_ESTADO);

        restPqrsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPqrs.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPqrs))
            )
            .andExpect(status().isOk());

        // Validate the Pqrs in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPqrsUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPqrs, pqrs), getPersistedPqrs(pqrs));
    }

    @Test
    void fullUpdatePqrsWithPatch() throws Exception {
        // Initialize the database
        insertedPqrs = pqrsRepository.save(pqrs);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pqrs using partial update
        Pqrs partialUpdatedPqrs = new Pqrs();
        partialUpdatedPqrs.setId(pqrs.getId());

        partialUpdatedPqrs
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .fechaCreacion(UPDATED_FECHA_CREACION)
            .fechaLimiteRespuesta(UPDATED_FECHA_LIMITE_RESPUESTA)
            .estado(UPDATED_ESTADO);

        restPqrsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPqrs.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPqrs))
            )
            .andExpect(status().isOk());

        // Validate the Pqrs in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPqrsUpdatableFieldsEquals(partialUpdatedPqrs, getPersistedPqrs(partialUpdatedPqrs));
    }

    @Test
    void patchNonExistingPqrs() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pqrs.setId(UUID.randomUUID().toString());

        // Create the Pqrs
        PqrsDTO pqrsDTO = pqrsMapper.toDto(pqrs);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPqrsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pqrsDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(pqrsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pqrs in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPqrs() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pqrs.setId(UUID.randomUUID().toString());

        // Create the Pqrs
        PqrsDTO pqrsDTO = pqrsMapper.toDto(pqrs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPqrsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pqrsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pqrs in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPqrs() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pqrs.setId(UUID.randomUUID().toString());

        // Create the Pqrs
        PqrsDTO pqrsDTO = pqrsMapper.toDto(pqrs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPqrsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(pqrsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pqrs in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePqrs() throws Exception {
        // Initialize the database
        insertedPqrs = pqrsRepository.save(pqrs);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pqrs
        restPqrsMockMvc
            .perform(delete(ENTITY_API_URL_ID, pqrs.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pqrsRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Pqrs getPersistedPqrs(Pqrs pqrs) {
        return pqrsRepository.findById(pqrs.getId()).orElseThrow();
    }

    protected void assertPersistedPqrsToMatchAllProperties(Pqrs expectedPqrs) {
        assertPqrsAllPropertiesEquals(expectedPqrs, getPersistedPqrs(expectedPqrs));
    }

    protected void assertPersistedPqrsToMatchUpdatableProperties(Pqrs expectedPqrs) {
        assertPqrsAllUpdatablePropertiesEquals(expectedPqrs, getPersistedPqrs(expectedPqrs));
    }
}
