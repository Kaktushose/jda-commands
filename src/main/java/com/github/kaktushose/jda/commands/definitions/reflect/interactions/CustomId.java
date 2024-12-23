package com.github.kaktushose.jda.commands.definitions.reflect.interactions;

import com.github.kaktushose.jda.commands.dispatching.internal.Runtime;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.components.GenericComponentDefinition;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/// Indicates that this class can create a component or modal which Discord requires a custom id for.
///
/// @implSpec the custom id has the following structure: `[prefix.runtimeId.definitionId]`. The prefix will always be `jdac`,
/// indicating that this id was created by jda-commands. The runtime id is a UUID or the String literal `independent`,
/// if the component or modal works runtime independent. The definition id is the hash value of the full class name and
/// method name combined: `(method.getDeclaringClass.getName() + method.getName()).hash()`
/// @see GenericComponentDefinition
/// @see ModalDefinition
/// @since 4.0.0
@ApiStatus.Internal
public sealed interface CustomId permits GenericComponentDefinition, ModalDefinition {

    String PREFIX = "jdac";
    String BOUND_CUSTOM_ID_REGEX = "^jdac\\.[0-9a-fA-F-]{36}\\.-?\\d+$";
    String INDEPENDENT_CUSTOM_ID_REGEX = "^jdac\\.independent\\.-?\\d+$";

    /// Extracts the runtime id from the passed custom id.
    ///
    /// @param customId the custom id to extract the runtime id from
    /// @return the runtime id
    @NotNull
    static String runtimeId(@NotNull String customId) {
        if (isIndependent(customId)) {
            throw new IllegalArgumentException("Provided custom id is independent!");
        }
        return getId(customId, 1);
    }

    /// Extracts the definition id from the passed custom id.
    ///
    /// @param customId the definition id to extract the runtime id from
    /// @return the runtime id
    /// @throws IllegalArgumentException if the passed custom id is a runtime-independent id
    @NotNull
    static String definitionId(@NotNull String customId) {
        return getId(customId, 2);
    }

    /// Checks if the passed custom id is runtime-independent.
    ///
    /// @param customId the custom id to check
    /// @return `true` if the custom id is runtime-independent
    static boolean isIndependent(@NotNull String customId) {
        return customId.matches(INDEPENDENT_CUSTOM_ID_REGEX);
    }

    /// Checks if the passed custom id is runtime-bound.
    ///
    /// @param customId the custom id to check
    /// @return `true` if the custom id is runtime-bound
    static boolean isBound(@NotNull String customId) {
        return customId.matches(BOUND_CUSTOM_ID_REGEX);
    }

    /// Checks if the passed custom id conforms to the defined format of jda-commands.
    ///
    /// @param customId the custom id to check
    /// @return `true` if the passed custom id conforms to the jda-commands format
    static boolean isInvalid(@NotNull String customId) {
        return !(isIndependent(customId) || isBound(customId));
    }

    private static String getId(String customId, int index) {
        if (isInvalid(customId)) {
            throw new IllegalArgumentException("Illegal id format!");
        }
        return customId.split("\\.")[index];
    }

    /// Gets a custom id for this definition that is bound to the passed [Runtime] id.
    ///
    /// @param runtimeId the id of the [Runtime]
    /// @return the custom id
    @NotNull
    String boundCustomId(@NotNull String runtimeId);

    /// Gets a custom id for this definition that is runtime-independent.
    ///
    /// @return the custom id
    /// @throws UnsupportedOperationException if the interaction this definition wraps doesn't support independent execution
    @NotNull
    String independentCustomId();
}
