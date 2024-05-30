package de.rexlmanu.gbank.user;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface BankUserStorage {

  Optional<UUID> findByUsername(String username);

  Optional<BankUser> findByUniqueId(UUID uniqueId);

  boolean delete(BankUser user);

  Collection<BankUser> all();

  void update(BankUser user);

  void insert(BankUser user);

  default void handleDisable() {

  }
}
