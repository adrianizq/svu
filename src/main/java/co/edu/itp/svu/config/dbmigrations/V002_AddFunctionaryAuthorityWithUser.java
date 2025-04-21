package co.edu.itp.svu.config.dbmigrations;

import co.edu.itp.svu.domain.Authority;
import co.edu.itp.svu.security.AuthoritiesConstants;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@ChangeUnit(id = "v002-add-functionary-authority-with-user", order = "002", author = "luiscarlosjo157")
public class V002_AddFunctionaryAuthorityWithUser {

    private final Logger log = LoggerFactory.getLogger(V002_AddFunctionaryAuthorityWithUser.class);

    private final MongoTemplate mongoTemplate;

    public V002_AddFunctionaryAuthorityWithUser(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void addFunctionaryRole() {
        String functionaryRoleName = AuthoritiesConstants.FUNCTIONARY; // Usa la constante FUNCTIONARY

        // Verificar si el rol ya existe
        Query query = Query.query(Criteria.where("name").is(functionaryRoleName));
        boolean exists = mongoTemplate.exists(query, Authority.class);

        if (!exists) {
            Authority functionaryAuthority = new Authority();
            functionaryAuthority.setName(functionaryRoleName);
            mongoTemplate.save(functionaryAuthority);
            log.info("Created Authority: {}", functionaryRoleName);
        } else {
            log.warn("Authority {} already exists. Migration skipped.", functionaryRoleName);
        }
    }

    @RollbackExecution
    public void rollback() {
        String functionaryRoleName = AuthoritiesConstants.FUNCTIONARY;
        // Encuentra y elimina el rol específico
        Query query = Query.query(Criteria.where("name").is(functionaryRoleName));
        mongoTemplate.remove(query, Authority.class);
        log.info("Rolled back (removed) Authority: {}", functionaryRoleName);
    }
}
