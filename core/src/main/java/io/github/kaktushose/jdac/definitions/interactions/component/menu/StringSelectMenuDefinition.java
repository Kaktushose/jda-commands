package io.github.kaktushose.jdac.definitions.interactions.component.menu;

import io.github.kaktushose.jdac.annotations.interactions.MenuOption;
import io.github.kaktushose.jdac.annotations.interactions.MenuOptionContainer;
import io.github.kaktushose.jdac.definitions.Definition;
import io.github.kaktushose.jdac.definitions.description.ClassDescription;
import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.features.NonCustomIdJDAEntity;
import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.MethodBuildContext;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.internal.Helpers;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.kaktushose.jdac.definitions.interactions.component.ComponentDefinition.override;

/// Representation of a string select menu.
///
/// @param classDescription  the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription the [MethodDescription] of the method this definition is bound to
/// @param permissions       a [Collection] of permissions for this menu
/// @param selectOptions     the [SelectOptions][MenuOptionDefinition] of this menu
/// @param placeholder       the placeholder text of this menu
/// @param minValue          the minimum amount of choices
/// @param maxValue          the maximum amount of choices
/// @param uniqueId          the uniqueId of this menu
public record StringSelectMenuDefinition(
        ClassDescription classDescription,
        MethodDescription methodDescription,
        Collection<String> permissions,
        Set<MenuOptionDefinition> selectOptions,
        String placeholder,
        int minValue,
        int maxValue,
        @Nullable Integer uniqueId
) implements SelectMenuDefinition<StringSelectMenu> {

    /// Builds a new [StringSelectMenuDefinition] from the given [MethodBuildContext].
    ///
    /// @return an [Optional] holding the [StringSelectMenuDefinition]
    public static StringSelectMenuDefinition build(MethodBuildContext context) {
        var method = context.method();
        io.github.kaktushose.jdac.annotations.interactions.StringSelectMenu selectMenu =
                method.annotation(io.github.kaktushose.jdac.annotations.interactions.StringSelectMenu.class);

        Helpers.checkSignature(method, List.of(ComponentEvent.class, List.class));

        Set<MenuOptionDefinition> selectOptions = new HashSet<>();

        method.findAnnotation(MenuOption.class)
                .ifPresent(it -> selectOptions.add(MenuOptionDefinition.build(it)));

        method.findAnnotation(MenuOptionContainer.class)
                .stream()
                .flatMap(it -> Arrays.stream(it.value()))
                .forEach(it -> selectOptions.add(MenuOptionDefinition.build(it)));

        return new StringSelectMenuDefinition(
                context.clazz(),
                method,
                Helpers.permissions(context),
                selectOptions,
                selectMenu.value(),
                selectMenu.minValue(),
                selectMenu.maxValue(),
                selectMenu.uniqueId() < 0 ? null : selectMenu.uniqueId()
        );
    }

    /// Builds a new [StringSelectMenuDefinition] with the given values.

    public StringSelectMenuDefinition with(Set<SelectOption> selectOptions,
                                           Collection<String> defaultValues,
                                           @Nullable String placeholder,
                                           @Nullable Integer minValue,
                                           @Nullable Integer maxValue,
                                           @Nullable Integer uniqueId) {
        return new StringSelectMenuDefinition(
                this.classDescription,
                this.methodDescription,
                this.permissions,
                createOptions(selectOptions, defaultValues),
                override(this.placeholder, placeholder),
                override(this.minValue, minValue),
                override(this.maxValue, maxValue),
                override(this.uniqueId, uniqueId)
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

    /// Transforms this definition to an [StringSelectMenu] with the given [CustomId].
    ///
    /// @param customId the [CustomId] to use
    /// @return the [StringSelectMenu]
    @Override
    public StringSelectMenu toJDAEntity(CustomId customId) {
        try {
            StringSelectMenu menu = StringSelectMenu.create(customId.merged())
                    .setPlaceholder(placeholder)
                    .setRequiredRange(minValue, maxValue)
                    .addOptions(selectOptions.stream().map(MenuOptionDefinition::toJDAEntity).collect(Collectors.toSet()))
                    .build();
            if (uniqueId != null) {
                menu = menu.withUniqueId(uniqueId);
            }
            return menu;
        } catch (IllegalArgumentException e) {
            throw Helpers.jdaException(e, this);
        }
    }

    @Override
    public String displayName() {
        return "String Select Menu: %s".formatted(placeholder);
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
    ) implements NonCustomIdJDAEntity<SelectOption>, Definition {

        public MenuOptionDefinition(SelectOption option) {
            this(option.getValue(), option.getLabel(), option.getDescription(), option.getEmoji(), option.isDefault());
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

        private MenuOptionDefinition withDefault() {
            return new MenuOptionDefinition(value, label, description, emoji, true);
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
