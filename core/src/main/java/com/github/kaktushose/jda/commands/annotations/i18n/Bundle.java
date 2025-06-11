package com.github.kaktushose.jda.commands.annotations.i18n;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// The bundle annotation states which localization bundle is to be used
/// in a certain scope. For more information refer to [com.github.kaktushose.jda.commands.i18n.I18n]
@Target({ElementType.PACKAGE, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Bundle {

    /// the name of the bundle to use
    String value();
}
