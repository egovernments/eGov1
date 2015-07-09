/**
 * eGov suite of products aim to improve the internal efficiency,transparency, 
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation 
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or 
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

	1) All versions of this program, verbatim or modified must carry this 
	   Legal Notice.

	2) Any misrepresentation of the origin of the material is prohibited. It 
	   is required that all modified versions of this material be marked in 
	   reasonable ways as different from the original version.

	3) This license does not grant any rights to any user of the program 
	   with regards to rights under trademark law for use of the trade names 
	   or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.infra.config.persistence;

import static org.hibernate.cfg.AvailableSettings.AUTOCOMMIT;
import static org.hibernate.cfg.AvailableSettings.AUTO_CLOSE_SESSION;
import static org.hibernate.cfg.AvailableSettings.CACHE_REGION_FACTORY;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.JTA_PLATFORM;
import static org.hibernate.cfg.AvailableSettings.MULTI_TENANT;
import static org.hibernate.cfg.AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER;
import static org.hibernate.cfg.AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER;
import static org.hibernate.cfg.AvailableSettings.USE_MINIMAL_PUTS;
import static org.hibernate.cfg.AvailableSettings.USE_QUERY_CACHE;
import static org.hibernate.cfg.AvailableSettings.USE_SECOND_LEVEL_CACHE;
import static org.hibernate.cfg.AvailableSettings.USE_STREAMS_FOR_BINARY;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.support.ClasspathScanningPersistenceUnitPostProcessor;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
@EnableTransactionManagement(proxyTargetClass=true)
@Profile("production")
public class JpaConfiguration {
    @Autowired
    private Environment env;
    @Autowired
    private DataSource dataSource;

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JtaTransactionManager();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setJtaDataSource(dataSource);
        entityManagerFactory.setPersistenceUnitName("EgovPersistenceUnit");
        entityManagerFactory.setPackagesToScan(new String[] { "org.egov.**.entity" });
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdaper());
        entityManagerFactory.setJpaPropertyMap(additionalProperties());
        entityManagerFactory.setValidationMode(ValidationMode.NONE);
        entityManagerFactory.setSharedCacheMode(SharedCacheMode.DISABLE_SELECTIVE);
        final ClasspathScanningPersistenceUnitPostProcessor classpathScanningPPU = new ClasspathScanningPersistenceUnitPostProcessor("org.egov");
        classpathScanningPPU.setMappingFileNamePattern("**/*hbm.xml");
        entityManagerFactory.setPersistenceUnitPostProcessors(classpathScanningPPU);
        entityManagerFactory.afterPropertiesSet();
        return entityManagerFactory.getObject();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdaper() {
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(env.getProperty("jpa.database", Database.class));
        vendorAdapter.setShowSql(env.getProperty("jpa.showSql", Boolean.class));
        vendorAdapter.setGenerateDdl(env.getProperty("jpa.generateDdl", Boolean.class));
        return vendorAdapter;
    }

    @Bean(name = "sessionFactory")
    public HibernateJpaSessionFactoryBean sessionFactory() {
        final HibernateJpaSessionFactoryBean hibernateJpaSessionFactoryBean = new HibernateJpaSessionFactoryBean();
        hibernateJpaSessionFactoryBean.setEntityManagerFactory(entityManagerFactory());
        return hibernateJpaSessionFactoryBean;
    }

    private Map<String, Object> additionalProperties() {
        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.validator.apply_to_ddl", false);
        properties.put("hibernate.validator.autoregister_listeners", false);
        properties.put(DIALECT, env.getProperty(DIALECT));
        properties.put(GENERATE_STATISTICS, env.getProperty(GENERATE_STATISTICS));
        properties.put(CACHE_REGION_FACTORY, env.getProperty(CACHE_REGION_FACTORY));
        properties.put(USE_SECOND_LEVEL_CACHE, env.getProperty(USE_SECOND_LEVEL_CACHE));
        properties.put(USE_QUERY_CACHE, env.getProperty(USE_QUERY_CACHE));
        properties.put(USE_MINIMAL_PUTS, env.getProperty(USE_MINIMAL_PUTS));
        properties.put("hibernate.cache.infinispan.cachemanager", env.getProperty("hibernate.cache.infinispan.cachemanager"));
        properties.put("hibernate.search.lucene_version", env.getProperty("hibernate.search.lucene_version"));
        properties.put(JTA_PLATFORM, env.getProperty(JTA_PLATFORM));
        properties.put(AUTO_CLOSE_SESSION, env.getProperty(AUTO_CLOSE_SESSION));
        properties.put(USE_STREAMS_FOR_BINARY, env.getProperty(USE_STREAMS_FOR_BINARY));
        properties.put(AUTOCOMMIT, false);
        properties.put("jadira.usertype.autoRegisterUserTypes", true);
        properties.put("jadira.usertype.databaseZone", "jvm");
        //properties.put("hibernate.enable_lazy_load_no_trans", true);
        
        //Multitenancy Configuration
        if (env.getProperty("multitenancy.enabled",Boolean.class)) {
        	properties.put(MULTI_TENANT, env.getProperty(MULTI_TENANT));
        	properties.put("hibernate.database.type", env.getProperty("jpa.database"));
        	if (env.getProperty(MULTI_TENANT).equals("SCHEMA")) {
        		properties.put(MULTI_TENANT_CONNECTION_PROVIDER, "org.egov.infra.config.persistence.multitenancy.MultiTenantSchemaConnectionProvider");
            	properties.put(MULTI_TENANT_IDENTIFIER_RESOLVER, "org.egov.infra.config.persistence.multitenancy.DomainBasedSchemaTenantIdentifierResolver");
        	} else if (env.getProperty(MULTI_TENANT).equals("DATABASE")) {
        		properties.put(MULTI_TENANT_CONNECTION_PROVIDER, "org.egov.infra.config.persistence.multitenancy.MultiTenantDatabaseConnectionProvider");
            	properties.put(MULTI_TENANT_IDENTIFIER_RESOLVER, "org.egov.infra.config.persistence.multitenancy.DomainBasedDatabaseTenantIdentifierResolver");
        	}
        }
        return properties;
    }
}
