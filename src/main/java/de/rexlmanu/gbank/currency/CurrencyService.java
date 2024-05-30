package de.rexlmanu.gbank.currency;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.config.ConfigProvider;
import de.rexlmanu.gbank.config.PluginConfig;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CurrencyService {
  private final ConfigProvider configProvider;

  public List<Currency> currencies() {
    return this.configProvider.get(PluginConfig.class).currencies();
  }

  public Currency currency(String name) {
    return this.currencies().stream()
        .filter(currency -> currency.name().equalsIgnoreCase(name))
        .findFirst()
        .orElseThrow(CurrencyNotFoundException::new);
  }
}
