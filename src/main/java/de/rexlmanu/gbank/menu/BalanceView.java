package de.rexlmanu.gbank.menu;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.config.ConfigProvider;
import de.rexlmanu.gbank.config.PluginConfig;
import de.rexlmanu.gbank.config.item.ItemFactory;
import de.rexlmanu.gbank.config.message.MessageConfig;
import de.rexlmanu.gbank.config.message.MessageManager;
import de.rexlmanu.gbank.currency.CurrencyFormatter;
import de.rexlmanu.gbank.currency.CurrencyService;
import de.rexlmanu.gbank.user.BankUser;
import de.rexlmanu.gbank.wallet.Wallet;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;
import xyz.xenondevs.invui.window.Window;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BalanceView {
  private final MessageManager messageManager;
  private final ConfigProvider configProvider;
  private final CurrencyService currencyService;
  private final ItemFactory itemFactory;

  public void open(Player player, BankUser bankUser) {
    PluginConfig config = this.configProvider.get(PluginConfig.class);
    PagedGui.Builder<Item> gui =
        PagedGui.items()
            .setStructure("< x 0 0 0 0 0 x >")
            .addIngredient('x', s -> this.itemFactory.create(config.backgroundItem()))
            .addIngredient(
                '<',
                new PageItem(false) {
                  @Override
                  public ItemProvider getItemProvider(PagedGui<?> pagedGui) {
                    return s -> itemFactory.create(config.previousPageItem());
                  }
                })
            .addIngredient(
                '>',
                new PageItem(true) {
                  @Override
                  public ItemProvider getItemProvider(PagedGui<?> pagedGui) {
                    return s -> itemFactory.create(config.nextPageItem());
                  }
                })
            .addIngredient('0', Markers.CONTENT_LIST_SLOT_HORIZONTAL);

    gui.setContent(
        this.currencyService.currencies().stream()
            .map(
                currency ->
                    new SimpleItem(
                        s ->
                            itemFactory.create(
                                config.currencyItem(),
                                Placeholder.parsed("currency-singular", currency.singular()),
                                Placeholder.parsed("currency-symbol", currency.symbol())),
                        click -> {
                          if (click.getClickType().isLeftClick()) {
                            player.closeInventory();
                            this.showBalance(player, bankUser.wallet(currency));
                          }
                        }))
            .map(Item.class::cast)
            .toList());

    Window.single()
        .setTitle(this.messageManager.getComponentWrapper(MessageConfig::balanceMenuTitle))
        .setGui(gui)
        .open(player);
  }

  public void showBalance(Player player, Wallet wallet) {
    this.messageManager.send(
        player,
        MessageConfig::balanceMenuOutput,
        CurrencyFormatter.placeholder(wallet.currency(), wallet.balance()));
  }
}
