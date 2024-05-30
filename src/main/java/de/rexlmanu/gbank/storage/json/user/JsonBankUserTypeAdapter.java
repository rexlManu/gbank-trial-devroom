package de.rexlmanu.gbank.storage.json.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.currency.Currency;
import de.rexlmanu.gbank.currency.CurrencyNotFoundException;
import de.rexlmanu.gbank.currency.CurrencyService;
import de.rexlmanu.gbank.wallet.Wallet;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class JsonBankUserTypeAdapter
    implements JsonSerializer<JsonBankUser>, JsonDeserializer<JsonBankUser> {
  private final CurrencyService currencyService;
  private final Logger logger;

  @Override
  public JsonBankUser deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject object = json.getAsJsonObject();
    String username = object.get("username").getAsString();
    UUID uniqueId = UUID.fromString(object.get("uniqueId").getAsString());
    LocalDateTime createdAt = LocalDateTime.parse(object.get("createdAt").getAsString());
    JsonBankUser bankUser = new JsonBankUser(username, uniqueId, createdAt);
    JsonArray wallets = object.getAsJsonArray("wallets");
    for (JsonElement wallet : wallets) {
      JsonObject walletObject = wallet.getAsJsonObject();
      try {
        Currency currency = currencyService.currency(walletObject.get("currency").getAsString());
        bankUser.addWallet(
            new Wallet(currency, walletObject.get("balance").getAsDouble()));
      } catch (CurrencyNotFoundException e) {
        logger.warn("Currency not found: " + walletObject.get("currency").getAsString());
      }
    }
    return bankUser;
  }

  @Override
  public JsonElement serialize(JsonBankUser src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    object.addProperty("username", src.username());
    object.addProperty("uniqueId", src.uniqueId().toString());
    object.addProperty("createdAt", src.createdAt().toString());
    JsonArray array = new JsonArray();
    for (Wallet wallet : src.wallets()) {
      JsonObject walletObject = new JsonObject();
      walletObject.addProperty("currency", wallet.currency().name());
      walletObject.addProperty("balance", wallet.balance());
      array.add(walletObject);
    }
    object.add("wallets", array);
    return object;
  }
}
