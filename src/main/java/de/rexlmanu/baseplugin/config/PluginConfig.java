package de.rexlmanu.baseplugin.config;

import de.exlll.configlib.Configuration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class PluginConfig {
  String databaseUrl = "jdbc:sqlite:plugins/baseplugin/database.db";
  String databaseUsername = "root";
  String databasePassword = "";
}
