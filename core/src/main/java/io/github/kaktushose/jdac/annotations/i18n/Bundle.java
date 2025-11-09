package io.github.kaktushose.jdac.annotations.i18n;

import io.github.kaktushose.jdac.message.i18n.I18n;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// The bundle annotation states which localization bundle is to be used
/// in a certain scope.
///
/// @see I18n
@Target({ElementType.PACKAGE, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Bundle {

    /// the name of the bundle to use
    String value();
}
