package io.vlingo.xoom.turbo.testcontainers;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SharedMySQLContainer extends MySQLContainer<SharedMySQLContainer> {
  private static final DockerImageName image = DockerImageName.parse("mysql/mysql-server:latest").asCompatibleSubstituteFor("mysql");
  private static SharedMySQLContainer instance;

  private SharedMySQLContainer() {
    super(image);
  }

  @SuppressWarnings("resource")
  public static SharedMySQLContainer getInstance() {
    if (instance == null) {
      String username = "xoom_test";
      String databaseName = "STORAGE_TEST";
      String password = "vlingo123";
      instance = new SharedMySQLContainer()
          .withEnv("MYSQL_ROOT_HOST", "%")
          .withDatabaseName(databaseName)
          .withExposedPorts(3306)
          .withPassword(password);
      instance.start();
      instance.createUser(username, password);
      instance.withUsername(username);
    }
    return instance;
  }

  @Override
  public void stop() {
    // do nothing, the JVM handles shut down
  }

  private void createUser(String username, String password) {
    try (Connection connection = DriverManager.getConnection(getJdbcUrl(), "root", getPassword())) {
      connection.createStatement().executeUpdate(String.format("CREATE USER '%s'@'%%' IDENTIFIED BY '%s';", username, password));
      connection.createStatement().executeUpdate(String.format("GRANT ALL PRIVILEGES ON *.* TO '%s'@'%%';", username));
    } catch (SQLException cause) {
      throw new RuntimeException("Failed to create the test user.", cause);
    }
  }
}
