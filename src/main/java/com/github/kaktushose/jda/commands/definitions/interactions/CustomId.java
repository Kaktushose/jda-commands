package com.github.kaktushose.jda.commands.definitions.interactions;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

public record CustomId(@NotNull String runtimeId, @NotNull String definitionId) {
    private static final String PREFIX = "jdac";
    private static final String INDEPENDENT_ID = "independent";
    public static String BOUND_CUSTOM_ID_REGEX = "^jdac\\.[0-9a-fA-F-]{36}\\.-?\\d+$";
    public static String INDEPENDENT_CUSTOM_ID_REGEX = "^jdac\\.%s\\.-?\\d+$".formatted(INDEPENDENT_ID);

    public CustomId {
        if (!(runtimeId.matches("[0-9a-fA-F-]{36}") || runtimeId.equals(INDEPENDENT_ID))) {
            throw new IllegalArgumentException("Invalid runtime id! Must either be a UUID or \"%s\"".formatted(INDEPENDENT_ID));
        }
    }

    public CustomId(String definitionId) {
        this(INDEPENDENT_ID, definitionId);
    }

    @NotNull
    public static CustomId fromEvent(@NotNull GenericComponentInteractionCreateEvent event) {
        return fromEvent(event.getComponentId());
    }

    @NotNull
    public static CustomId fromEvent(@NotNull ModalInteractionEvent event) {
        return fromEvent(event.getModalId());
    }

    @NotNull
    private static CustomId fromEvent(@NotNull String customId) {
        if (isInvalid(customId)) {
            throw new IllegalArgumentException("Provided custom id is invalid!");
        }
        var split = customId.split("\\.");
        return new CustomId(split[0], split[1]);
    }

    @NotNull
    public String id() {
        return "%s.%s.%s".formatted(PREFIX, runtimeId, definitionId);
    }

    /// Extracts the runtime id from the passed custom id.
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

    /// Checks if the passed custom id conforms to the defined format of jda-commands.
    ///
    /// @return `true` if the passed custom id conforms to the jda-commands format
    public static boolean isInvalid(String customId) {
        return !(customId.matches(BOUND_CUSTOM_ID_REGEX) || customId.matches(INDEPENDENT_CUSTOM_ID_REGEX));
    }

}