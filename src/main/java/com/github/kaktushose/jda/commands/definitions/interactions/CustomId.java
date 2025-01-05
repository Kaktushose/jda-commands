package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.definitions.Definition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

/// Representation of a custom id used in modals, buttons or select menus.
///
/// @param runtimeId    the id of the [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) this custom id is bound to
///                     or the literal `independent`.
/// @param definitionId the [Definition#definitionId()]
/// @implNote the custom id has the following format: `jdac.runtimeId.definitionId`
public record CustomId(@NotNull String runtimeId, @NotNull String definitionId) {
    private static final String PREFIX = "jdac";
    private static final String INDEPENDENT_ID = "independent";
    public static final String BOUND_CUSTOM_ID_REGEX = "^jdac\\.[0-9a-fA-F-]{36}\\.-?\\d+$";
    public static final String INDEPENDENT_CUSTOM_ID_REGEX = "^jdac\\.%s\\.-?\\d+$".formatted(INDEPENDENT_ID);

    public CustomId {
        if (!runtimeId.matches("[0-9a-fA-F-]{36}") && !runtimeId.equals(INDEPENDENT_ID)) {
            throw new IllegalArgumentException("Invalid runtime id! Must either be a UUID or \"%s\"".formatted(INDEPENDENT_ID));
        }
    }

    /// Constructs a new [CustomId] from the given [GenericInteractionCreateEvent].
    ///
    /// @param event the [GenericInteractionCreateEvent]
    /// @return the [CustomId]
    @NotNull
    public static CustomId fromEvent(@NotNull GenericComponentInteractionCreateEvent event) {
        return fromEvent(event.getComponentId());
    }

    /// Constructs a new [CustomId] from the given [ModalInteractionEvent].
    ///
    /// @param event the [ModalInteractionEvent]
    /// @return the [CustomId]
    @NotNull
    public static CustomId fromEvent(@NotNull ModalInteractionEvent event) {
        return fromEvent(event.getModalId());
    }

    /// Constructs a new [CustomId] from the given String.
    ///
    /// @param customId the custom id String
    /// @return the [CustomId]
    @NotNull
    private static CustomId fromEvent(@NotNull String customId) {
        if (isInvalid(customId)) {
            throw new IllegalArgumentException("Provided custom id is invalid!");
        }
        var split = customId.split("\\.");
        return new CustomId(split[1], split[2]);
    }

    /// Constructs a new runtime-independent [CustomId] from the given definition id.
    ///
    /// @param definitionId the definition id to construct the [CustomId] from
    /// @return a new runtime-independent [CustomId]
    @NotNull
    public static CustomId independent(@NotNull String definitionId) {
        return new CustomId(INDEPENDENT_ID, definitionId);
    }

    /// Checks if the passed custom id *doesn't* conform to the defined format of jda-commands.
    ///
    /// @return `true` if the passed custom id *doesn't* conform to the jda-commands format
    public static boolean isInvalid(@NotNull String customId) {
        return !(customId.matches(BOUND_CUSTOM_ID_REGEX) || customId.matches(INDEPENDENT_CUSTOM_ID_REGEX));
    }

    /// The String representation of this custom id.
    @NotNull
    public String id() {
        return "%s.%s.%s".formatted(PREFIX, runtimeId, definitionId);
    }

    /// Gets the runtime id of this custom id.
    ///
    /// @return the runtime id
    /// @throws IllegalStateException if this custom id is runtime-independent
    @NotNull
    public String runtimeId() {
        if (isIndependent()) {
            throw new IllegalStateException("Provided custom id is runtime-independent!");
        }
        return runtimeId;
    }

    /// Checks if the passed custom id is runtime-independent.
    ///
    /// @return `true` if the custom id is runtime-independent
    public boolean isIndependent() {
        return runtimeId.equals(INDEPENDENT_ID);
    }

    /// Checks if the passed custom id is runtime-bound.
    ///
    /// @return `true` if the custom id is runtime-bound
    public boolean isBound() {
        return !isIndependent();
    }
}
