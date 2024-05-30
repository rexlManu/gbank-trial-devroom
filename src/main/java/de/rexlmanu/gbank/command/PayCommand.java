package de.rexlmanu.gbank.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.config.message.MessageConfig;
import de.rexlmanu.gbank.config.message.MessageManager;
import de.rexlmanu.gbank.currency.Currency;
import de.rexlmanu.gbank.currency.CurrencyFormatter;
import de.rexlmanu.gbank.notification.NotificationFactory;
import de.rexlmanu.gbank.notification.NotificationService;
import de.rexlmanu.gbank.user.BankUser;
import de.rexlmanu.gbank.user.BankUserService;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.slf4j.Logger;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Singleton
public class PayCommand {
  private final BankUserService bankUserService;
  private final MessageManager messageManager;
  private final Logger logger;
  private final NotificationService notificationService;
  private final NotificationFactory notificationFactory;

  @Command(value = "pay <player> <currency> <amount>", requiredSender = Player.class)
  @Permission("gbank.command.pay")
  public void pay(
      Player sender,
      BankUser senderUser,
      @Argument(value = "player", parserName = "bank-user") BankUser target,
      @Argument(value = "currency", parserName = "currency") Currency currency,
      @Argument("amount") double amount) {
    amount = sanitize(amount);

    if (senderUser.uniqueId().equals(target.uniqueId())) {
      this.messageManager.send(sender, MessageConfig::cannotPaySelf);
      return;
    }

    if (amount <= 0) {
      this.messageManager.send(sender, MessageConfig::invalidAmount);
      return;
    }

    if (!senderUser.wallet(currency).transfer(target.wallet(currency), amount)) {
      this.messageManager.send(sender, MessageConfig::notEnoughMoney);
      return;
    }

    this.messageManager.send(
        sender,
        MessageConfig::sentMoney,
        CurrencyFormatter.placeholder(currency, amount),
        Placeholder.unparsed("username", target.username()));

    this.notificationService.queue(
        this.notificationFactory.paymentReceived(
            target.uniqueId(), amount, currency, senderUser.username()));
  }

  // limit amount to 2 decimal places
  private static double sanitize(double amount) {
    return Math.round(amount * 100.0) / 100.0;
  }
}
