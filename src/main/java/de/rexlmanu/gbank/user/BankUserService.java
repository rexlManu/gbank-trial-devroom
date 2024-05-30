package de.rexlmanu.gbank.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.user.cache.BankUserCache;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BankUserService {
  private final BankUserStorage storage;
  private final BankUserCache cache;

  public CompletableFuture<BankUser> find(UUID uniqueId) {
    return CompletableFuture.supplyAsync(() -> this.cache.load(uniqueId));
  }

  public CompletableFuture<BankUser> find(String username) {
    return CompletableFuture.supplyAsync(
            () -> this.storage.findByUsername(username).orElseThrow(BankUserNotFoundException::new))
        .thenCompose(this::find);
  }

  public CompletableFuture<Void> update(BankUser user) {
    return CompletableFuture.runAsync(() -> this.storage.update(user))
        .thenRun(() -> this.cache.cache(user));
  }

  public CompletableFuture<List<String>> usernames() {
    return CompletableFuture.supplyAsync(
        () -> this.storage.all().stream().map(BankUser::username).toList());
  }

  public CompletableFuture<Void> create(BankUser user) {
    return CompletableFuture.runAsync(() -> this.storage.insert(user));
  }
}
