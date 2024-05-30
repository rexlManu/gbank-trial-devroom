package de.rexlmanu.gbank.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.config.ConfigProvider;
import de.rexlmanu.gbank.config.message.MessageConfig;
import de.rexlmanu.gbank.config.message.MessageManager;
import de.rexlmanu.gbank.currency.Currency;
import de.rexlmanu.gbank.currency.CurrencyFormatter;
import de.rexlmanu.gbank.user.BankUser;
import de.rexlmanu.gbank.user.BankUserService;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.slf4j.Logger;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Singleton
public class BankCommand {
  private final BankUserService bankUserService;
  private final MessageManager messageManager;
  private final Logger logger;
  private final ConfigProvider configProvider;

  @Command("bank give <player> <currency> <amount>")
  @Permission("gbank.command.bank.give")
  public void bankGive(
      CommandSender sender,
      @Argument(value = "player", parserName = "bank-user") BankUser bankUser,
      @Argument(value = "currency", parserName = "currency") Currency currency,
      @Argument("amount") double amount) {
    bankUser.wallet(currency).give(amount);
    this.messageManager.send(
        sender,
        MessageConfig::bankGiven,
        Placeholder.unparsed("username", bankUser.username()),
        CurrencyFormatter.placeholder(currency, amount));
  }

  @Command("bank take <player> <currency> <amount>")
  @Permission("gbank.command.bank.take")
  public void bankTake(
      CommandSender sender,
      @Argument(value = "player", parserName = "bank-user") BankUser bankUser,
      @Argument(value = "currency", parserName = "currency") Currency currency,
      @Argument("amount") double amount) {
    bankUser.wallet(currency).take(amount);
    this.messageManager.send(
        sender,
        MessageConfig::bankTaken,
        Placeholder.unparsed("username", bankUser.username()),
        CurrencyFormatter.placeholder(currency, amount));
  }

  @Command("bank set <player> <currency> <amount>")
  @Permission("gbank.command.bank.set")
  public void bankSet(
      CommandSender sender,
      @Argument(value = "player", parserName = "bank-user") BankUser bankUser,
      @Argument(value = "currency", parserName = "currency") Currency currency,
      @Argument("amount") double amount) {
    bankUser.wallet(currency).set(amount);

    this.messageManager.send(
        sender,
        MessageConfig::bankSet,
        Placeholder.unparsed("username", bankUser.username()),
        CurrencyFormatter.placeholder(currency, amount));
  }

  @Command("bank reload")
  @Permission("gbank.command.bank.reload")
  public void reload(CommandSender sender) {
    this.configProvider.reload();
    this.messageManager.send(sender, MessageConfig::pluginReloaded);
  }
}
