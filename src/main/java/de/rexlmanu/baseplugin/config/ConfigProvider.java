package de.rexlmanu.baseplugin.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.exlll.configlib.YamlConfigurations;
import de.rexlmanu.baseplugin.config.item.ConfigItem;
import de.rexlmanu.baseplugin.config.item.ItemFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@Singleton
public class ConfigProvider {
  private final Map<Class<?>, String> configNames = new HashMap<>();
  private final Map<Class<?>, Object> loadedConfigs = new HashMap<>();
  private final Path dataDirectory;

  @Inject
  private ItemFactory itemFactory;
  @SneakyThrows
  @Inject
  void init() {
    Files.createDirectories(this.dataDirectory);
  }

  public void register(Class<?> configClass, String name) {
    var path = this.dataDirectory.resolve(name + ".yml");

    this.configNames.put(configClass, name);
    this.loadedConfigs.put(configClass, YamlConfigurations.update(path, configClass));
  }

  public <T> T get(Class<T> configClass) {
    var config = this.loadedConfigs.get(configClass);
    if (config == null) {
      throw new IllegalStateException("Config not loaded: " + configClass.getSimpleName());
    }
    return configClass.cast(config);
  }

  public void reload() {
    this.configNames.forEach(
        (configClass, name) -> {
          var path = this.dataDirectory.resolve(name + ".yml");
          this.loadedConfigs.put(configClass, YamlConfigurations.update(path, configClass));
        });
  }

  public ItemStack getItem(Function<PluginConfig, ConfigItem> mapper, TagResolver... tagResolver) {
    return this.itemFactory.create(mapper.apply(this.get(PluginConfig.class)), tagResolver);
  }
}
