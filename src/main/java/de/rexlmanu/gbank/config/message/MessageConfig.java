package de.rexlmanu.gbank.config.message;

import de.exlll.configlib.Configuration;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class MessageConfig {
  String pluginReloaded = "<gray>The plugin was <green>successfully</green> reloaded.";
  String bankGiven =
      "<gray>You have given <green><amount></green> to <green><username></green>'s wallet.";
  String bankTaken =
      "<gray>You have taken <red><amount></red> from <red><username></red>'s wallet.";

  String bankSet =
      "<gray>You have set <yellow><amount></yellow> to <yellow><username></yellow>'s wallet.";
  String commandFailed =
      "<red>Failed to execute the command. Check the console for more information.";

  String invalidAmount = "<red>Invalid amount. Please enter a valid amount.";

  String cannotPaySelf = "<red>You cannot pay yourself.";
  String notEnoughMoney = "<red>You do not have enough money.";
  String sentMoney = "<gray>You have sent <green><amount></green> to <green><username></green>.";
  String balanceMenuTitle = "<dark_gray>ʙᴀʟᴀɴᴄᴇ";
  String balanceMenuOutput = "<gray>Balance: <green><amount>";
  String notificationPaymentReceived =
      "<gray>You have received <green><amount></green> from <aqua><username></aqua>.";
  String currencyGiveawayReceived =
      "<rainbow>Congratulations! You just got <amount> gifted from the server! Spend it wisely!";

  Map<String, String> commandParseErrors =
      Map.of(
          "argument.parse.failure.currency",
          "No currency with name '<input>' could be found.",
          "argument.parse.failure.bankuser",
          "No bank user with name '<input>' could be found.");
}
