package br.edu.divulgaambulantes.config;

import jakarta.persistence.EntityManagerFactory;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean factory =
                new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(dataSource);
        factory.setPackagesToScan("br.edu.divulgaambulantes.entity");
        factory.setPersistenceProviderClass(PersistenceProvider.class);

        Map<String, Object> props = new HashMap<>();

        // EclipseLink config
        props.put("eclipselink.logging.level", "FINE");
        props.put("eclipselink.ddl-generation", "none");
        props.put("eclipselink.weaving", "false");

        factory.setJpaPropertyMap(props);

        return factory;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}