package de.rexlmanu.baseplugin.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import de.rexlmanu.baseplugin.config.ConfigProvider;
import de.rexlmanu.baseplugin.config.PluginConfig;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DatabaseManager {
  private final ConfigProvider configProvider;
  private ConnectionSource connectionSource;

  public void open() {
    var config = this.configProvider.get(PluginConfig.class);
    try {
      this.connectionSource =
          new JdbcPooledConnectionSource(
              config.databaseUrl(), config.databaseUsername(), config.databasePassword());
    } catch (SQLException e) {
      log.error("Failed to open database connection", e);
      throw new RuntimeException("Failed to open database connection");
    }
  }

  public void close() {
    try {
      this.connectionSource.close();
    } catch (Exception e) {
      log.error("Failed to close database connection", e);
    }
  }
}
