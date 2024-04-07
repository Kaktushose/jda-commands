package com.github.kaktushose.jda.commands.reflect.interactions;

/**
 * Indicates that this class can create a component or modal which Discord requires a custom id for.
 *
 * @see 4.0.0
 */
public interface CustomId {

    String PREFIX = "jdac";
    String CUSTOM_ID_REGEX = String.format("%s.%s", PREFIX, "[aA-zZ]+.s?-?\\d*");

    /**
     * Gets the custom id for this component.
     *
     * @param runtimeId the runtimeId of this component execution
     * @return the runtime id
     */
    String createCustomId(String runtimeId);

}
