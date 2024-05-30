package de.rexlmanu.gbank.storage.json;

import com.google.inject.AbstractModule;
import de.rexlmanu.gbank.notification.NotificationStorage;
import de.rexlmanu.gbank.storage.json.notification.JsonNotificationStorage;
import de.rexlmanu.gbank.storage.json.user.JsonBankUserFactory;
import de.rexlmanu.gbank.storage.json.user.JsonBankUserStorage;
import de.rexlmanu.gbank.user.BankUserFactory;
import de.rexlmanu.gbank.user.BankUserStorage;

public class JsonStorageModule extends AbstractModule {
  @Override
  protected void configure() {
    this.bind(BankUserStorage.class).to(JsonBankUserStorage.class);
    this.bind(BankUserFactory.class).to(JsonBankUserFactory.class);
    this.bind(NotificationStorage.class).to(JsonNotificationStorage.class);
  }
}
