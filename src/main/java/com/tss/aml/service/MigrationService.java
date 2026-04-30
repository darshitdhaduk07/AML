package com.tss.aml.service;

import com.tss.aml.dto.request.TenantRegisterRequestDto;
import com.tss.aml.enums.TenantStatus;
import com.tss.aml.exception.MigrationException;
import com.tss.aml.master.entity.Tenant;
import com.tss.aml.master.repository.TenantRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import static com.tss.aml.constant.GlobalConstants.DB_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrationService {

    private final TenantRepository tenantRepository;
    //    private final JdbcTemplate jdbcTemplate;

    private final DataSource dataSource;
    private final EntityManager entityManager;

//    public void createSchema(String schemaName){
//        try {
//            System.out.println("Hii");
//            jdbcTemplate.execute("CREATE SCHEMA " + schemaName);
//
//        } catch (Exception ex) {
//            throw new RuntimeException("Tenant registration failed: " + ex.getMessage());
//        }
//    }

    public void runFlyWay(String schemaName) {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(schemaName)
                    .locations("classpath:db/migration/tenant")
                    .baselineOnMigrate(true)
//                    .ignoreMigrationPatterns("*:*")
                    .failOnMissingLocations(false)
                    .load();


            log.info("Starting Flyway migration for schema: {}", schemaName);
            flyway.migrate();
            log.info("Flyway migration completed successfully for schema: {}", schemaName);
        } catch (Exception e) {
            throw new MigrationException(
                    "Flyway migration failed for schema: " + schemaName
            );
        }
    }

    public void saveTenant(String schemaName, TenantRegisterRequestDto request) {
        Tenant tenant = new Tenant();
        tenant.setTenantName(request.getTenantName());
        tenant.setSchemaName(schemaName);
        tenant.setTenantStatus(TenantStatus.DISABLED);
        tenant.setDbName(DB_NAME);

        tenantRepository.save(tenant);
    }

}
