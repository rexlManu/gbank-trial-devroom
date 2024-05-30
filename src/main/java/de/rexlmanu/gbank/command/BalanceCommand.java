package de.rexlmanu.gbank.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.currency.Currency;
import de.rexlmanu.gbank.menu.BalanceView;
import de.rexlmanu.gbank.user.BankUser;
import de.rexlmanu.gbank.utils.TaskScheduler;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Singleton
public class BalanceCommand {
  private final BalanceView balanceView;
  private final TaskScheduler taskScheduler;

  @Command(value = "balance", requiredSender = Player.class)
  @Permission("gbank.command.balance")
  public void showOwnBalance(Player sender, BankUser bankUser) {
    this.taskScheduler.run(() -> this.balanceView.open(sender, bankUser));
  }

  @Command(value = "balance <currency>", requiredSender = Player.class)
  @Permission("gbank.command.balance.currency")
  public void showOwnBalanceCurrency(
      Player player,
      BankUser bankUser,
      @Argument(value = "currency", parserName = "currency") Currency currency) {
    this.balanceView.showBalance(player, bankUser.wallet(currency));
  }

  @Command(value = "balance <currency> <player>")
  @Permission("gbank.command.balance.currency.other")
  public void showOtherBalanceCurrency(
      Player player,
      @Argument(value = "player", parserName = "bank-user") BankUser bankUser,
      @Argument(value = "currency", parserName = "currency") Currency currency) {
    this.balanceView.showBalance(player, bankUser.wallet(currency));
  }
}
