package de.rexlmanu.gbank.notification;

import java.util.List;
import java.util.UUID;

public interface NotificationStorage {
  void insert(Notification notification);

  List<Notification> retrieveAll(UUID receiverId, boolean deleteAfterRetrieve);
}
