package com.github.kaktushose.jda.commands.definitions.interactions.component.menu;

import com.github.kaktushose.jda.commands.annotations.interactions.MenuOption;
import com.github.kaktushose.jda.commands.annotations.interactions.MenuOptionContainer;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition.override;

/// Representation of a string select menu.
///
/// @param classDescription  the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription the [MethodDescription] of the method this definition is bound to
/// @param permissions       a [Collection] of permissions for this menu
/// @param selectOptions     the [SelectOptions][MenuOptionDefinition] of this menu
/// @param placeholder       the placeholder text of this menu
/// @param minValue          the minimum amount of choices
/// @param maxValue          the maximum amount of choices
public record StringSelectMenuDefinition(
        ClassDescription classDescription,
        MethodDescription methodDescription,
        Collection<String> permissions,
        Set<MenuOptionDefinition> selectOptions,
        String placeholder,
        int minValue,
        int maxValue
) implements SelectMenuDefinition<StringSelectMenu> {

    /// Builds a new [StringSelectMenuDefinition] with the given values.
    
    public StringSelectMenuDefinition with(Set<SelectOption> selectOptions,
                                           Collection<String> defaultValues,
                                           @Nullable String placeholder,
                                           @Nullable Integer minValue,
                                           @Nullable Integer maxValue) {
        return new StringSelectMenuDefinition(
                this.classDescription,
                this.methodDescription,
                this.permissions,
                createOptions(selectOptions, defaultValues),
                override(this.placeholder, placeholder),
                override(this.minValue, minValue),
                override(this.maxValue, maxValue)
        );
    }

    private Set<MenuOptionDefinition> createOptions(Set<SelectOption> selectOptions, Collection<String> defaultValues) {
        return override(HashSet::new, this.selectOptions, selectOptions
                .stream()
                .map(MenuOptionDefinition::new)
                .collect(Collectors.toSet()))
                .stream()
                .map(selectOption -> defaultValues.contains(selectOption.value())
                        ? selectOption.withDefault()
                        : selectOption
                )
                .collect(Collectors.toSet());
    }

    /// Builds a new [StringSelectMenuDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [Optional] holding the [StringSelectMenuDefinition]
    public static Optional<StringSelectMenuDefinition> build(MethodBuildContext context) {
        var method = context.method();
        com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu selectMenu =
                method.annotation(com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu.class).orElseThrow();

        if (Helpers.checkSignature(method, List.of(ComponentEvent.class, List.class))) {
            return Optional.empty();
        }

        Set<MenuOptionDefinition> selectOptions = new HashSet<>();

        method.annotations().stream()
                .filter(MenuOption.class::isInstance)
                .map(MenuOption.class::cast)
                .forEach(it -> selectOptions.add(MenuOptionDefinition.build(it)));

        method.annotations().stream()
                .filter(MenuOptionContainer.class::isInstance)
                .map(MenuOptionContainer.class::cast)
                .flatMap(it -> Arrays.stream(it.value()))
                .forEach(it -> selectOptions.add(MenuOptionDefinition.build(it)));

        return Optional.of(new StringSelectMenuDefinition(
                context.clazz(),
                method,
                Helpers.permissions(context),
                selectOptions,
                selectMenu.value(),
                selectMenu.minValue(),
                selectMenu.maxValue()
        ));
    }

    /// Transforms this definition to an [StringSelectMenu] with an independent custom id.
    ///
    /// @return the [StringSelectMenu]
    /// @see CustomId#independent(String)
    
    @Override
    public StringSelectMenu toJDAEntity() {
        return toJDAEntity(CustomId.independent(definitionId()));
    }

    /// Transforms this definition to an [StringSelectMenu] with the given [CustomId].
    ///
    /// @param customId the [CustomId] to use
    /// @return the [StringSelectMenu]
    
    @Override
    public StringSelectMenu toJDAEntity(CustomId customId) {
        return StringSelectMenu.create(customId.merged())
                .setPlaceholder(placeholder)
                .setRequiredRange(minValue, maxValue)
                .addOptions(selectOptions.stream().map(MenuOptionDefinition::toJDAEntity).collect(Collectors.toSet()))
                .build();
    }

    
    @Override
    public String displayName() {
        return "Select Menu: %s".formatted(placeholder);
    }

    /// Representation of a select option for a string select menu defined by a [MenuOption].
    ///
    /// @param value       the value of the select option
    /// @param label       the label of the select option
    /// @param description the description of the select option
    /// @param emoji       the [Emoji] of the select option or `null`
    /// @param isDefault   whether the select option is a default value
    public record MenuOptionDefinition(String value,
                                       String label,
                                       @Nullable String description,
                                       @Nullable Emoji emoji,
                                       boolean isDefault
    ) implements JDAEntity<SelectOption>, Definition {

        public MenuOptionDefinition(SelectOption option) {
            this(option.getValue(), option.getLabel(), option.getDescription(), option.getEmoji(), option.isDefault());
        }

        private MenuOptionDefinition withDefault() {
            return new MenuOptionDefinition(value, label, description, emoji, true);
        }

        /// Constructs a new [MenuOptionDefinition] from the given
        /// [`MenuOption`][MenuOption].
        public static MenuOptionDefinition build(MenuOption option) {
            Emoji emoji;
            String emojiString = option.emoji();
            if (emojiString.isEmpty()) {
                emoji = null;
            } else {
                emoji = Emoji.fromFormatted(emojiString);
            }
            return new MenuOptionDefinition(option.value(), option.label(), option.description(), emoji, option.isDefault());
        }

        
        @Override
        public String displayName() {
            return value;
        }

        /// Transforms this definition into a [SelectOption].
        
        @Override
        public SelectOption toJDAEntity() {
            return SelectOption.of(label, value)
                    .withDescription(description)
                    .withEmoji(emoji)
                    .withDefault(isDefault);
        }
    }
}
