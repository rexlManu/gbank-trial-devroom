package de.rexlmanu.gbank.wallet;

import de.rexlmanu.gbank.currency.Currency;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Wallet {
  private final Currency currency;
  @Setter private double balance;

  public void give(double amount) {
    this.balance += amount;
  }

  public void take(double amount) {
    this.balance -= amount;
  }

  public void set(double amount) {
    this.balance = amount;
  }

  public boolean has(double amount) {
    return this.balance >= amount;
  }

  public boolean transfer(Wallet wallet, double amount) {
    if (!this.has(amount)) return false;
    this.take(amount);
    wallet.give(amount);
    return true;
  }

  public boolean transfer(Wallet wallet, double amount, double tax) {
    if (!this.has(amount)) return false;
    this.take(amount);
    wallet.give(amount - tax);
    return true;
  }

  public boolean withdraw(double amount) {
    if (!this.has(amount)) return false;
    this.take(amount);
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;

    Wallet wallet = (Wallet) o;
    return Objects.equals(this.currency, wallet.currency);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.currency);
  }
}
