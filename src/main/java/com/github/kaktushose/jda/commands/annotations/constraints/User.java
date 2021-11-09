package com.github.kaktushose.jda.commands.annotations.constraints;

import net.dv8tion.jda.api.entities.Member;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint({Member.class, net.dv8tion.jda.api.entities.User.class})
public @interface User {
    String value();

    String message() default "";
}
