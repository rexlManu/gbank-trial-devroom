package de.rexlmanu.gbank.command.parser;

import de.rexlmanu.gbank.currency.Currency;
import de.rexlmanu.gbank.currency.CurrencyNotFoundException;
import de.rexlmanu.gbank.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.SuggestionProvider;

@RequiredArgsConstructor
public class CurrencyParser implements ArgumentParser<CommandSender, Currency> {
  private final CurrencyService currencyService;

  @Override
  public @NonNull ArgumentParseResult<@NonNull Currency> parse(
      @NonNull CommandContext<@NonNull CommandSender> commandContext,
      @NonNull CommandInput commandInput) {
    String input = commandInput.readString();

    try {
      return ArgumentParseResult.success(this.currencyService.currency(input));
    } catch (CurrencyNotFoundException e) {
      return ArgumentParseResult.failure(new CurrencyParseException(input, commandContext));
    }
  }

  @Override
  public @NonNull SuggestionProvider<CommandSender> suggestionProvider() {
    return SuggestionProvider.suggestingStrings(
        this.currencyService.currencies().stream().map(Currency::name).toList());
  }

  public static final class CurrencyParseException extends ParserException {

    private final String input;

    public CurrencyParseException(
        final @NonNull String input, final @NonNull CommandContext<?> context) {
      super(
          BankUserParser.class,
          context,
          Caption.of("argument.parse.failure.currency"),
          CaptionVariable.of("input", input));
      this.input = input;
    }

    public @NonNull String input() {
      return this.input;
    }
  }
}
