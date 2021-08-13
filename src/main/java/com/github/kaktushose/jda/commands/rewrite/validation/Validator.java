package com.github.kaktushose.jda.commands.rewrite.validation;

public interface Validator {

    boolean validate(Class<?> type, Object object);

}
