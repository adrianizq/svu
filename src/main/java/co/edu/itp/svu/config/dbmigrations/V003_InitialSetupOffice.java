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
            log.error("Migration 'insert-initial-oficinas' failed: User 'admin' not found.");
            throw new RuntimeException("Required 'admin' user not found for Oficina migration.");
        } else {
            log.info("Found responsible user: {}", responsableUser.getLogin());
        }

        List<Oficina> officesToInsert = new ArrayList<>();

        Oficina rectoria = new Oficina();
        rectoria.setNombre("Rectoría");
        rectoria.setDescripcion("Oficina principal de la institución.");
        rectoria.setNivel("1");
        rectoria.setOficinaSuperior("Consejo");
        rectoria.setResponsable(responsableUser);
        rectoria.setPqrsList(new ArrayList<>());
        officeNames.add(rectoria.getNombre());
        officesToInsert.add(rectoria);

        Oficina vicerrectoriaAcademica = new Oficina();
        vicerrectoriaAcademica.setNombre("Vicerrectoría Académica");
        vicerrectoriaAcademica.setDescripcion("Coordina las actividades académicas.");
        vicerrectoriaAcademica.setNivel("2");
        vicerrectoriaAcademica.setOficinaSuperior("Rectoría");
        vicerrectoriaAcademica.setResponsable(responsableUser);
        vicerrectoriaAcademica.setPqrsList(new ArrayList<>());
        officeNames.add(vicerrectoriaAcademica.getNombre());
        officesToInsert.add(vicerrectoriaAcademica);

        Oficina secretariaGeneral = new Oficina();
        secretariaGeneral.setNombre("Secretaría General");
        secretariaGeneral.setDescripcion("Administra documentos y procesos formales.");
        secretariaGeneral.setNivel("2");
        secretariaGeneral.setOficinaSuperior("Rectoría");
        secretariaGeneral.setResponsable(responsableUser);
        secretariaGeneral.setPqrsList(new ArrayList<>());
        officeNames.add(secretariaGeneral.getNombre());
        officesToInsert.add(secretariaGeneral);

        Oficina admisiones = new Oficina();
        admisiones.setNombre("Oficina de Admisiones y Registro");
        admisiones.setDescripcion("Gestiona el ingreso y registro de estudiantes.");
        admisiones.setNivel("3");
        admisiones.setOficinaSuperior("Vicerrectoría Académica");
        admisiones.setResponsable(responsableUser);
        admisiones.setPqrsList(new ArrayList<>());
        officeNames.add(admisiones.getNombre());
        officesToInsert.add(admisiones);

        Oficina Ciecyt = new Oficina();
        Ciecyt.setNombre("Cetro de Investigación");
        Ciecyt.setDescripcion("maneja las investigaciones.");
        Ciecyt.setNivel("3");
        Ciecyt.setOficinaSuperior("Rectoría");
        Ciecyt.setResponsable(responsableUser);
        Ciecyt.setPqrsList(new ArrayList<>());
        officeNames.add(secretariaGeneral.getNombre());
        officesToInsert.add(Ciecyt);

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
        log.warn("Executing rollback for migration: insert-initial-oficinas");

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
