package com.github.kaktushose.jda.commands;

/// An abstract top class indicating that an Exception corresponds to JDA-Commands
public sealed abstract class JDACException extends RuntimeException permits JDACException.Configuration, JDACException.Internal, JDACException.InvalidDeclaration, JDACException.Other {
    public JDACException(String message) {
        super(message);
    }

    public JDACException(String message, Object... placeholder) {
        super(message.formatted(placeholder));
    }

    public JDACException(Throwable cause) {
        super(cause);
    }

    public JDACException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /// Will be thrown if anything goes wrong while configuring jda-commands.
    public static final class Configuration extends JDACException {
        public Configuration(String message) {
            super("Error while trying to configure jda-commands: " + message);
        }

        public Configuration(String message, Object... placeholder) {
            super(message, placeholder);
        }
    }

    /// Will be thrown if anything goes wrong internally. Should be reported to the devs.
    public static final class Internal extends JDACException {

        public Internal(String message) {
            super(message);
        }

        public Internal(String message, Object... placeholder) {
            super(message, placeholder);
        }

        @Override
        public String getMessage() {
            return super.getMessage() + " Please report this error the the devs of jda-commands.";
        }
    }

    /// Will be thrown if any errors are made in the declaration of commands/components etc.
    public static final class InvalidDeclaration extends JDACException {

        public InvalidDeclaration(String message) {
            super(message);
        }

        public InvalidDeclaration(String message, Object... placeholder) {
            super(message, placeholder);
        }
    }

    /// A wrapper around any [Throwable] thrown while starting JDA-Commands
    public static final class Other extends JDACException {

        public Other(String message, Throwable throwable) {
            super(message, throwable);
        }

        public Other(Throwable cause) {
            super(cause);
        }
    }

    static JDACException wrap(Throwable throwable) {
        if (throwable instanceof JDACException e) {
            throw e;
        }
        return new Other(throwable);
    }
}
