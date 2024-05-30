package de.rexlmanu.gbank.command.parser;

import de.rexlmanu.gbank.user.BankUser;
import de.rexlmanu.gbank.user.BankUserService;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

@RequiredArgsConstructor
public class BankUserParser
    implements ArgumentParser.FutureArgumentParser<CommandSender, BankUser> {
  private final BankUserService bankUserService;

  @Override
  public @NonNull CompletableFuture<@NonNull ArgumentParseResult<BankUser>> parseFuture(
      @NonNull CommandContext<CommandSender> commandContext, @NonNull CommandInput commandInput) {
    String input = commandInput.readString();

    try {
      UUID uuid = UUID.fromString(input);
      return this.bankUserService
          .find(uuid)
          .thenApply(ArgumentParseResult::success)
          .exceptionally(
              throwable ->
                  ArgumentParseResult.failure(new BankUserParseException(input, commandContext)));
    } catch (IllegalArgumentException ignored) {
    }
    return this.bankUserService
        .find(input)
        .thenApply(ArgumentParseResult::success)
        .exceptionally(
            throwable ->
                ArgumentParseResult.failure(new BankUserParseException(input, commandContext)));
  }

  @Override
  public @NonNull SuggestionProvider<CommandSender> suggestionProvider() {
    return (context, input) ->
        this.bankUserService
            .usernames()
            .thenApply(strings -> strings.stream().map(Suggestion::suggestion).toList());
  }

  public static final class BankUserParseException extends ParserException {

    private final String input;

    public BankUserParseException(
        final @NonNull String input, final @NonNull CommandContext<?> context) {
      super(
          BankUserParser.class,
          context,
          Caption.of("argument.parse.failure.bankuser"),
          CaptionVariable.of("input", input));
      this.input = input;
    }

    public @NonNull String input() {
      return this.input;
    }
  }
}
