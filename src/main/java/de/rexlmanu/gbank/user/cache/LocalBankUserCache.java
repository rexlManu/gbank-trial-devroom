package de.rexlmanu.gbank.user.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.user.BankUser;
import de.rexlmanu.gbank.user.BankUserNotFoundException;
import de.rexlmanu.gbank.user.BankUserStorage;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class LocalBankUserCache implements BankUserCache, RemovalListener<UUID, BankUser> {
  private final Cache<UUID, BankUser> storedUsers =
      CacheBuilder.newBuilder()
          .expireAfterAccess(10, TimeUnit.MINUTES)
          .removalListener(this)
          .build();
  private final BankUserStorage bankUserStorage;

  @Override
  public void cache(BankUser bankUser) {
    this.storedUsers.put(bankUser.uniqueId(), bankUser);
  }

  @Override
  public BankUser load(UUID identifier) {
    try {
      return this.storedUsers.get(
          identifier,
          () ->
              this.bankUserStorage
                  .findByUniqueId(identifier)
                  .orElseThrow(BankUserNotFoundException::new));
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void invalidate(UUID identifier) {
    this.storedUsers.invalidate(identifier);
  }

  @Override
  public void handleDisable() {
    this.storedUsers.asMap().values().forEach(this.bankUserStorage::update);
    this.storedUsers.invalidateAll();
  }

  @Override
  public void onRemoval(RemovalNotification<UUID, BankUser> notification) {
    if (notification.wasEvicted()) {
      this.bankUserStorage.update(notification.getValue());
    }
  }
}
