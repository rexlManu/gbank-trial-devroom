package de.rexlmanu.gbank.storage.mysql.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.currency.CurrencyService;
import de.rexlmanu.gbank.storage.mysql.DatabaseConnection;
import de.rexlmanu.gbank.user.BankUser;
import de.rexlmanu.gbank.user.BankUserStorage;
import de.rexlmanu.gbank.wallet.Wallet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MySQLBankUserStorage implements BankUserStorage {
  private final DatabaseConnection databaseConnection;
  private final CurrencyService currencyService;

  @Override
  public void handleDisable() {
    this.databaseConnection.close();
  }

  @Override
  public Optional<UUID> findByUsername(String username) {
    return this.databaseConnection
        .newBuilder("SELECT unique_id FROM bank_users WHERE username = ? LIMIT 1;")
        .append(username)
        .query(resultSet -> UUID.fromString(resultSet.getString("unique_id")));
  }

  private BankUser resolveUser(ResultSet resultSet) throws SQLException {
    BankUser user = null;
    while (resultSet.next()) {
      if (user == null) {
        user =
            new MySQLBankUser(
                resultSet.getInt("u.id"),
                resultSet.getString("u.username"),
                UUID.fromString(resultSet.getString("u.unique_id")),
                resultSet.getTimestamp("u.created_at").toLocalDateTime());
      }

      String currency = resultSet.getString("w.currency");
      double amount = resultSet.getDouble("w.amount");
      if (currency != null) {
        user.addWallet(new Wallet(this.currencyService.currency(currency), amount));
      }
    }

    return user;
  }

  @Override
  public Optional<BankUser> findByUniqueId(UUID uniqueId) {
    return this.databaseConnection
        .newBuilder(
            "SELECT u.id, u.unique_id, u.username, u.created_at, w.currency, w.amount FROM bank_users u LEFT JOIN bank_wallets w ON u.id = w.bank_user_id WHERE u.unique_id = ?;")
        .append(uniqueId.toString())
        .queryRaw(this::resolveUser);
  }

  @Override
  public boolean delete(BankUser user) {
    return this.databaseConnection
        .newBuilder("DELETE FROM bank_users WHERE unique_id = ? LIMIT 1;")
        .append(user.uniqueId().toString())
        .execute()
        .filter(i -> i > 0)
        .isPresent();
  }

  @Override
  public Collection<BankUser> all() {
    return this.databaseConnection
        .newBuilder(
            "SELECT u.id, u.unique_id, u.username, u.created_at, w.currency, w.amount FROM bank_users u LEFT JOIN bank_wallets w ON u.id = w.bank_user_id;")
        .queryRaw(
            resultSet -> {
              Map<Integer, BankUser> users = new HashMap<>();

              while (resultSet.next()) {
                var id = resultSet.getInt("u.id");
                BankUser user =
                    users.computeIfAbsent(
                        id,
                        integer -> {
                          try {
                            return new MySQLBankUser(
                                id,
                                resultSet.getString("u.username"),
                                UUID.fromString(resultSet.getString("u.unique_id")),
                                resultSet.getTimestamp("u.created_at").toLocalDateTime());
                          } catch (SQLException e) {
                            throw new RuntimeException(e);
                          }
                        });

                String currency = resultSet.getString("w.currency");
                double amount = resultSet.getDouble("w.amount");
                if (currency != null) {
                  user.addWallet(new Wallet(this.currencyService.currency(currency), amount));
                }
              }

              return users.values();
            })
        .orElse(List.of());
  }

  @Override
  public void update(BankUser user) {
    MySQLBankUser mySQLBankUser = (MySQLBankUser) user;
    this.databaseConnection.transaction(
        transaction -> {
          transaction
              .newBuilder("UPDATE bank_users SET username = ? WHERE id = ? LIMIT 1;")
              .appends(user.username(), mySQLBankUser.id())
              .execute();

          for (Wallet wallet : user.wallets()) {
            transaction
                .newBuilder(
                    "INSERT INTO bank_wallets (bank_user_id, currency, amount) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE amount = ?;")
                .appends(
                    mySQLBankUser.id(),
                    wallet.currency().name(),
                    wallet.balance(),
                    wallet.balance())
                .execute();
          }
        });
  }

  @Override
  public void insert(BankUser user) {
    this.databaseConnection.transaction(
        transaction -> transaction
            .newBuilder("INSERT INTO bank_users (unique_id, username) VALUES (?, ?);")
            .autoGeneratedKeys(Statement.RETURN_GENERATED_KEYS)
            .appends(user.uniqueId().toString(), user.username())
            .generatedKeys(resultSet -> resultSet.getInt(1))
            .ifPresent(
                id -> {
                  ((MySQLBankUser) user).id(id);
                  user.wallets()
                      .forEach(
                          wallet ->
                              transaction
                                  .newBuilder(
                                      "INSERT INTO bank_wallets (bank_user_id, currency, amount) VALUES (?, ?, ?);")
                                  .appends(id, wallet.currency().name(), wallet.balance())
                                  .execute());
                }));
  }
}
