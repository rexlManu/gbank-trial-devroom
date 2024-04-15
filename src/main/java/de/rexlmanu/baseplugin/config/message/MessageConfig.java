package de.rexlmanu.baseplugin.config.message;

import de.exlll.configlib.Configuration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class MessageConfig {
  String pluginReloaded = "<gray>The plugin was <green>successfully</green> reloaded.";
}
