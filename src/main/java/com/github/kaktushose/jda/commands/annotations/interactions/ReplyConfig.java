package com.github.kaktushose.jda.commands.annotations.interactions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReplyConfig {

    /**
     * Returns whether this command should send ephemeral replies by default.
     *
     * @return {@code true} if this command should send ephemeral replies
     */
    boolean ephemeral() default false;

    boolean keepComponents() default true;

    boolean editReply() default true;

}
