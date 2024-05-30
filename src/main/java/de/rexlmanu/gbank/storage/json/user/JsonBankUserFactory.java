package de.rexlmanu.gbank.storage.json.user;

import com.google.inject.Singleton;
import de.rexlmanu.gbank.user.BankUser;
import de.rexlmanu.gbank.user.BankUserFactory;
import java.time.LocalDateTime;
import java.util.UUID;

@Singleton
public class JsonBankUserFactory implements BankUserFactory {
  @Override
  public BankUser make(UUID uniqueId, String username) {
    return new JsonBankUser(username, uniqueId, LocalDateTime.now());
  }
}
