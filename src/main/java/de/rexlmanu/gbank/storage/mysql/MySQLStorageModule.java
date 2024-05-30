package de.rexlmanu.gbank.storage.mysql;

import com.google.inject.AbstractModule;
import de.rexlmanu.gbank.notification.NotificationStorage;
import de.rexlmanu.gbank.storage.mysql.notification.MySQLNotificationStorage;
import de.rexlmanu.gbank.storage.mysql.user.MySQLBankUserFactory;
import de.rexlmanu.gbank.storage.mysql.user.MySQLBankUserStorage;
import de.rexlmanu.gbank.user.BankUserFactory;
import de.rexlmanu.gbank.user.BankUserStorage;

public class MySQLStorageModule extends AbstractModule {
  @Override
  protected void configure() {
    this.bind(NotificationStorage.class).to(MySQLNotificationStorage.class);
    this.bind(BankUserStorage.class).to(MySQLBankUserStorage.class);
    this.bind(BankUserFactory.class).to(MySQLBankUserFactory.class);
  }
}
