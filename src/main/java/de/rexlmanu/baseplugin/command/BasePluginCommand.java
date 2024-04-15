package de.rexlmanu.baseplugin.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.baseplugin.config.ConfigProvider;
import de.rexlmanu.baseplugin.config.message.MessageConfig;
import de.rexlmanu.baseplugin.config.message.MessageManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Singleton
public class BasePluginCommand {
  private final ConfigProvider configProvider;
  private final MessageManager messageManager;

  @Command("baseplugin reload")
  @Permission("baseplugin.command.baseplugin.reload")
  public void reload(CommandSender sender) {
    this.configProvider.reload();
    this.messageManager.send(sender, MessageConfig::pluginReloaded);
  }
}
