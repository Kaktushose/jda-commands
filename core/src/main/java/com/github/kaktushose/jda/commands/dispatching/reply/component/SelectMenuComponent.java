package com.github.kaktushose.jda.commands.dispatching.reply.component;

import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.SelectMenuDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.Component;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

public sealed abstract class SelectMenuComponent<Self extends SelectMenuComponent<Self, T, D>, T extends SelectMenu, D extends SelectMenuDefinition<T>> extends Component<Self, T, D> permits EntitySelectMenuComponent, StringSelectComponent {

    public SelectMenuComponent(String method, Class<?> origin) {
        super(method, origin);
    }
}
