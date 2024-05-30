package de.rexlmanu.gbank.storage.json.user;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.rexlmanu.gbank.user.BankUser;
import de.rexlmanu.gbank.user.BankUserStorage;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.slf4j.Logger;

@Singleton
public class JsonBankUserStorage implements BankUserStorage {
  private static final Type JSON_USER_TYPE = new TypeToken<JsonBankUser>() {}.getType();
  private final Path directory;
  private final Logger logger;
  private final Gson gson;

  @Inject
  public JsonBankUserStorage(
      @Named("data") Path dataDirectory, Logger logger, JsonBankUserTypeAdapter adapter) {
    this.directory = dataDirectory.resolve("users");
    this.logger = logger;
    this.gson =
        new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(JsonBankUser.class, adapter)
            .create();

    try {
      Files.createDirectories(this.directory);
    } catch (IOException e) {
      this.logger.error("Failed to create user directory.", e);
    }
  }

  @Override
  public Optional<UUID> findByUsername(String username) {
    return this.all().stream().filter(user -> user.username().equals(username))
        .map(BankUser::uniqueId)
        .findFirst();
  }

  @Override
  public Optional<BankUser> findByUniqueId(UUID uniqueId) {
    Path path = this.userPath(uniqueId);

    if (!Files.exists(path)) {
      return Optional.empty();
    }

    try {
      return Optional.of(this.deserializeUser(Files.readString(path)));
    } catch (IOException e) {
      this.logger.error("Failed to read user.", e);
      return Optional.empty();
    }
  }

  @Override
  public boolean delete(BankUser user) {
    Path path = this.userPath(user);
    try {
      return Files.deleteIfExists(path);
    } catch (IOException e) {
      this.logger.error("Failed to delete user.", e);
      return false;
    }
  }

  @Override
  public Collection<BankUser> all() {
    try (Stream<Path> paths = Files.list(this.directory)) {
      return paths
          .map(
              path -> {
                try {
                  return this.deserializeUser(Files.readString(path));
                } catch (IOException e) {
                  this.logger.error("Failed to read user.", e);
                  return null;
                }
              })
          .filter(Objects::nonNull)
          .toList();
    } catch (IOException e) {
      this.logger.error("Failed to list users.", e);
      return List.of();
    }
  }

  @Override
  public void update(BankUser user) {
    try {
      Files.writeString(this.userPath(user), this.serializeUser(user));
    } catch (IOException e) {
      this.logger.error("Failed to update user.", e);
    }
  }

  @Override
  public void insert(BankUser user) {
    this.update(user);
  }

  private String serializeUser(BankUser bankUser) {
    return this.gson.toJson(bankUser, JSON_USER_TYPE);
  }

  private BankUser deserializeUser(String json) {
    return this.gson.fromJson(json, JSON_USER_TYPE);
  }

  private Path userPath(UUID uniqueId) {
    return this.directory.resolve(uniqueId + ".json");
  }

  private Path userPath(BankUser user) {
    return this.userPath(user.uniqueId());
  }
}
