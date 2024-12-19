package com.github.kaktushose.jda.commands.reflect.interactions;

import org.jetbrains.annotations.NotNull;

/**
 * Indicates that this class can create a component or modal which Discord requires a custom id for.
 *
 * @see 4.0.0
 */
public interface CustomId {

    String PREFIX = "jdac";
    String SCOPED_CUSTOM_ID_REGEX = "^jdac\\.[0-9a-fA-F-]{36}\\.-?\\d+$";
    String STATIC_CUSTOM_ID_REGEX = "^jdac\\.static\\.-?\\d+$";

    /**
     * Gets the custom id for this component.
     *
     * @param runtimeId the runtimeId of this component execution
     * @return the runtime id
     */
    String scopedCustomId(String runtimeId);

    String staticCustomId();

    @NotNull
    static String runtimeId(@NotNull String customId) {
        return getId(customId, 1);
    }

    @NotNull
    static String definitionId(@NotNull String customId) {
        return getId(customId, 2);
    }

    static boolean isStatic(@NotNull String customId) {
        return customId.matches(STATIC_CUSTOM_ID_REGEX);
    }

    static boolean isScoped(@NotNull String customId) {
        return customId.matches(SCOPED_CUSTOM_ID_REGEX);
    }

    static boolean isInvalid(@NotNull String customId) {
        return !(isStatic(customId) || isScoped(customId));
    }

    private static String getId(String customId, int index) {
        if (isInvalid(customId)) {
            throw new IllegalArgumentException("Illegal id format!");
        }
        return customId.split("\\.")[index];
    }

}
