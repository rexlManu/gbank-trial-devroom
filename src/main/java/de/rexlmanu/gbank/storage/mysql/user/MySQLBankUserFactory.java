package de.rexlmanu.gbank.storage.mysql.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.user.BankUser;
import de.rexlmanu.gbank.user.BankUserFactory;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MySQLBankUserFactory implements BankUserFactory {
  @Override
  public BankUser make(UUID uniqueId, String username) {
    return new MySQLBankUser(-1, username, uniqueId, LocalDateTime.now());
  }
}
