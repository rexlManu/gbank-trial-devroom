package de.rexlmanu.baseplugin;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.rexlmanu.baseplugin.command.CommandContainer;
import de.rexlmanu.baseplugin.command.BasePluginCommand;
import de.rexlmanu.baseplugin.config.ConfigProvider;
import de.rexlmanu.baseplugin.config.PluginConfig;
import de.rexlmanu.baseplugin.config.message.MessageConfig;
import de.rexlmanu.baseplugin.database.DatabaseManager;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class BasePlugin extends JavaPlugin {
  private static final Logger log = LoggerFactory.getLogger(BasePlugin.class);
  private final ConfigProvider configProvider = new ConfigProvider(this.getDataFolder().toPath());

  @Getter(AccessLevel.NONE)
  private Injector injector;

  @Inject private CommandContainer commandContainer;
  @Inject private DatabaseManager databaseManager;

  @Override
  public void onEnable() {
    this.configProvider.register(PluginConfig.class, "config");
    this.configProvider.register(MessageConfig.class, "messages");
    try {
      this.injector = Guice.createInjector(new BasePluginModule(this));
      this.injector.injectMembers(this);
    } catch (Exception e) {
      log.error("Failed to enable plugin", e);
      this.getServer().getPluginManager().disablePlugin(this);
      return;
    }

    this.databaseManager.open();
    this.commandContainer.withCommands(BasePluginCommand.class);
  }

  @Override
  public void onDisable() {
    // plugin didn't enable properly
    if (this.injector == null) {
      return;
    }
    this.databaseManager.close();
  }

  @SafeVarargs
  private void registerListeners(Class<? extends Listener>... classes) {
    Arrays.stream(classes)
        .forEach(
            clazz ->
                this.getServer()
                    .getPluginManager()
                    .registerEvents(this.injector.getInstance(clazz), this));
  }
}
