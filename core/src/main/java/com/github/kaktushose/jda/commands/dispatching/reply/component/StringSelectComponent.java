package com.github.kaktushose.jda.commands.dispatching.reply.component;

import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public final class StringSelectComponent extends SelectMenuComponent<StringSelectComponent, StringSelectMenu, StringSelectMenuDefinition> {

    public StringSelectComponent(String method, Class<?> origin) {
        super(method, origin);
    }

    @Override
    public Class<StringSelectMenuDefinition> definitionClass() {
        return StringSelectMenuDefinition.class;
    }

    @Override
    public StringSelectMenuDefinition build(StringSelectMenuDefinition definition) {
        return definition;
    }
}
