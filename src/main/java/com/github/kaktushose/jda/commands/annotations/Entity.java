package com.github.kaktushose.jda.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes annotated with Entity can parsed by the {@link com.github.kaktushose.jda.commands.api.ArgumentParser}.
 *
 * <p>This annotation provides a way to use parameters that are not supported by default. Classes annotated with
 * Entity will need a constructor consuming a {@code String}, otherwise the
 * {@link com.github.kaktushose.jda.commands.api.ArgumentParser} won't be able to parse it. In order to prevent errors,
 * any {@link Command} using a Entity that doesn't meet this condition, will be skipped during startup.
 *
 * @author Kaktushose
 * @version 1.1.0
 * @since 1.1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
}
