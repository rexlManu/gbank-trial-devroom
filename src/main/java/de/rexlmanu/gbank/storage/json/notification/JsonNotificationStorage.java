package de.rexlmanu.gbank.storage.json.notification;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.rexlmanu.gbank.notification.Notification;
import de.rexlmanu.gbank.notification.NotificationStorage;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;

@Singleton
public class JsonNotificationStorage implements NotificationStorage {
  record UserNotificationStore(List<Notification> notifications) {}

  private static final Type JSON_NOTIFCATION_TYPE =
      new TypeToken<UserNotificationStore>() {}.getType();
  private final Path directory;
  private final Logger logger;
  private final Gson gson;

  @Inject
  public JsonNotificationStorage(@Named("data") Path dataDirectory, Logger logger) {
    this.directory = dataDirectory.resolve("notifications");
    this.logger = logger;
    this.gson =
        new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Notification.class, new JsonNotificationTypeAdapter())
            .create();

    try {
      Files.createDirectories(this.directory);
    } catch (IOException e) {
      this.logger.error("Failed to create user directory.", e);
    }
  }

  @Override
  public void insert(Notification notification) {
    UserNotificationStore store = this.getOrCreate(notification.receiverId());
    store.notifications().add(notification);
    this.save(notification.receiverId(), store);
  }

  @Override
  public List<Notification> retrieveAll(UUID receiverId, boolean deleteAfterRetrieve) {
    UserNotificationStore store = this.getOrCreate(receiverId);
    if (deleteAfterRetrieve) {
      this.save(receiverId, new UserNotificationStore(new ArrayList<>()));
    }
    return store.notifications();
  }

  private void save(UUID uniqueId, UserNotificationStore store) {
    Path path = this.userPath(uniqueId);
    try {
      Files.writeString(path, gson.toJson(store));
    } catch (IOException e) {
      this.logger.error("Failed to write notification file.", e);
    }
  }

  private UserNotificationStore getOrCreate(UUID uniqueId) {
    Path path = this.userPath(uniqueId);
    if (!Files.exists(path)) {
      return new UserNotificationStore(new ArrayList<>());
    }

    try {
      return gson.fromJson(Files.readString(path), JSON_NOTIFCATION_TYPE);
    } catch (IOException e) {
      this.logger.error("Failed to read notification file.", e);
      return new UserNotificationStore(new ArrayList<>());
    }
  }

  private Path userPath(UUID uniqueId) {
    return this.directory.resolve(uniqueId + ".json");
  }
}
