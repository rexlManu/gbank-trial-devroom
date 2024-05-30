package de.rexlmanu.gbank.user;

import de.rexlmanu.gbank.wallet.Wallet;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BankUserFactory {
  BankUser make(UUID uniqueId, String username);
}
