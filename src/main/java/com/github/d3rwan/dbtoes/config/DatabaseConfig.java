package com.github.d3rwan.dbtoes.config;

import java.sql.Driver;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.github.d3rwan.dbtoes.common.Constants;
import com.github.d3rwan.dbtoes.exceptions.ConfigException;

/**
 * DB Config 
 * 
 * @author d3rwan
 * 
 */
@Configuration
public class DatabaseConfig {

    @Autowired
    private Environment environment;

    public DataSource dataSource() throws ConfigException {
        try {
            SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
            Driver driver = (Driver) Class.forName(
                    environment.getProperty(Constants.CONFIG_DB_DRIVER)).newInstance();
            dataSource.setDriverClass(driver.getClass());
            dataSource.setUrl(environment.getProperty(Constants.CONFIG_DB_URL));
            dataSource.setUsername(environment.getProperty(Constants.CONFIG_DB_USER));
            dataSource.setPassword(environment.getProperty(Constants.CONFIG_DB_PASSWORD));
            return dataSource;
        } catch (Exception ex) {
            throw new ConfigException(ex.getMessage(), ex.getCause());
        }
    }

    @Bean
    public PlatformTransactionManager transactionManager()
            throws ConfigException {
        return new HibernateTransactionManager(sessionFactory().getObject());
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() throws ConfigException {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setHibernateProperties(hibernateProperties());
        factory.setDataSource(dataSource());
        factory.setPackagesToScan(new String[] { "com.github.d3rwan.dbtoes.models" });
        return factory;
    }

    @Bean
    public Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect",
                environment.getProperty(Constants.CONFIG_HB_DIALECT));
        hibernateProperties.put("hibernate.show_sql",
                environment.getProperty(Constants.CONFIG_HB_SHOWSQL));
        return hibernateProperties;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws ConfigException {
        return new JdbcTemplate(dataSource());
    }
}
