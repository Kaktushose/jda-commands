package com.github.kaktushose.jda.commands.exceptions;

/**
 * This exception is raised if any error occurs while transferring a
 * {@link com.github.kaktushose.jda.commands.entities.EmbedDTO} to either {@code EmbedBuilder} or {@code MessageEmbed}.
 * EmbedCastExceptions are runtime exceptions.
 *
 * @author Kaktushose
 * @version 1.1.0
 * @see RuntimeException
 * @since 1.1.0
 */
public class EmbedCastException extends RuntimeException {

    public EmbedCastException(String error) {
        super(error);
    }

    public EmbedCastException(String error, Throwable t) {
        super(error, t);
    }

    public EmbedCastException(Throwable t) {
        super(t);
    }

}
