package de.rexlmanu.gbank.config;

import de.exlll.configlib.Configuration;
import de.rexlmanu.gbank.config.item.ConfigItem;
import de.rexlmanu.gbank.config.type.StorageConf;
import de.rexlmanu.gbank.currency.Currency;
import de.rexlmanu.gbank.storage.StorageType;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class PluginConfig {
  StorageConf storage =
      new StorageConf(StorageType.JSON, "jdbc:mysql://localhost:3306/gbank", "root", "password");

  List<Currency> currencies =
      List.of(
          new Currency("dollar", "$", "dollar", "dollars", "$%,.2f"),
          new Currency("euro", "€", "euro", "euros", "%,.2f €"),
          new Currency("yen", "¥", "yen", "yens", "%,.0f ¥"));

  ConfigItem backgroundItem = ConfigItem.builder().material(Material.BLACK_STAINED_GLASS_PANE)
      .name("<!i>")
      .build();

  ConfigItem nextPageItem = ConfigItem.builder().material(Material.STONE_BUTTON)
      .name("<!i><gray>Next page")
      .build();


  ConfigItem previousPageItem = ConfigItem.builder().material(Material.STONE_BUTTON)
      .name("<!i><gray>Previous Page")
      .build();

  ConfigItem currencyItem = ConfigItem.builder().material(Material.PAPER)
      .name("<!i><aqua><currency-singular> <currency-symbol>")
      .build();


}
