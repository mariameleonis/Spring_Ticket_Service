package com.example.config;

import jakarta.persistence.EntityManagerFactory;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("com.example")
@PropertySource("classpath:application.properties")
@EnableJpaRepositories(basePackages="com.example.repository")
@EnableTransactionManagement
@EnableCaching
@RequiredArgsConstructor
public class AppConfig {

  private final Environment environment;
  private final ResourceLoader resourceLoader;

  @Bean
  public LocalSessionFactoryBean sessionFactory() {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource());
    sessionFactory.setPackagesToScan("com.example");
    sessionFactory.setHibernateProperties(hibernateProperties());

    return sessionFactory;
  }

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
    dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
    dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
    dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));

    return dataSource;
  }

  @Bean
  public EntityManagerFactory entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource());
    em.setPackagesToScan("com.example.entity");

    JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    em.setJpaProperties(hibernateProperties());
    em.afterPropertiesSet();
    return em.getObject();
  }

  @Bean
  public CacheManager cacheManager() {
    return new EhCacheCacheManager(ehCacheManager());
  }

  @Bean(destroyMethod="shutdown")
  public net.sf.ehcache.CacheManager ehCacheManager() {
    CacheConfiguration cacheConfiguration = new CacheConfiguration();
    cacheConfiguration.setName("myCache");
    cacheConfiguration.setMaxEntriesLocalHeap(1000);
    cacheConfiguration.setTimeToLiveSeconds(60);

    net.sf.ehcache.config.Configuration configuration = new net.sf.ehcache.config.Configuration();
    configuration.addCache(cacheConfiguration);

    return net.sf.ehcache.CacheManager.newInstance(configuration);
  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(entityManagerFactory());
    txManager.afterPropertiesSet();
    return txManager;
  }

  private Properties hibernateProperties() {
    Properties hibernateProperties = new Properties();
    hibernateProperties.setProperty(
        "hibernate.hbm2ddl.auto", environment.getRequiredProperty("hibernate.hbm2ddl.auto"));
    hibernateProperties.setProperty(
        "hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
    hibernateProperties.setProperty(
        "hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
    hibernateProperties.setProperty(
        "hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));

    return hibernateProperties;
  }

  @Bean
  public DatabasePopulator databasePopulator() {
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    Resource resource = resourceLoader.getResource("classpath:/sql/data.sql");
    populator.addScript(resource);
    return populator;
  }

  @Bean
  public DataSourceInitializer dataSourceInitializer() {
    DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource());
    initializer.setDatabasePopulator(databasePopulator());
    return initializer;
  }

}
