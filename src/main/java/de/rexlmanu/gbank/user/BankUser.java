package de.rexlmanu.gbank.user;

import de.rexlmanu.gbank.currency.Currency;
import de.rexlmanu.gbank.wallet.Wallet;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface BankUser {
  String username();

  UUID uniqueId();

  LocalDateTime createdAt();

  Collection<Wallet> wallets();

  default Optional<Wallet> walletOr(Currency currency) {
    return this.wallets().stream().filter(wallet -> wallet.currency().equals(currency)).findFirst();
  }

  default Wallet wallet(Currency currency) {
    return this.walletOr(currency)
        .orElseGet(
            () -> {
              Wallet wallet = new Wallet(currency, 0.0D);
              this.addWallet(wallet);
              return wallet;
            });
  }

  void addWallet(Wallet wallet);

  BankUser username(String username);
}
