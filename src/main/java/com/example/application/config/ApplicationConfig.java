package com.example.application.config;

import com.example.application.dao.EventDao;
import com.example.application.dao.TicketDao;
import com.example.application.dao.UserDao;
import com.example.application.model.Category;
import com.example.application.model.Event;
import com.example.application.model.Ticket;
import com.example.application.model.User;
import com.example.application.service.RandomIdGenerator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan("com.example.application")
@Slf4j
@PropertySource("classpath:application.properties")
public class ApplicationConfig {

  @Autowired
  private Environment env;

  @Bean
  public Map<String, Object> inMemoryStorage() {
    return new HashMap<>();
  }

  @Bean
  public UserDao userDao() {
    return new UserDao(inMemoryStorage());
  }

  @Bean
  public EventDao eventDao() {
    return new EventDao(inMemoryStorage());
  }

  @Bean
  public TicketDao ticketDao() {
    return new TicketDao(inMemoryStorage());
  }

  @Bean
  public RandomIdGenerator idGenerator() {
    return new RandomIdGenerator();
  }

  @Bean
  public BeanPostProcessor storageDataInjector() {
    return new StorageDataInjector();
  }

  private class StorageDataInjector implements BeanPostProcessor {

    private static final String USER_PREFIX = "user:";
    private static final String TICKET_PREFIX = "ticket:";
    private static final String EVENT_PREFIX = "event:";

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
      if (isStorageBean(bean, beanName)) {
        Map<String, Object> storage = (Map<String, Object>) bean;
        readStorageDataFile(storage);
      }
      return bean;
    }

    private boolean isStorageBean(Object bean, String beanName) {
      return bean instanceof Map && beanName.equals("inMemoryStorage");
    }

    private void readStorageDataFile(Map<String, Object> storage) {
      String filename = env.getProperty("storage.data.path");

      try (val br = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = br.readLine()) != null) {
          int equalIndex = line.indexOf(",");
          String key = line.substring(0, equalIndex);
          String value = line.substring(equalIndex + 1);
          addValueToStorageBasedOnPrefix(storage, key, value);
        }
      } catch (IOException e) {
        log.error("Error reading storage data file", e);
      }
    }

    private void addUser(Map<String, Object> storage, String key, String value) {
        val user = createUser(value);
        storage.put(key, user);
    }

    private User createUser(String value) {
      String[] parts = value.split(",");
      long id = Long.parseLong(parts[0]);
      String name = parts[1];
      String email = parts[2];
      return new User(id, name, email);
    }

    private void addTicket(Map<String, Object> storage, String key, String value) {
      val ticket = createTicket(value);
      storage.put(key, ticket);
    }

    private Ticket createTicket(String value) {
      String[] parts = value.split(",");
      return Ticket.builder()
          .id(Long.parseLong(parts[0]))
          .eventId(Long.parseLong(parts[1]))
          .userId(Long.parseLong(parts[2]))
          .category(Category.valueOf(parts[3]))
          .place(Integer.parseInt(parts[4]))
          .build();
    }

    private void addEvent(Map<String, Object> storage, String key, String value) {
      val event = createEvent(value);
      storage.put(key, event);
    }

    private Event createEvent(String value) {
      String[] parts = value.split(",");
      return Event.builder()
          .id(Long.parseLong(parts[0]))
          .title(parts[1])
          .date(LocalDate.parse(parts[2]))
          .build();
    }

    private void addValueWithUnknownPrefixWarning(String key) {
      log.warn("Unrecognized key prefix in storage data file: {}", key);
    }

    private void addValueToStorageBasedOnPrefix(Map<String, Object> storage, String key, String value) {
      if (key.startsWith(USER_PREFIX)) {
        addUser(storage, key, value);
      } else if (key.startsWith(TICKET_PREFIX)) {
        addTicket(storage, key, value);
      } else if (key.startsWith(EVENT_PREFIX)) {
        addEvent(storage, key, value);
      } else {
        addValueWithUnknownPrefixWarning(key);
      }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
      if (isStorageBean(bean, beanName)) {
        log.info("Storage contents: {}", bean);
      }
      return bean;
    }
  }
}
