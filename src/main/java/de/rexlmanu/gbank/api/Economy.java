package de.rexlmanu.gbank.api;

import java.util.UUID;

public interface Economy {
  /**
   * Get the balance of the player in the given currency.
   *
   * @param playerId The player's UUID
   * @param currencyName The currency name
   * @return The balance of the player in the given currency
   */
  double getBalance(UUID playerId, String currencyName);
}
