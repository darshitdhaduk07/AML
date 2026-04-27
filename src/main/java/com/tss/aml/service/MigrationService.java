package com.tss.aml.service;

import com.tss.aml.context.TenantContext;
import com.tss.aml.dto.request.RegisterRequestDto;
import com.tss.aml.enums.TenantStatus;
import com.tss.aml.exception.MigrationException;
import com.tss.aml.master.entity.Tenant;
import com.tss.aml.master.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import static com.tss.aml.constant.GlobalConstants.DB_NAME;

@Service
@RequiredArgsConstructor
public class MigrationService {

    private final TenantRepository tenantRepository;
    //    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

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


            System.out.println("Hello2");
            flyway.migrate();
            System.out.println("HOHO1");
            System.out.println("heello3");
        } catch (Exception e) {
            throw new MigrationException(
                    "Flyway migration failed for schema: " + schemaName
            );
        }
    }

    public void saveTenant(String schemaName, RegisterRequestDto request) {
        Tenant tenant = new Tenant();
        tenant.setTenantName(request.getTenantName());
        tenant.setSchemaName(schemaName);
        tenant.setTenantStatus(TenantStatus.DISABLED);
        tenant.setDbName(DB_NAME);

        tenantRepository.save(tenant);

        TenantContext.setTenant(schemaName);
    }

}
