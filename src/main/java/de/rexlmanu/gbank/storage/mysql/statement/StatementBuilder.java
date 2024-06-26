package de.rexlmanu.gbank.storage.mysql.statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Statement builder class
 *
 * <p>This class is used to create a statement and execute it with the given parameters.
 */
@RequiredArgsConstructor
public class StatementBuilder {
  @Setter private String statement = "";
  private final List<Object> parameters = new ArrayList<>();
  @Setter private boolean logging = false;
  private final Connection connection;
  @Setter private int autoGeneratedKeys = 2;
  @Setter private boolean closeConnection = true;

  /**
   * Append a parameter to the statement.
   *
   * <p>The type of the parameter must be supported by the set method of the prepared statement.
   *
   * @param parameter the parameter.
   * @return the statement builder.
   */
  public StatementBuilder append(Object parameter) {
    this.parameters.add(parameter);
    return this;
  }

  /**
   * Append multiple parameters to the statement.
   *
   * @param parameters the parameters.
   * @return the statement builder.
   */
  public StatementBuilder appends(Object... parameters) {
    Collections.addAll(this.parameters, parameters);
    return this;
  }

  /**
   * Creates a prepared statement and applies the executor.
   *
   * @param executor the executor.
   * @param <T> the result type of the executor.
   * @return the result of the executor.
   * @throws SQLException if an error occurs while creating the statement.
   */
  public <T> T createStatement(StatementExecutor<T> executor) throws SQLException {
    Objects.requireNonNull(this.connection, "Connection is null");
    if (this.statement.isEmpty()) {
      throw new IllegalStateException("Statement is empty");
    }
    PreparedStatement statement =
        this.connection.prepareStatement(this.statement, this.autoGeneratedKeys);
    for (int i = 0; i < this.parameters.size(); i++) {
      statement.setObject(i + 1, this.parameters.get(i));
    }

    return executor.apply(statement);
  }

  public <T> Optional<T> generatedKeys(StatementResolver<T> resolver) {
    try {
      return this.createStatement(
          statement -> {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
              return Optional.empty();
            }
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
              if (!resultSet.next()) {
                return Optional.empty();
              }
              return Optional.of(resolver.resolve(resultSet));
            } finally {
              if (this.closeConnection) {
                this.connection.close();
              }
            }
          });
    } catch (SQLException e) {
      if (this.logging) {
        e.printStackTrace();
      }
      return Optional.empty();
    }
  }

  /**
   * Query the database with the given statement.
   *
   * @return the result of the one row query.
   */
  public <T> Optional<T> query(StatementResolver<T> resolver) {
    try {
      return Optional.ofNullable(
          this.createStatement(
              statement -> {
                try (ResultSet resultSet = statement.executeQuery()) {
                  if (!resultSet.next()) {
                    return null;
                  }
                  return resolver.resolve(resultSet);
                } finally {
                  // Give the connection back to the pool
                  if (this.closeConnection) {
                    this.connection.close();
                  }
                }
              }));
    } catch (SQLException e) {
      if (this.logging) {
        e.printStackTrace();
      }
      return Optional.empty();
    }
  }

  public <T> Optional<T> queryRaw(StatementResolver<T> resolver) {
    try {
      return Optional.ofNullable(
          this.createStatement(
              statement -> {
                try (ResultSet resultSet = statement.executeQuery()) {
                  return resolver.resolve(resultSet);
                } finally {
                  // Give the connection back to the pool
                  if (this.closeConnection) {
                    this.connection.close();
                  }
                }
              }));
    } catch (SQLException e) {
      if (this.logging) {
        e.printStackTrace();
      }
      return Optional.empty();
    }
  }

  /**
   * Query the database with the given statement.
   *
   * @param resolver the resolver.
   * @param <T> the type of the result.
   * @return the result of the query with all rows.
   */
  public <T> Optional<List<T>> queryAll(StatementResolver<T> resolver) {
    try {
      return Optional.ofNullable(
          this.createStatement(
              statement -> {
                try (ResultSet resultSet = statement.executeQuery()) {
                  List<T> list = new ArrayList<>();
                  while (resultSet.next()) {
                    list.add(resolver.resolve(resultSet));
                  }
                  return list;
                } finally {
                  // Give the connection back to the pool
                  if (this.closeConnection) {
                    this.connection.close();
                  }
                }
              }));
    } catch (SQLException e) {
      if (this.logging) {
        e.printStackTrace();
      }
      return Optional.empty();
    }
  }

  /**
   * Execute the statement.
   *
   * @return the result of the statement.
   */
  public Optional<Integer> execute() {
    try {
      Integer result = this.createStatement(PreparedStatement::executeUpdate);
      // Give the connection back to the pool
      if (this.closeConnection) {
        this.connection.close();
      }
      return Optional.ofNullable(result);
    } catch (SQLException e) {
      if (this.logging) {
        e.printStackTrace();
      }
      return Optional.empty();
    }
  }
}
