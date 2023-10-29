package com.github.kaktushose.jda.commands.annotations.interactions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with Modal will be registered as a modal at startup.
 *
 * <p>Therefore the method must be declared inside a class that is annotated with
 * {@link Interaction}.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see Interaction
 * @see TextInput
 * @since 4.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Modal {

    /**
     * Gets the title of this modal.
     *
     * @return the title of the modal
     */
    String value();

    /**
     * Whether this Modal should send ephemeral replies by default.
     *
     * @return {@code true} if to send ephemeral replies
     */
    boolean ephemeral() default false;

}
