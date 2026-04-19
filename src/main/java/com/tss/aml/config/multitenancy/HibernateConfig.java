package com.tss.aml.config.multitenancy;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class HibernateConfig {

    private MultiTenantConnectionProviderImpl connectionProvider;

    private CurrentTenantIdentifierResolverImpl tenantResolver;

    public void customize(LocalContainerEntityManagerFactoryBean emf) {

        Map<String, Object> props = new HashMap<>();

        props.put("hibernate.multiTenancy", "SCHEMA");
        props.put("hibernate.multi_tenant_connection_provider", connectionProvider);
        props.put("hibernate.tenant_identifier_resolver", tenantResolver);

        emf.setJpaPropertyMap(props);
    }
}