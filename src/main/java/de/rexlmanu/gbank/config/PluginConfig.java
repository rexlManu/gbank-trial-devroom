package de.rexlmanu.gbank.config;

import de.exlll.configlib.Configuration;
import de.rexlmanu.gbank.config.item.ConfigItem;
import de.rexlmanu.gbank.config.type.StorageConf;
import de.rexlmanu.gbank.currency.Currency;
import de.rexlmanu.gbank.storage.StorageType;
import java.util.List;
import java.util.Map;
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
          new Currency("yen", "¥", "yen", "yens", "%,.0f ¥"),
          new Currency("pound", "£", "pound", "pounds", "£%,.2f"),
          new Currency("bitcoin", "₿", "bitcoin", "bitcoins", "₿%,.2f"),
          new Currency("ether", "Ξ", "ether", "ethers", "Ξ%,.2f"),
          new Currency("litecoin", "Ł", "litecoin", "litecoins", "Ł%,.2f"),
          new Currency("dogecoin", "Ð", "dogecoin", "dogecoins", "Ð%,.2f"),
          new Currency("monero", "ɱ", "monero", "moneros", "ɱ%,.2f"),
          new Currency("ripple", "Ʀ", "ripple", "ripples", "Ʀ%,.2f"),
          new Currency("dash", "Đ", "dash", "dash", "Đ%,.2f"),
          new Currency("neo", "№", "neo", "neo", "№%,.2f"),
          new Currency("stellar", "⋆", "stellar", "stellar", "⋆%,.2f"),
          new Currency("cardano", "₳", "cardano", "cardano", "₳%,.2f"),
          new Currency("tron", "₮", "tron", "tron", "₮%,.2f"),
          new Currency("tezos", "ꜩ", "tezos", "tezos", "ꜩ%,.2f"),
          new Currency("vechain", "V", "vechain", "vechain", "V%,.2f"),
          new Currency("chainlink", "⛓", "chainlink", "chainlink", "⛓%,.2f"),
          new Currency("polkadot", "⨯", "polkadot", "polkadot", "⨯%,.2f"),
          new Currency("uniswap", "⨷", "uniswap", "uniswap", "⨷%,.2f"),
          new Currency("aave", "⚒", "aave", "aave", "⚒%,.2f"),
          new Currency("maker", "⚖", "maker", "maker", "⚖%,.2f"),
          new Currency("compound", "⚗", "compound", "compound", "⚗%,.2f"),
          new Currency("yearn", "⚙", "yearn", "yearn", "⚙%,.2f"),
          new Currency("sushi", "⚛", "sushi", "sushi", "⚛%,.2f"),
          new Currency("curve", "⚜", "curve", "curve", "⚜%,.2f"),
          new Currency("balancer", "⚖", "balancer", "balancer", "⚖%,.2f"),
          new Currency("ren", "⚡", "ren", "ren", "⚡%,.2f"),
          new Currency("loopring", "⚪", "loopring", "loopring", "⚪%,.2f"),
          new Currency("uma", "⚫", "uma", "uma", "⚫%,.2f"),
          new Currency("band", "⚬", "band", "band", "⚬%,.2f"),
          new Currency("nem", "⚭", "nem", "nem", "⚭%,.2f"),
          new Currency("zilliqa", "⚮", "zilliqa", "zilliqa", "⚮%,.2f"),
          new Currency("mio", "⚯", "mio", "mio", "⚯%,.2f"));

  ConfigItem backgroundItem =
      ConfigItem.builder().material(Material.BLACK_STAINED_GLASS_PANE).name("<!i>").build();

  ConfigItem nextPageItem =
      ConfigItem.builder().material(Material.STONE_BUTTON).name("<!i><gray>Next page").build();

  ConfigItem previousPageItem =
      ConfigItem.builder().material(Material.STONE_BUTTON).name("<!i><gray>Previous Page").build();

  ConfigItem currencyItem =
      ConfigItem.builder()
          .material(Material.PAPER)
          .name("<!i><aqua><currency-singular> <currency-symbol>")
          .build();

  Map<String, Float> payTaxes =
      Map.of(
          "dollar",
          0.05f,
          "euro",
          0.10f,
          "yen",
          0.50f,
          "bitcoin",
          0.90f,
          "ether",
          0.99F,
          "litecoin",
          0.05f,
          "dogecoin",
          1F);
}
