package com.github.kaktushose.jda.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Parameters annotated with Concat will be assigned with a concatenated String of all remaining arguments.
 * Therefore, the parameter annotated with Concat must be a String and must also be the last parameter defined.
 * Any other occurrence will result in an error.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see Command
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Concat {
}
