package com.github.kaktushose.jda.commands.definitions.api.interactions;

import com.github.kaktushose.jda.commands.definitions.api.Definition;
import org.jetbrains.annotations.NotNull;

public sealed interface CustomIdInteraction extends Definition permits ButtonDefinition, ModalDefinition, SelectMenuDefinition {

    String PREFIX = "jdac";

    @NotNull
    default CustomId boundCustomId(@NotNull String runtimeId) {
        return new CustomId("%s.%s.%s".formatted(PREFIX, runtimeId, definitionId()));
    }

    @NotNull
    default CustomId independentCustomId() {
        return new CustomId("%s.independent.%s".formatted(PREFIX, definitionId()));
    }

    record CustomId(@NotNull String customId) {
        public static String PREFIX = "jdac";
        public static String BOUND_CUSTOM_ID_REGEX = "^jdac\\.[0-9a-fA-F-]{36}\\.-?\\d+$";
        public static String INDEPENDENT_CUSTOM_ID_REGEX = "^jdac\\.independent\\.-?\\d+$";

        /// Extracts the runtime id from the passed custom id.
        ///
        /// @return the runtime id
        @NotNull
        String runtimeId() {
            if (isIndependent()) {
                throw new IllegalArgumentException("Provided custom id is independent!");
            }
            return getId(1);
        }

        /// Extracts the definition id from the passed custom id.
        ///
        /// @return the runtime id
        /// @throws IllegalArgumentException if the passed custom id is a runtime-independent id
        @NotNull
        String definitionId() {
            return getId(2);
        }

        /// Checks if the passed custom id is runtime-independent.
        ///
        /// @return `true` if the custom id is runtime-independent
        boolean isIndependent() {
            return customId.matches(INDEPENDENT_CUSTOM_ID_REGEX);
        }

        /// Checks if the passed custom id is runtime-bound.
        ///
        /// @return `true` if the custom id is runtime-bound
        boolean isBound() {
            return customId.matches(BOUND_CUSTOM_ID_REGEX);
        }

        /// Checks if the passed custom id conforms to the defined format of jda-commands.
        ///
        /// @return `true` if the passed custom id conforms to the jda-commands format
        boolean isInvalid() {
            return !(isIndependent() || isBound());
        }

        private String getId(int index) {
            if (isInvalid()) {
                throw new IllegalArgumentException("Illegal id format!");
            }
            return customId.split("\\.")[index];
        }
    }

}
