package de.rexlmanu.gbank.notification;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class NotificationJoinDisplayListener implements Listener {
  private final NotificationService notificationService;

  @EventHandler
  public void handleJoin(PlayerJoinEvent event) {
    List<Notification> notifications =
        this.notificationService.retrieve(event.getPlayer().getUniqueId());

    if (notifications.isEmpty()) return;

    notifications.forEach(this.notificationService::sendNotification);
  }
}
