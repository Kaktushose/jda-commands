package com.github.kaktushose.jda.commands.dispatching.validation;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;

public interface Validator {

    boolean validate(Object argument, Object annotation, CommandContext context);

}
