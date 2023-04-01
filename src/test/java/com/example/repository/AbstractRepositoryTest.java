package com.example.repository;

import com.example.config.TestConfig;
import jakarta.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class AbstractRepositoryTest {

  public static final long USER_ID = 1L;
  public static final String USER_EMAIL = "john.doe@example.com";

  @BeforeAll
  static void loadData(@Autowired DataSource dataSource) {
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("data.sql"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @AfterAll
  static void cleanup(@Autowired DataSource dataSource) {
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("cleanup.sql"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
