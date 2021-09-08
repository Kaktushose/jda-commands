package com.github.kaktushose.jda.commands.rewrite.dispatching.validation;

import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;

public interface Validator {

    boolean validate(Object argument, Object annotation, CommandContext context);

}
