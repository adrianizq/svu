package co.edu.itp.svu.config.dbmigrations;

import co.edu.itp.svu.domain.Oficina;
import co.edu.itp.svu.domain.User;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@ChangeUnit(id = "v003-initial-setup-office", order = "003", author = "luiscarlosjo157")
public class V003_InitialSetupOffice {

    private final Logger log = LoggerFactory.getLogger(V003_InitialSetupOffice.class);

    private final MongoTemplate mongoTemplate;

    private final List<String> createdOficinaNombres = new ArrayList<>();

    private static final String OFICINA_COLLECTION_NAME = "oficina";
    private static final String USER_COLLECTION_NAME = "jhi_user";

    public V003_InitialSetupOffice(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void insertInitialOficinas() {
        log.info("Executing migration: insert-initial-oficinas");

        // --- 1. Find a default responsible User (e.g., 'admin') ---
        // This assumes the 'admin' user exists from JHipster setup or another migration.
        Query adminUserQuery = new Query(Criteria.where("login").is("admin"));
        User responsableUser = mongoTemplate.findOne(adminUserQuery, User.class, USER_COLLECTION_NAME);

        if (responsableUser == null) {
            log.error("Migration 'insert-initial-oficinas' failed: User 'admin' not found.");
            // Option 1: Throw exception to halt migration (recommended if admin is required)
            throw new RuntimeException("Required 'admin' user not found for Oficina migration.");
            // Option 2: Log a warning and proceed without a responsible user (or with null)
            // log.warn("User 'admin' not found. Proceeding without setting responsible user for initial oficinas.");
            // responsableUser = null; // Explicitly set to null if proceeding
        } else {
            log.info("Found responsible user: {}", responsableUser.getLogin());
        }

        // --- 2. Define Oficina instances ---
        List<Oficina> oficinasToInsert = new ArrayList<>();

        Oficina rectoria = new Oficina();
        rectoria.setNombre("Rectoría");
        rectoria.setDescripcion("Oficina principal de la institución.");
        rectoria.setNivel("1"); // Top level
        rectoria.setOficinaSuperior("Consejo"); // No superior office
        rectoria.setResponsable(responsableUser);
        rectoria.setPqrsList(new ArrayList<>()); // Initialize PQRS list
        createdOficinaNombres.add(rectoria.getNombre());
        oficinasToInsert.add(rectoria);

        Oficina vicerrectoriaAcademica = new Oficina();
        vicerrectoriaAcademica.setNombre("Vicerrectoría Académica");
        vicerrectoriaAcademica.setDescripcion("Coordina las actividades académicas.");
        vicerrectoriaAcademica.setNivel("2");
        vicerrectoriaAcademica.setOficinaSuperior("Rectoría"); // Refers to the 'nombre' of the superior office
        vicerrectoriaAcademica.setResponsable(responsableUser); // Assign same user for simplicity, change if needed
        vicerrectoriaAcademica.setPqrsList(new ArrayList<>());
        createdOficinaNombres.add(vicerrectoriaAcademica.getNombre());
        oficinasToInsert.add(vicerrectoriaAcademica);

        Oficina secretariaGeneral = new Oficina();
        secretariaGeneral.setNombre("Secretaría General");
        secretariaGeneral.setDescripcion("Administra documentos y procesos formales.");
        secretariaGeneral.setNivel("2");
        secretariaGeneral.setOficinaSuperior("Rectoría");
        secretariaGeneral.setResponsable(responsableUser);
        secretariaGeneral.setPqrsList(new ArrayList<>());
        createdOficinaNombres.add(secretariaGeneral.getNombre());
        oficinasToInsert.add(secretariaGeneral);

        Oficina admisiones = new Oficina();
        admisiones.setNombre("Oficina de Admisiones y Registro");
        admisiones.setDescripcion("Gestiona el ingreso y registro de estudiantes.");
        admisiones.setNivel("3");
        admisiones.setOficinaSuperior("Vicerrectoría Académica"); // Reports to Vicerrectoría
        admisiones.setResponsable(responsableUser);
        admisiones.setPqrsList(new ArrayList<>());
        createdOficinaNombres.add(admisiones.getNombre());
        oficinasToInsert.add(admisiones);

        Oficina Ciecyt = new Oficina();
        Ciecyt.setNombre("Cetro de Investigación");
        Ciecyt.setDescripcion("maneja las investigaciones.");
        Ciecyt.setNivel("3");
        Ciecyt.setOficinaSuperior("Rectoría");
        Ciecyt.setResponsable(responsableUser);
        Ciecyt.setPqrsList(new ArrayList<>());
        createdOficinaNombres.add(secretariaGeneral.getNombre());
        oficinasToInsert.add(Ciecyt);

        // --- 3. Insert into Database ---
        if (!oficinasToInsert.isEmpty()) {
            try {
                // Use insertAll for efficiency
                mongoTemplate.insert(oficinasToInsert, OFICINA_COLLECTION_NAME);
                log.info("Successfully inserted {} initial oficinas.", oficinasToInsert.size());
            } catch (Exception e) {
                log.error("Error inserting initial oficinas: {}", e.getMessage(), e);
                // Rethrow to indicate migration failure
                throw new RuntimeException("Failed to insert initial oficinas.", e);
            }
        } else {
            log.warn("No oficinas defined to insert in this migration run.");
        }
    }

    @RollbackExecution
    public void rollback() {
        log.warn("Executing rollback for migration: insert-initial-oficinas");

        if (!createdOficinaNombres.isEmpty()) {
            // Remove only the documents created by this migration execution
            Query rollbackQuery = new Query(Criteria.where("nombre").in(createdOficinaNombres));
            try {
                long deletedCount = mongoTemplate.remove(rollbackQuery, OFICINA_COLLECTION_NAME).getDeletedCount();
                log.info("Rollback successful: Deleted {} oficinas with names in {}.", deletedCount, createdOficinaNombres);
            } catch (Exception e) {
                log.error("Error during rollback of initial oficinas: {}", e.getMessage(), e);
                // Log error but don't necessarily throw, as rollback failure is less critical than execution failure
            }
        } else {
            log.warn("Rollback skipped: No oficina names were recorded during execution.");
        }
    }
}
