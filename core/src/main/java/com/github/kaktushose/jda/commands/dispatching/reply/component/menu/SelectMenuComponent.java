package com.github.kaktushose.jda.commands.dispatching.reply.component.menu;

import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.SelectMenuDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.Component;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

public sealed abstract class SelectMenuComponent<Self extends SelectMenuComponent<Self, T, D>, T extends SelectMenu, D extends SelectMenuDefinition<T>> extends Component<Self, T, D> permits StringSelectComponent, EntitySelectMenuComponent {

    protected String placeholder;

    // wrapper types for nullability (null -> not set)
    protected Integer minValues;
    protected Integer maxValues;

    public SelectMenuComponent(String method, Class<?> origin) {
        super(method, origin);
    }

    public Self placeholder(String placeholder) {
        this.placeholder = placeholder;
        return self();
    }

    public Self minValue(int minValue) {
        this.minValues = minValue;
        return self();
    }

    public Self maxValue(int maxValue) {
        this.maxValues = maxValue;
        return self();
    }

    public Self requiresRange(int min, int max) {
        minValue(min);
        maxValue(max);
        return self();
    }
}
