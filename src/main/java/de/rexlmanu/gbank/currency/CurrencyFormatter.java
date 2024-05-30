package de.rexlmanu.gbank.currency;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

@UtilityClass
public class CurrencyFormatter {
  public String format(Currency currency, double amount) {
    return currency.format().formatted(amount);
  }

  public TagResolver placeholder(Currency currency, double amount) {
    return Placeholder.parsed("amount", format(currency, amount));
  }

  public TagResolver placeholder(String placeholderName, Currency currency, double amount) {
    return Placeholder.parsed(placeholderName, format(currency, amount));
  }
}
