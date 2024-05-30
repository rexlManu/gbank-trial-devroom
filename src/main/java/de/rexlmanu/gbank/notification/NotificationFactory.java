package de.rexlmanu.gbank.notification;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.config.message.MessageConfig;
import de.rexlmanu.gbank.config.message.MessageManager;
import de.rexlmanu.gbank.currency.Currency;
import de.rexlmanu.gbank.currency.CurrencyFormatter;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class NotificationFactory {
  private final MessageManager messageManager;

  public Notification paymentReceived(
      UUID receiverId, double amount, Currency currency, String senderName) {
    return new Notification(
        receiverId,
        this.messageManager.getComponent(
            MessageConfig::notificationPaymentReceived,
            CurrencyFormatter.placeholder(currency, amount),
            Placeholder.unparsed("username", senderName)),
        LocalDateTime.now());
  }
}
