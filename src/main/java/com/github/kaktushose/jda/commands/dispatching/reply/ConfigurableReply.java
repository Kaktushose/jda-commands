package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.reply.components.Buttons;
import com.github.kaktushose.jda.commands.dispatching.reply.components.Component;
import com.github.kaktushose.jda.commands.dispatching.reply.components.SelectMenus;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.components.ButtonDefinition;
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
            switch (component) {
                case Buttons buttons -> buttons.buttonContainers().forEach(container -> {
                    var definition = interactionRegistry.find(ButtonDefinition.class,
                            it -> it.getMethod().getName().equals(container.name())
                    );
                    var button = definition.toButton().withDisabled(!container.enabled());
                    //only assign ids to non-link buttons
                    items.add(button.getUrl() == null ? button.withId(definition.createCustomId(runtime.id())) : button);
                });

                case SelectMenus selectMenus -> selectMenus.selectMenuContainers().stream().map(container ->
                        interactionRegistry.find(GenericSelectMenuDefinition.class,
                                it -> it.getMethod().getName().startsWith(container.name())
                        ).toSelectMenu(runtime.id(), container.enabled())
                ).forEach(items::add);
            }

        }
        if (!items.isEmpty()) {
            builder.addComponents(ActionRow.of(items));
        }

        return new ComponentReply(this);
    }

    /**
     * Adds an {@link ActionRow} to the reply and adds the passed {@link Component Components} to it.
     * The buttonContainers must be defined in the same
     * {@link com.github.kaktushose.jda.commands.annotations.interactions.Interaction Interaction} as the referring
     * {@link SlashCommand Command}. This will enable all buttonContainers. To add
     * disabled buttonContainers, use {@link #components(Component...)}.
     *
     * @param buttons the id of the buttonContainers to add
     * @return the current instance for fluent interface
     */
    public ComponentReply buttons(@NotNull String... buttons) {
        return components(Buttons.enabled(buttons));
    }

    /**
     * Adds an {@link ActionRow} to the reply and adds the passed {@link Component Components} to it.
     * The select menus must be defined in the same
     * {@link com.github.kaktushose.jda.commands.annotations.interactions.Interaction Interaction} as the referring
     * {@link SlashCommand Command}. This will enable all select menus. To add
     * disabled select menus, use {@link #components(Component...)}.
     *
     * @param selectMenus the id of the selectMenus to add
     * @return the current instance for fluent interface
     */
    public ComponentReply selectMenus(@NotNull String... selectMenus) {
        return components(SelectMenus.enabled(selectMenus));
    }
}

