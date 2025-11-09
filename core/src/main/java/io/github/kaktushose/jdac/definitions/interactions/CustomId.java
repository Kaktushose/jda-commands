package io.github.kaktushose.jdac.definitions.interactions;

import io.github.kaktushose.jdac.definitions.Definition;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;

/// Representation of a custom id used in modals, buttons or select menus.
///
/// @param runtimeId    the id of the [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) this custom id is bound to
///                     or the literal `independent`.
/// @param definitionId the [Definition#definitionId()]
/// @implNote the custom id has the following format: `jdac.runtimeId.definitionId`
public record CustomId(String runtimeId, String definitionId) {
    public static final String BOUND_CUSTOM_ID_REGEX = "^jdac\\.[0-9a-fA-F-]{36}\\.-?\\d+$";
    private static final String PREFIX = "jdac";
    private static final String INDEPENDENT_ID = "independent";
    public static final String INDEPENDENT_CUSTOM_ID_REGEX = "^jdac\\.%s\\.-?\\d+$".formatted(INDEPENDENT_ID);

    public CustomId {
        if (!runtimeId.matches("[0-9a-fA-F-]{36}") && !runtimeId.equals(INDEPENDENT_ID)) {
            throw new IllegalArgumentException(JDACException.errorMessage("invalid-runtime-id"));
        }
    }

    /// Constructs a new [CustomId] from the given String.
    ///
    /// @param customId the custom id String
    /// @return the [CustomId]
    public static CustomId fromMerged(String customId) {
        if (isInvalid(customId)) {
            throw new IllegalArgumentException(JDACException.errorMessage("invalid-custom-id"));
        }
        var split = customId.split("\\.");
        return new CustomId(split[1], split[2]);
    }

    /// Constructs a new runtime-independent [CustomId] from the given definition id.
    ///
    /// @param definitionId the definition id to construct the [CustomId] from
    /// @return a new runtime-independent [CustomId]
    public static CustomId independent(String definitionId) {
        return new CustomId(INDEPENDENT_ID, definitionId);
    }

    /// Checks if the passed custom id *doesn't* conform to the defined format of jda-commands.
    ///
    /// @return `true` if the passed custom id *doesn't* conform to the jda-commands format
    public static boolean isInvalid(String customId) {
        return !(customId.matches(BOUND_CUSTOM_ID_REGEX) || customId.matches(INDEPENDENT_CUSTOM_ID_REGEX));
    }

    /// The String representation of this custom id.
    public String merged() {
        return "%s.%s.%s".formatted(PREFIX, runtimeId, definitionId);
    }

    /// Gets the runtime id of this custom id.
    ///
    /// @return the runtime id
    /// @throws IllegalStateException if this custom id is runtime-independent
    public String runtimeId() {
        if (isIndependent()) {
            throw new IllegalStateException(JDACException.errorMessage("independent-runtime-id"));
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
