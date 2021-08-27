package com.github.kaktushose.jda.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If the command is annotated with ConcatQuotes, it will first parse the arguments by quotes.
 *
 * @author Kaktushose
 * @version 1.1.3
 * @see Command
 * @since 1.1.3
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConcatQuotes {
}
