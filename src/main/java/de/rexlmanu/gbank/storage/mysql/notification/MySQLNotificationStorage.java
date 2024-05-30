package de.rexlmanu.gbank.storage.mysql.notification;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.notification.Notification;
import de.rexlmanu.gbank.notification.NotificationStorage;
import de.rexlmanu.gbank.storage.mysql.DatabaseConnection;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MySQLNotificationStorage implements NotificationStorage {
  private final DatabaseConnection databaseConnection;

  @Override
  public void insert(Notification notification) {
    this.databaseConnection
        .newBuilder("INSERT INTO notifications (unique_id, message, created_at) VALUES (?, ?, ?)")
        .appends(
            notification.receiverId().toString(),
            GsonComponentSerializer.gson().serialize(notification.message()),
            Timestamp.from(notification.createdAt().toInstant(ZoneOffset.UTC)))
        .execute();
  }

  @Override
  public List<Notification> retrieveAll(UUID receiverId, boolean deleteAfterRetrieve) {
    List<Notification> notifications =
        this.databaseConnection
            .newBuilder("SELECT * FROM notifications WHERE unique_id = ?")
            .append(receiverId.toString())
            .queryAll(
                resultSet ->
                    new Notification(
                        receiverId,
                        GsonComponentSerializer.gson().deserialize(resultSet.getString("message")),
                        resultSet.getTimestamp("created_at").toLocalDateTime()))
            .orElse(List.of());

    if (deleteAfterRetrieve && !notifications.isEmpty()) {
      this.databaseConnection
          .newBuilder("DELETE FROM notifications WHERE unique_id = ?")
          .append(receiverId.toString())
          .execute();
    }

    return notifications;
  }
}
