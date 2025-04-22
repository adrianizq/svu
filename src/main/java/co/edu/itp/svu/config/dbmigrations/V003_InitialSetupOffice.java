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

    private final List<String> officeNames = new ArrayList<>();

    private static final String OFFICE_COLLECTION_NAME = "oficina";
    private static final String USER_COLLECTION_NAME = "jhi_user";

    public V003_InitialSetupOffice(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void insertInitialOffices() {
        log.info("Executing migration: v003-initial-setup-office");

        Query adminUserQuery = new Query(Criteria.where("login").is("admin"));
        User responsableUser = mongoTemplate.findOne(adminUserQuery, User.class, USER_COLLECTION_NAME);

        if (responsableUser == null) {
            log.error("Migration 'v003-initial-setup-office' failed: User 'admin' not found.");
            throw new RuntimeException("Required 'admin' user not found for Oficina migration.");
        } else {
            log.info("Found responsible user: {}", responsableUser.getLogin());
        }

        List<Oficina> officesToInsert = new ArrayList<>();

        Oficina rectory = new Oficina();
        rectory.setNombre("Rectoría");
        rectory.setDescripcion("Oficina principal de la institución.");
        rectory.setNivel("1");
        rectory.setOficinaSuperior("Consejo");
        rectory.setResponsable(responsableUser);
        rectory.setPqrsList(new ArrayList<>());
        officeNames.add(rectory.getNombre());
        officesToInsert.add(rectory);

        Oficina AcademicViceRectorate = new Oficina();
        AcademicViceRectorate.setNombre("Vicerrectoría Académica");
        AcademicViceRectorate.setDescripcion("Coordina las actividades académicas.");
        AcademicViceRectorate.setNivel("2");
        AcademicViceRectorate.setOficinaSuperior("Rectoría");
        AcademicViceRectorate.setResponsable(responsableUser);
        AcademicViceRectorate.setPqrsList(new ArrayList<>());
        officeNames.add(AcademicViceRectorate.getNombre());
        officesToInsert.add(AcademicViceRectorate);

        Oficina generalsecretary = new Oficina();
        generalsecretary.setNombre("Secretaría General");
        generalsecretary.setDescripcion("Administra documentos y procesos formales.");
        generalsecretary.setNivel("2");
        generalsecretary.setOficinaSuperior("Rectoría");
        generalsecretary.setResponsable(responsableUser);
        generalsecretary.setPqrsList(new ArrayList<>());
        officeNames.add(generalsecretary.getNombre());
        officesToInsert.add(generalsecretary);

        Oficina admissions = new Oficina();
        admissions.setNombre("Oficina de Admisión y Registro");
        admissions.setDescripcion("Gestiona el ingreso y registro de estudiantes.");
        admissions.setNivel("3");
        admissions.setOficinaSuperior("Vicerrectoría Académica");
        admissions.setResponsable(responsableUser);
        admissions.setPqrsList(new ArrayList<>());
        officeNames.add(admissions.getNombre());
        officesToInsert.add(admissions);

        Oficina ciecyt = new Oficina();
        ciecyt.setNombre("Cetro de Investigación");
        ciecyt.setDescripcion("maneja las investigaciones.");
        ciecyt.setNivel("3");
        ciecyt.setOficinaSuperior("Rectoría");
        ciecyt.setResponsable(responsableUser);
        ciecyt.setPqrsList(new ArrayList<>());
        officeNames.add(ciecyt.getNombre());
        officesToInsert.add(ciecyt);

        if (!officesToInsert.isEmpty()) {
            try {
                mongoTemplate.insert(officesToInsert, OFFICE_COLLECTION_NAME);
                log.info("Successfully inserted {} initial oficinas.", officesToInsert.size());
            } catch (Exception e) {
                log.error("Error inserting initial oficinas: {}", e.getMessage(), e);

                throw new RuntimeException("Failed to insert initial oficinas.", e);
            }
        } else {
            log.warn("No oficinas defined to insert in this migration run.");
        }
    }

    @RollbackExecution
    public void rollback() {
        log.warn("Executing rollback for migration: v003-initial-setup-office");

        if (!officeNames.isEmpty()) {
            Query rollbackQuery = new Query(Criteria.where("nombre").in(officeNames));
            try {
                long deletedCount = mongoTemplate.remove(rollbackQuery, OFFICE_COLLECTION_NAME).getDeletedCount();
                log.info("Rollback successful: Deleted {} oficinas with names in {}.", deletedCount, officeNames);
            } catch (Exception e) {
                log.error("Error during rollback of initial oficinas: {}", e.getMessage(), e);
            }
        } else {
            log.warn("Rollback skipped: No oficina names were recorded during execution.");
        }
    }
}
