package de.rexlmanu.gbank.api;

import com.google.inject.AbstractModule;

public class ApiModule extends AbstractModule {
  @Override
  protected void configure() {
    this.bind(Economy.class).to(BankEconomy.class);
  }
}
