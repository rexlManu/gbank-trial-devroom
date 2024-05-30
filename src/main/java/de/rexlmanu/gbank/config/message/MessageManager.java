package de.rexlmanu.gbank.config.message;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.gbank.config.ConfigProvider;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Singleton
public class MessageManager {
  private final ConfigProvider configProvider;
  private final MiniMessage miniMessage;

  public void send(
      Audience audience, Function<MessageConfig, String> messageMapper, TagResolver... resolvers) {
    Component component = this.getComponent(messageMapper, resolvers);

    audience.sendMessage(component);
  }

  public Component getComponent(
      Function<MessageConfig, String> messageMapper, TagResolver... resolvers) {
    return this.miniMessage.deserialize(
        messageMapper.apply(this.configProvider.get(MessageConfig.class)), resolvers);
  }

  public ComponentWrapper getComponentWrapper(
      Function<MessageConfig, String> messageMapper, TagResolver... resolvers) {
    return new AdventureComponentWrapper(this.getComponent(messageMapper, resolvers));
  }

  public List<ComponentWrapper> getComponentWrappers(
      Function<MessageConfig, List<String>> messageMapper, TagResolver... resolvers) {
    return messageMapper.apply(this.configProvider.get(MessageConfig.class)).stream()
        .map(
            message ->
                new AdventureComponentWrapper(this.miniMessage.deserialize(message, resolvers)))
        .map(ComponentWrapper.class::cast)
        .toList();
  }
}
