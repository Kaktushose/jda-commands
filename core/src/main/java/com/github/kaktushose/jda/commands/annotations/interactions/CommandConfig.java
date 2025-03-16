package com.github.kaktushose.jda.commands.annotations.interactions;

import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandConfig {

    InteractionContextType[] context() default InteractionContextType.GUILD;

    IntegrationType[] integration() default IntegrationType.GUILD_INSTALL;

    CommandScope scope() default CommandScope.GLOBAL;

    boolean isNSFW() default false;

}
