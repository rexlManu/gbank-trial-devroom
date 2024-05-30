package de.rexlmanu.gbank.tax;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.config.ConfigProvider;
import de.rexlmanu.gbank.config.PluginConfig;
import de.rexlmanu.gbank.currency.Currency;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TaxService {
  private final ConfigProvider configProvider;

  public float taxes(Currency currency) {
    return this.configProvider
        .get(PluginConfig.class)
        .payTaxes()
        .getOrDefault(currency.name().toLowerCase(), 0.0F);
  }

  public double calculateTax(Currency currency, double amount) {
    float taxes = this.taxes(currency);
    if (taxes == 0.0F || taxes < 0.00000F) return 0.0D;
    return amount * taxes;
  }
}
