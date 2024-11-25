package com.github.kaktushose.jda.commands.dispatching.interactions.components;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericEvent;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.SelectOptionProviderDefinition;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

import java.util.HashSet;
import java.util.Set;

public class SelectOptionEvent extends GenericEvent<SelectOptionProviderDefinition> {

    private Set<SelectOption> selectOptions;

    /**
     * Constructs a new SelectOptionEvent.
     *
     * @param context the underlying {@link Context}
     */
    protected SelectOptionEvent(Context context) {
        super(context);
        selectOptions = new HashSet<>();
    }

    public Set<SelectOption> getSelectOptions() {
        return selectOptions;
    }

    public void setSelectOptions(Set<SelectOption> selectOptions) {
        this.selectOptions = selectOptions;
    }
}
