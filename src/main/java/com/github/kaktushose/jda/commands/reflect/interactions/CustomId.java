package com.github.kaktushose.jda.commands.reflect.interactions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Indicates that this class can create a component or modal which Discord requires a custom id for.
 *
 * @see 4.0.0
 */
public interface CustomId {

    String PREFIX = "jdac";
    String CUSTOM_ID_REGEX = "^jdac\\.[0-9a-fA-F-]{36}\\.[0-9a-fA-F-]{36}$";

    /**
     * Gets the custom id for this component.
     *
     * @param runtimeId the runtimeId of this component execution
     * @return the runtime id
     */
    String createCustomId(String runtimeId);

    @NotNull
    static String getRuntimeId(@Nullable String customId) {
        return getId(customId, 1);
    }

    @NotNull
    static String getDefinitionId(@Nullable String customId) {
        return getId(customId, 2);
    }

    static boolean isInvalid(@Nullable String customId) {
        return customId == null || !customId.matches(CUSTOM_ID_REGEX);
    }

    private static String getId(String customId, int index) {
        if (isInvalid(customId)) {
            throw new IllegalArgumentException("Illegal id format!");
        }
        return customId.split("\\.")[index];
    }

}
