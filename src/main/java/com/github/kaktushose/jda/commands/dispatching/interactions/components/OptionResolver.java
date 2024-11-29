package com.github.kaktushose.jda.commands.dispatching.interactions.components;

import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.MenuOptionProviderDefinition;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The OptionResolver class is used to dynamically resolve {@link SelectOption SelectOptions} for
 * {@link com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu StringSelectMenus} that don't use
 * static {@link SelectOption SelectOptions}, normally provided by the
 * {@link com.github.kaktushose.jda.commands.annotations.interactions.MenuOption MenuOption}
 * annotation, and instead use
 * {@link com.github.kaktushose.jda.commands.annotations.interactions.DynamicOptions DynamicOptions}.
 *
 * <p>
 * This class only gets constructed from jda-commands. It must be used as the first and only parameter of a method
 * annotated with
 * {@link com.github.kaktushose.jda.commands.annotations.interactions.MenuOptionProvider MenuOptionProvider}.
 * <br>Example:
 * <pre>
 * {@code
 * @MenuOptionProvider
 * public void onResolveOptions(OptionResolver resolver) {
 *      resolver.add(SelectOption.of("Option 1", "option-1").withDefault(true))
 *              .add("Option 2", "option-2");
 * }
 * }
 * </pre>
 * </p>
 *
 * @see com.github.kaktushose.jda.commands.annotations.interactions.MenuOptionProvider MenuOptionProvider
 * @since 4.0.0
 */
public class OptionResolver {

    private final MenuOptionProviderDefinition definition;
    private Set<SelectOption> selectOptions;

    /**
     * Constructs a new OptionResolver.
     *
     * @param definition the underlying {@link MenuOptionProviderDefinition}
     */
    public OptionResolver(MenuOptionProviderDefinition definition) {
        this.definition = definition;
        selectOptions = new HashSet<>();
    }

    /**
     * Sets all {@link SelectOption SelectOptions}, overriding previous additions.
     *
     * @param selectOptions the {@link SelectOption SelectOptions} to use
     * @return this instance for fluent interface
     */
    public OptionResolver setSelectOptions(Set<SelectOption> selectOptions) {
        this.selectOptions = selectOptions;
        return this;
    }

    /**
     * Gets the underlying {@link MenuOptionProviderDefinition}.
     *
     * @return the underlying {@link MenuOptionProviderDefinition}
     */
    public MenuOptionProviderDefinition getProviderDefinition() {
        return definition;
    }

    /**
     * Gets a possibly-empty Set of all {@link SelectOption SelectOptions}.
     *
     * @return a possibly-empty Set of all {@link SelectOption SelectOptions}
     */
    public Set<SelectOption> getSelectOptions() {
        return selectOptions;
    }

    /**
     * Adds a new {@link SelectOption}.
     *
     * @param label The label for the option, up to {@value SelectOption#LABEL_MAX_LENGTH} characters, as defined by {@link SelectOption#LABEL_MAX_LENGTH}
     * @param value The value for the option used to indicate which option was selected with {@link SelectMenuInteraction#getValues()},
     *              up to {@value SelectOption#VALUE_MAX_LENGTH} characters, as defined by {@link SelectOption#VALUE_MAX_LENGTH}
     * @return this instance for fluent interface
     */
    public OptionResolver add(String label, String value) {
        selectOptions.add(SelectOption.of(label, value));
        return this;
    }

    /**
     * Adds a new {@link SelectOption}.
     *
     * @param selectOption the {@link SelectOption} to add
     * @return this instance for fluent interface
     */
    public OptionResolver add(SelectOption selectOption) {
        selectOptions.add(selectOption);
        return this;
    }

    /**
     * Adds one or more {@link SelectOption SelectOption(s)}.
     *
     * @param selectOption the {@link SelectOption SelectOption(s)} to add
     * @return this instance for fluent interface
     */
    public OptionResolver add(SelectOption... selectOption) {
        selectOptions.addAll(List.of(selectOption));
        return this;
    }

    /**
     * Adds all the elements in the specified collection
     *
     * @param collection collection containing elements to be added
     * @return this instance for fluent interface
     */
    public OptionResolver add(Collection<? extends SelectOption> collection) {
        selectOptions.addAll(collection);
        return this;
    }

    /**
     * Returns the number of {@link SelectOption SelectOptions} already added.
     *
     * @return the number of added {@link SelectOption SelectOptions}
     */
    public int size() {
        return selectOptions.size();
    }

    /**
     * Returns {@code true} if no {@link SelectOption SelectOptions} have been added yet.
     *
     * @return {@code true} if no {@link SelectOption SelectOptions} have been added yet
     */
    public boolean isEmpty() {
        return selectOptions.isEmpty();
    }

    /**
     * Returns {@code true} if the specified {@link SelectOption} has already been added.
     *
     * @param selectOption the {@link SelectOption} to test
     * @return {@code true} if the specified {@link SelectOption} has already been added
     */
    public boolean contains(SelectOption selectOption) {
        return selectOptions.contains(selectOption);
    }

    /**
     * Removes the specified {@link SelectOption} from the Set of {@link SelectOption SelectOptions} that will be
     * attached to the SelectMenu.
     *
     * @param selectOption the {@link SelectOption} to remove
     * @return this instance for fluent interface
     */
    public OptionResolver remove(SelectOption selectOption) {
        selectOptions.remove(selectOption);
        return this;
    }

    /**
     * Returns {@code true} if all the elements of the specified collection have already been added.
     *
     * @param collection collection to be checked for containment
     * @return {@code true} {@code true} if all the elements of the specified collection have already been added
     */
    public boolean containsAll(Collection<SelectOption> collection) {
        return selectOptions.containsAll(collection);
    }

    /**
     * Removes all the elements of the specified collection from the Set of {@link SelectOption SelectOptions} that will
     * be attached to the SelectMenu.
     *
     * @param collection collection to be checked for containment
     * @return {@code true} {@code true} if all the elements of the specified collection have already been added
     */
    public boolean removeAll(Collection<SelectOption> collection) {
        return selectOptions.removeAll(collection);
    }

    /**
     * Removes all {@link SelectOption SelectOptions} from the result
     *
     * @return this instance for fluent interface
     */
    public OptionResolver clear() {
        selectOptions.clear();
        return this;
    }
}
