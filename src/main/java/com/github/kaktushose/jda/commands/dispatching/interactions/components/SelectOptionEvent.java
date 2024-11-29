package com.github.kaktushose.jda.commands.dispatching.interactions.components;

import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.MenuOptionProviderDefinition;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

import java.util.HashSet;
import java.util.Set;

public class SelectOptionEvent {

    private final MenuOptionProviderDefinition definition;
    private Set<SelectOption> selectOptions;

    public SelectOptionEvent(MenuOptionProviderDefinition definition) {
        this.definition = definition;
        selectOptions = new HashSet<>();
    }

    public Set<SelectOption> getSelectOptions() {
        return selectOptions;
    }

    public void setSelectOptions(Set<SelectOption> selectOptions) {
        this.selectOptions = selectOptions;
    }

    public MenuOptionProviderDefinition getProviderDefinition() {
        return definition;
    }
}
