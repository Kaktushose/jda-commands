package com.github.kaktushose.jda.commands.annotations.interactions;

import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Button {

    String id() default "";

    ButtonStyle style();

    String label() default "";

    String emoji() default "";

    String link() default "";

}
