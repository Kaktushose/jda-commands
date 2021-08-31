package com.github.kaktushose.jda.commands.rewrite.exceptions;

/**
 * Exception that is mainly used to log any errors regarding the configuration or declaration of commands.
 * CommandExceptions are runtime exceptions.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @see RuntimeException
 * @since 1.0.0
 */
public class CommandException extends RuntimeException {

    public CommandException(String error) {
        super(error);
    }

    public CommandException(String error, Throwable t) {
        super(error, t);
    }

    public CommandException(Throwable t) {
        super(t);
    }

}
