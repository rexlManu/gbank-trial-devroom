package de.rexlmanu.gbank;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.rexlmanu.gbank.api.Economy;
import de.rexlmanu.gbank.command.BalanceCommand;
import de.rexlmanu.gbank.command.BankCommand;
import de.rexlmanu.gbank.command.CommandContainer;
import de.rexlmanu.gbank.command.PayCommand;
import de.rexlmanu.gbank.command.parser.BankUserParser;
import de.rexlmanu.gbank.command.parser.CurrencyParser;
import de.rexlmanu.gbank.config.ConfigProvider;
import de.rexlmanu.gbank.config.PluginConfig;
import de.rexlmanu.gbank.config.message.MessageConfig;
import de.rexlmanu.gbank.currency.CurrencyGiveawayTask;
import de.rexlmanu.gbank.currency.CurrencyService;
import de.rexlmanu.gbank.notification.NotificationJoinDisplayListener;
import de.rexlmanu.gbank.storage.StorageType;
import de.rexlmanu.gbank.storage.json.JsonStorageModule;
import de.rexlmanu.gbank.storage.mysql.MySQLStorageModule;
import de.rexlmanu.gbank.user.BankUser;
import de.rexlmanu.gbank.user.BankUserService;
import de.rexlmanu.gbank.user.BankUserStorage;
import de.rexlmanu.gbank.user.cache.BankUserCache;
import de.rexlmanu.gbank.user.cache.BankUserCacheModule;
import de.rexlmanu.gbank.user.listener.CreateOrUpdateJoiningPlayersListener;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.Getter;
import net.kyori.adventure.identity.Identity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.xenondevs.inventoryaccess.component.i18n.Languages;
import xyz.xenondevs.invui.InvUI;

@Getter
public class GBankPlugin extends JavaPlugin {
  private static final Logger log = LoggerFactory.getLogger(GBankPlugin.class);
  private final ConfigProvider configProvider = new ConfigProvider(this.getDataFolder().toPath());

  @Getter(AccessLevel.NONE)
  private Injector injector;

  @Inject private CommandContainer commandContainer;
  @Inject private BankUserService bankUserService;
  @Inject private CurrencyService currencyService;
  @Inject private BankUserCache bankUserCache;
  @Inject private BankUserStorage bankUserStorage;

  @Override
  public void onEnable() {
    Languages.getInstance().enableServerSideTranslations(false);
    InvUI.getInstance().setPlugin(this);
    this.configProvider.register(PluginConfig.class, "config");
    this.configProvider.register(MessageConfig.class, "messages");
    try {
      this.injector =
          Guice.createInjector(
              new GBankPluginModule(this),
              this.configProvider.get(PluginConfig.class).storage().type().equals(StorageType.JSON)
                  ? new JsonStorageModule()
                  : new MySQLStorageModule(),
              new BankUserCacheModule());
      this.injector.injectMembers(this);
    } catch (Exception e) {
      log.error("Failed to enable plugin", e);
      this.getServer().getPluginManager().disablePlugin(this);
      return;
    }

    this.commandContainer
        .withMessages(this.configProvider.get(MessageConfig.class).commandParseErrors())
        .withParser("bank-user", new BankUserParser(this.bankUserService))
        .withParser("currency", new CurrencyParser(this.currencyService))
        .withContextInjector(
            BankUser.class,
            (context, annotationAccessor) ->
                context
                    .sender()
                    .get(Identity.UUID)
                    .map(uuid -> this.bankUserService.find(uuid))
                    .map(CompletableFuture::join)
                    .orElse(null))
        .withCommands(BalanceCommand.class, BankCommand.class, PayCommand.class);

    this.registerListeners(
        CreateOrUpdateJoiningPlayersListener.class, NotificationJoinDisplayListener.class);

    this.setupServices();

    this.getServer()
        .getScheduler()
        .runTaskTimerAsynchronously(
            this, this.injector.getInstance(CurrencyGiveawayTask.class), 0, 20 * 10);
  }

  private void setupServices() {
    ServicesManager servicesManager = this.getServer().getServicesManager();
    servicesManager.register(
        BankUserService.class, this.bankUserService, this, ServicePriority.Normal);
    servicesManager.register(
        CurrencyService.class, this.currencyService, this, ServicePriority.Normal);
    servicesManager.register(
        Economy.class, this.injector.getInstance(Economy.class), this, ServicePriority.Normal);
  }

  @Override
  public void onDisable() {
    // plugin didn't enable properly
    if (this.injector == null) {
      return;
    }

    this.bankUserCache.handleDisable();
    this.bankUserStorage.handleDisable();
  }

  @SafeVarargs
  private void registerListeners(Class<? extends Listener>... classes) {
    Arrays.stream(classes)
        .forEach(
            clazz ->
                this.getServer()
                    .getPluginManager()
                    .registerEvents(this.injector.getInstance(clazz), this));
  }
}
