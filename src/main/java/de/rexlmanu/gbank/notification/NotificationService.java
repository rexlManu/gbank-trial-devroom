package de.rexlmanu.gbank.notification;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class NotificationService {

  private final NotificationStorage notificationStorage;
  private final Server server;

  public void queue(Notification notification) {
    if (this.sendNotification(notification)) return;
    this.notificationStorage.insert(notification);
  }

  public List<Notification> retrieve(UUID receiverId) {
    return this.notificationStorage.retrieveAll(receiverId, true);
  }

  public boolean sendNotification(Notification notification) {
    Player player = this.server.getPlayer(notification.receiverId());
    if (player == null) return false;

    player.sendMessage(notification.message());
    return true;
  }
}
