package com.github.kaktushose.jda.commands.exceptions;

/// Will be thrown if anything goes wrong internally. Should be reported to the devs.
public final class InternalException extends RuntimeException {

  /// @param message the exception message to be displayed
  public InternalException(String message) {
    super(message);
  }

  /// @param message the exception message to be displayed
  /// @param placeholder the values to replace the placeholders (see [String#format(String, Object...) ])
  public InternalException(String message, Object... placeholder) {
    super(message.formatted(placeholder));
  }

  @Override
  public String getMessage() {
    return super.getMessage() + " Please report this error the the devs of jda-commands.";
  }
}
