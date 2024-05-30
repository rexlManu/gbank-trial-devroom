package de.rexlmanu.gbank.user.cache;

import de.rexlmanu.gbank.user.BankUser;
import java.util.UUID;

public interface BankUserCache {
  void cache(BankUser bankUser);

  BankUser load(UUID identifier);

  void invalidate(UUID identifier);

  void handleDisable();
}
