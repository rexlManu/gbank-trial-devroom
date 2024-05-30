package de.rexlmanu.gbank.storage.mysql;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.rexlmanu.gbank.config.ConfigProvider;
import de.rexlmanu.gbank.config.PluginConfig;
import de.rexlmanu.gbank.storage.mysql.statement.StatementBuilder;
import de.rexlmanu.gbank.storage.mysql.statement.StatementTransaction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

@Singleton
public class DatabaseConnection {
  private final ConfigProvider configProvider;

  private HikariDataSource dataSource;

  @Inject
  public DatabaseConnection(ConfigProvider configProvider) {
    this.configProvider = configProvider;

    this.setupDataSource();
  }

  public void setupDataSource() {
    HikariConfig hikariConfig = new HikariConfig();
    PluginConfig config = this.configProvider.get(PluginConfig.class);
    hikariConfig.setJdbcUrl(config.storage().url());
    hikariConfig.setUsername(config.storage().username());
    hikariConfig.setPassword(config.storage().password());

    this.dataSource = new HikariDataSource(hikariConfig);

    this.createTables();
  }

  public void close() {
    if (this.dataSource != null) {
      this.dataSource.close();
    }
  }

  public StatementBuilder newBuilder(String statement) {
    try {
      return new StatementBuilder(this.dataSource.getConnection())
          .statement(statement)
          .logging(true);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void transaction(Consumer<StatementTransaction> transactionConsumer) {
    try (Connection connection = this.dataSource.getConnection()) {
      connection.setAutoCommit(false);
      transactionConsumer.accept(new StatementTransaction(connection));
      connection.commit();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void createTables() {
    // bank_users: id, unique_id, username, created_at
    // bank_wallets: bank_user_id, currency, amount
    // notifications: id, unique_id, message, created_at
    this.newBuilder(
            """
            CREATE TABLE IF NOT EXISTS bank_users (
              id INT PRIMARY KEY AUTO_INCREMENT,
              unique_id VARCHAR(36) NOT NULL,
              username VARCHAR(16) NOT NULL,
              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """)
        .execute();

    this.newBuilder(
            """
            CREATE TABLE IF NOT EXISTS bank_wallets (
              bank_user_id INT NOT NULL,
              currency VARCHAR(16) NOT NULL,
              amount DOUBLE NOT NULL,
              PRIMARY KEY (bank_user_id, currency),
              CONSTRAINT fk_bank_user_id FOREIGN KEY (bank_user_id) REFERENCES bank_users (id) ON DELETE CASCADE
            )
            """)
        .execute();

    this.newBuilder(
            """
            CREATE TABLE IF NOT EXISTS notifications (
              id INT PRIMARY KEY AUTO_INCREMENT,
              unique_id VARCHAR(36) NOT NULL,
              message TEXT NOT NULL,
              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """)
        .execute();
  }
}
