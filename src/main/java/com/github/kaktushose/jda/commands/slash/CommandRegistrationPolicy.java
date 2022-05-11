package com.github.kaktushose.jda.commands.slash;

/**
 * Policy defining different operation modes.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see com.github.kaktushose.jda.commands.JDACommandsSlashBuilder JDACommandsSlashBuilder
 * @since 2.3.0
 */
public enum CommandRegistrationPolicy {
    /**
     * This policy will not register any slash commands and will only listen to text commands.
     */
    TEXT,
    /**
     * This policy will register slash commands and will not listen to text commands.
     */
    SLASH,
    /**
     * This policy will register slash commands and will listen to text commands.
     */
    TEXT_AND_SLASH,
    /**
     * This policy will register slash commands and will respond with a deprecation message to text commands.
     */
    MIGRATING
}
