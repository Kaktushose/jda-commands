package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import jakarta.inject.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Indicates that the annotated class is a custom implementation that should replace the default implementation.
///
/// A class annotated with [Implementation] will be automatically searched for with help of the [ClassFinder]s
/// and instantiated by guice. Following types are candidates for automatic registration.
///
/// - [PermissionsProvider]
/// - [GuildScopeProvider]
/// - [ErrorMessageFactory]
/// - [Descriptor]
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Scope
public @interface Implementation {}
