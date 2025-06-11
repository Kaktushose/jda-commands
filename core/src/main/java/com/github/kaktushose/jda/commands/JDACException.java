package com.github.kaktushose.jda.commands;

/// An abstract top class indicating that an Exception corresponds to JDA-Commands
public sealed abstract class JDACException extends RuntimeException permits JDACException.Configuration, JDACException.Internal {
    public JDACException(String message) {
        super(message);
    }

    public JDACException(String message, Object... placeholder) {
        super(message.formatted(placeholder));
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
}
