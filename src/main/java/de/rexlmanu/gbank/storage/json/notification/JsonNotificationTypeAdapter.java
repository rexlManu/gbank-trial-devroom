package de.rexlmanu.gbank.storage.json.notification;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.rexlmanu.gbank.notification.Notification;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class JsonNotificationTypeAdapter
    implements JsonSerializer<Notification>, JsonDeserializer<Notification> {
  @Override
  public Notification deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject object = json.getAsJsonObject();
    UUID receiverId = UUID.fromString(object.get("receiverId").getAsString());
    LocalDateTime createdAt = LocalDateTime.parse(object.get("createdAt").getAsString());
    Component message = GsonComponentSerializer.gson().deserializeFromTree(object.get("message"));
    return new Notification(receiverId, message, createdAt);
  }

  @Override
  public JsonElement serialize(Notification src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    object.addProperty("receiverId", src.receiverId().toString());
    object.addProperty("createdAt", src.createdAt().toString());
    object.add("message", GsonComponentSerializer.gson().serializeToTree(src.message()));
    return object;
  }
}
