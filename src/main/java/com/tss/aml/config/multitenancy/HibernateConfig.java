package com.tss.aml.config.multitenancy;

import lombok.AllArgsConstructor;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class HibernateConfig implements HibernatePropertiesCustomizer {

    private MultiTenantConnectionProviderImpl connectionProvider;

    private CurrentTenantIdentifierResolverImpl tenantResolver;

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.multiTenancy", "SCHEMA");
        hibernateProperties.put("hibernate.multi_tenant_connection_provider", connectionProvider);
        hibernateProperties.put("hibernate.tenant_identifier_resolver", tenantResolver);

    }
}