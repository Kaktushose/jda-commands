package com.github.kaktushose.jda.commands.rewrite.validation;

import net.dv8tion.jda.api.entities.Guild;

public interface Validator {

    boolean validate(Object argument, Object annotation, Guild guild);

}
