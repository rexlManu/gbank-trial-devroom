package de.rexlmanu.gbank.storage.mysql.statement;

import java.sql.Connection;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StatementTransaction {
  private final Connection connection;

  public StatementBuilder newBuilder(String statement) {
    return new StatementBuilder(this.connection)
        .statement(statement)
        .closeConnection(false)
        .logging(true);
  }
}
