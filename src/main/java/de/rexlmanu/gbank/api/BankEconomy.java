package de.rexlmanu.gbank.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.currency.Currency;
import de.rexlmanu.gbank.currency.CurrencyService;
import de.rexlmanu.gbank.user.BankUserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BankEconomy implements Economy {
  private final BankUserService bankUserService;
  private final CurrencyService currencyService;

  @Override
  public double getBalance(UUID playerId, String currencyName) {
    Currency currency = this.currencyService.currency(currencyName);
    return this.bankUserService
        .find(playerId)
        .thenApply(bankUser -> bankUser.wallet(currency).balance())
        .exceptionally(throwable -> 0.0D)
        .join();
  }
}
