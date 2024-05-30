package de.rexlmanu.gbank.storage.mysql.user;

import com.google.common.collect.ImmutableSet;
import de.rexlmanu.gbank.user.BankUser;
import de.rexlmanu.gbank.wallet.Wallet;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MySQLBankUser implements BankUser {
  @Setter
  private int id;
  private String username;
  private final UUID uniqueId;
  private final LocalDateTime createdAt;
  private final Collection<Wallet> wallets;

  public MySQLBankUser(int id, String username, UUID uniqueId, LocalDateTime createdAt) {
    this.id = id;
    this.username = username;
    this.uniqueId = uniqueId;
    this.createdAt = createdAt;
    this.wallets = new HashSet<>();
  }

  @Override
  public Collection<Wallet> wallets() {
    return ImmutableSet.copyOf(this.wallets);
  }

  @Override
  public void addWallet(Wallet wallet) {
    this.wallets.add(wallet);
  }

  @Override
  public BankUser username(String username) {
    this.username = username;
    return this;
  }
}
