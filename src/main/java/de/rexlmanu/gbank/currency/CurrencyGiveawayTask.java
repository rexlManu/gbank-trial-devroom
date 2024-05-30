package de.rexlmanu.gbank.currency;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.config.message.MessageConfig;
import de.rexlmanu.gbank.config.message.MessageManager;
import de.rexlmanu.gbank.user.BankUserService;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CurrencyGiveawayTask implements Runnable {
  private final Server server;
  private final BankUserService bankUserService;
  private final CurrencyService currencyService;
  private final MessageManager messageManager;

  @Override
  public void run() {
    final Currency currency = this.currencyService.random();
    final double amount = Math.random() * 1000;
    for (Player onlinePlayer : server.getOnlinePlayers()) {
      this.bankUserService
          .find(onlinePlayer.getUniqueId())
          .thenAccept(
              bankUser -> {
                bankUser.wallet(currency).give(amount);
                this.messageManager.send(
                    onlinePlayer,
                    MessageConfig::currencyGiveawayReceived,
                    CurrencyFormatter.placeholder(currency, amount));
              });
    }
  }
}
