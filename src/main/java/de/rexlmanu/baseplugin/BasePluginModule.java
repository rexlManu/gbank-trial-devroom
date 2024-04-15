package de.rexlmanu.baseplugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import de.rexlmanu.baseplugin.command.CloudCommandModule;
import de.rexlmanu.baseplugin.config.ConfigProvider;
import de.rexlmanu.baseplugin.utils.TaskScheduler;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class BasePluginModule extends AbstractModule {
  private final BasePlugin plugin;

  @Override
  protected void configure() {
    this.install(new CloudCommandModule());
    this.bind(BasePlugin.class).toInstance(this.plugin);
    this.bind(JavaPlugin.class).toInstance(this.plugin);
    this.bind(Logger.class).toInstance(this.plugin.getSLF4JLogger());
    this.bind(Gson.class).toInstance(new GsonBuilder().serializeNulls().create());
    this.bind(MiniMessage.class).toInstance(MiniMessage.miniMessage());
    this.bind(Server.class).toInstance(this.plugin.getServer());
    this.bind(TaskScheduler.class).toInstance(new TaskScheduler(this.plugin));
    this.bind(ConfigProvider.class).toInstance(this.plugin.configProvider());
    this.bind(ServicesManager.class).toInstance(this.plugin.getServer().getServicesManager());

    this.requestInjection(this.plugin.configProvider());
  }
}
