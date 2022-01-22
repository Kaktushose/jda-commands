package com.github.kaktushose.jda.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Parameters annotated with Optional are as the name says optional.
 *
 * <p>More formally if a command has an optional parameter the argument doesn't need to be present to execute the
 * command. An optional parameter can also be followed by another optional parameter, but not by a non-optional
 * parameter.
 *
 * <p>It is also possible to pass a default value which will be used instead if the argument isn't present.
 * The default value will be handled as a normal input and thus the
 * {@link com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry TypeAdapterRegistry}
 * will try to parse it. If the parsing fails the command will still be executed but with empty or
 * possible {@code null} values.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Optional {

    /**
     * Returns the default value.
     *
     * @return the default value
     */
    String value() default "";
}
