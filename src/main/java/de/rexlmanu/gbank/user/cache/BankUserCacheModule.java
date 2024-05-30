package de.rexlmanu.gbank.user.cache;

import com.google.inject.AbstractModule;

public class BankUserCacheModule extends AbstractModule {
  @Override
  protected void configure() {
    this.bind(BankUserCache.class).to(LocalBankUserCache.class);
  }
}
