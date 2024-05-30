package de.rexlmanu.gbank.wallet;

import de.rexlmanu.gbank.currency.Currency;

public class WalletFactory {

  public static Wallet create(Currency currency, double amount) {
    return new Wallet(currency, amount);
  }
}
