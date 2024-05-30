package de.rexlmanu.gbank.user.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.user.BankUser;
import de.rexlmanu.gbank.user.BankUserFactory;
import de.rexlmanu.gbank.user.BankUserNotFoundException;
import de.rexlmanu.gbank.user.BankUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.slf4j.Logger;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CreateOrUpdateJoiningPlayersListener implements Listener {
  private final BankUserService bankUserService;
  private final BankUserFactory bankUserFactory;
  private final Logger logger;

  @EventHandler
  public void handleJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    this.bankUserService
        .find(player.getUniqueId())
        .whenComplete(
            (bankUser, throwable) -> {
              if (ExceptionUtils.indexOfThrowable(throwable, BankUserNotFoundException.class)
                  != -1) {
                BankUser user = this.bankUserFactory.make(player.getUniqueId(), player.getName());

                this.bankUserService
                    .create(user)
                    .thenRun(
                        () ->
                            this.logger.info(
                                "Created a new bank user for the player {}", player.getName()))
                    .exceptionally(
                        throwable1 -> {
                          this.logger.error(
                              "Failed to create a new bank user for the player {}",
                              player.getName(),
                              throwable1);
                          return null;
                        });
                return;
              }

              if (throwable != null) {
                logger.error(
                    "Failed to find the bank user for the player {}", player.getName(), throwable);
                return;
              }

              bankUser.username(player.getName());
              this.bankUserService
                  .update(bankUser)
                  .thenRun(
                      () ->
                          this.logger.info(
                              "Updated the bank user for the player {}", player.getName()))
                  .exceptionally(
                      throwable1 -> {
                        this.logger.error(
                            "Failed to update the bank user for the player {}",
                            player.getName(),
                            throwable1);
                        return null;
                      });
            });
  }
}
