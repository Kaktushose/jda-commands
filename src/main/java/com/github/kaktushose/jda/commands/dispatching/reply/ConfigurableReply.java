package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.components.ButtonDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.GenericComponentDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.GenericSelectMenuDefinition;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public sealed class ConfigurableReply extends MessageReply permits ComponentReply {

    protected final InteractionRegistry interactionRegistry;
    protected final Runtime runtime;

    public ConfigurableReply(MessageReply reply, InteractionRegistry registry, Runtime runtime) {
        super(reply);
        this.interactionRegistry = registry;
        this.runtime = runtime;
    }

    public ConfigurableReply(ConfigurableReply reply) {
        super(reply);
        this.interactionRegistry = reply.interactionRegistry;
        this.runtime = reply.runtime;
    }

    /**
     * Whether to send ephemeral replies.
     *
     * @param ephemeral {@code true} if replies should be ephemeral
     * @return the current instance for fluent interface
     */
    public ConfigurableReply ephemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
        return this;
    }

    /**
     * Whether this reply should edit the existing message or send a new one. The default value is
     * {@code true}.
     *
     * @param editReply {@code true} if this reply should edit the existing message
     * @return the current instance for fluent interface
     */
    public ConfigurableReply editReply(boolean editReply) {
        this.editReply = editReply;
        return this;
    }

    /**
     * Whether this reply should keep all components that are attached to the previous message. The default value is
     * {@code true}.
     *
     * @param keepComponents {@code true} if this reply should keep all components
     * @return the current instance for fluent interface
     */
    public ConfigurableReply keepComponents(boolean keepComponents) {
        this.keepComponents = keepComponents;
        return this;
    }

    /**
     * Adds an {@link ActionRow} to the reply and adds the passed {@link Component Components} to it.
     * For buttonContainers, they must be defined in the same
     * {@link com.github.kaktushose.jda.commands.annotations.interactions.Interaction Interaction} as the referring
     * {@link SlashCommand Command}.
     *
     * @param components the {@link Component Components} to add
     * @return the current instance for fluent interface
     */
    public ComponentReply components(@NotNull Component... components) {
        List<ItemComponent> items = new ArrayList<>();
        for (Component component : components) {

            var definition = interactionRegistry.find(GenericComponentDefinition.class, false, it ->
                    it.getMethod().getName().equals(component.name())
            );

            switch (definition) {
                case ButtonDefinition buttonDefinition -> {
                    var button = buttonDefinition.toButton().withDisabled(!component.enabled());
                    //only assign ids to non-link buttons
                    items.add(button.getUrl() == null ? button.withId(createId(definition, component.staticComponent())) : button);
                }
                case GenericSelectMenuDefinition<?> menuDefinition ->
                        items.add(menuDefinition.toSelectMenu(createId(definition, component.staticComponent()), component.enabled()));
            }
        }
        if (!items.isEmpty()) {
            builder.addComponents(ActionRow.of(items));
        }

        return new ComponentReply(this);
    }

    private String createId(GenericComponentDefinition definition, boolean staticComponent) {
        return staticComponent
                ? definition.staticCustomId()
                : definition.scopedCustomId(runtime.id());
    }
}

