package de.rexlmanu.gbank.command;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.injection.ParameterInjector;
import org.incendo.cloud.parser.ArgumentParser;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CommandContainer {
  private final Injector injector;
  private final CommandManager<CommandSender> commandManager;
  private final AnnotationParser<CommandSender> annotationParser;
  private final MiniMessage miniMessage;

  public CommandContainer withMessages(Map<String, String> customMessages) {
    customMessages.forEach(
        (s, s2) ->
            this.commandManager
                .captionRegistry()
                .registerProvider(CaptionProvider.constantProvider(Caption.of(s), s2)));
    return this;
  }

  public <T> CommandContainer withContextInjector(
      final @NonNull Class<T> clazz, final @NonNull ParameterInjector<CommandSender, T> injector) {
    this.commandManager.parameterInjectorRegistry().registerInjector(clazz, injector);
    return this;
  }

  public CommandContainer withParser(String name, ArgumentParser<CommandSender, ?> parser) {
    this.commandManager
        .parserRegistry()
        .registerNamedParserSupplier(name, parserParameters -> parser);
    return this;
  }

  public CommandContainer withCommands(Class<?>... commandClasses) {
    for (Class<?> commandClass : commandClasses) {
      this.annotationParser.parse(this.injector.getInstance(commandClass));
    }
    return this;
  }
}
