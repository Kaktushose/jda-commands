package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.message.i18n.Localizer;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import jakarta.inject.Scope;

import java.lang.annotation.*;

/// Indicates that the annotated class is a custom implementation that should replace the default implementation.
///
/// A class annotated with [Implementation] will be automatically searched for with help of the [ClassFinder]s
/// and instantiated by guice. Following types are candidates for automatic registration.
///
/// - [PermissionsProvider]
/// - [GuildScopeProvider]
/// - [ErrorMessageFactory]
/// - [Descriptor]
/// - [Localizer]
///
/// Additionally, the following classes can be also automatically registered via their dedicated annotations.
///
/// - [`Middleware`][com.github.kaktushose.jda.commands.dispatching.middleware.Middleware] (via [`@Implementation.Middleware`][Middleware])
/// - [`TypeAdapter`][com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter] (via [`@Implementation.TypeAdapter`][TypeAdapter])
/// - [`Validator`][com.github.kaktushose.jda.commands.dispatching.validation.Validator] (via [`@Implementation.Validator`][Validator])
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Scope
public @interface Implementation {

    /// A class annotated with [Middleware] will be automatically searched for and instantiated as a
    /// [`Middleware`][com.github.kaktushose.jda.commands.dispatching.middleware.Middleware] by guice.
    ///
    /// ### Example
    /// ```java
    /// @Middleware(priority = Priority.NORMAL)
    /// public class CustomMiddleware implements Middleware {
    ///     private static final Logger log = LoggerFactory.getLogger(FirstMiddleware.class);
    ///
    ///     @Override
    ///     public void accept(InvocationContext<?> context) {
    ///         log.info("run custom middleware");
    ///     }
    /// }
    /// ```
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Scope
    @interface Middleware {

        /// Gets the [Priority] to register the [`Middleware`][com.github.kaktushose.jda.commands.dispatching.middleware.Middleware] with.
        ///
        /// @return the [Priority]
        Priority priority() default Priority.NORMAL;

    }

    /// A class annotated with [TypeAdapter] will be automatically searched for and instantiated as a
    /// [`TypeAdapter`][com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter] by guice.
    ///
    /// ### Example
    /// ```java
    /// @TypeAdapter(clazz = CustomType.class)
    /// public class CustomTypeAdapter implements TypeAdapter<CustomType> {
    ///
    ///     public Optional<CustomType> apply(String raw, GenericInteractionCreateEvent event) {
    ///         return Optional.of(new CustomType(raw, event));
    ///     }///
    /// }
    /// ```
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Scope
    @interface TypeAdapter {

        /// Gets the [Class] this [`TypeAdapter`][com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter] will convert from.
        ///
        /// @return the class the [`TypeAdapter`][com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter]
        /// will convert from
        Class<?> source();

        /// Gets the [Class] this [`TypeAdapter`][com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter] will convert into.
        ///
        /// @return the class the [`TypeAdapter`][com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter]
        /// will convert into
        Class<?> target();

    }

    /// A class annotated with [Validator] will be automatically searched for and instantiated as a
    /// [`Validator`][com.github.kaktushose.jda.commands.dispatching.validation.Validator] by guice.
    ///
    /// ### Example
    /// ```java
    /// @Target(ElementType.PARAMETER)
    /// @Retention(RetentionPolicy.RUNTIME)
    /// @Constraint(String.class)
    /// public @interface MaxString {
    ///     int value();
    ///     String message() default "The given String is too long";
    /// }
    ///
    /// @Validator(annotation = MaxString.class)
    /// public class MaxStringLengthValidator implements Validator {
    ///
    ///     @Override
    ///    public boolean apply(Object argument, Object annotation, InvocationContext<? context) {
    ///         MaxString maxString = (MaxString) annotation;
    ///         return String.valueOf(argument).length() < maxString.value();
    ///     }
    /// }
    /// ```
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Scope
    @interface Validator {

        /// Gets the annotation the [`Validator`][com.github.kaktushose.jda.commands.dispatching.validation.Validator]
        /// should be mapped to.
        ///
        /// @return the annotation the [`Validator`][com.github.kaktushose.jda.commands.dispatching.validation.Validator]
        /// should be mapped to
        Class<? extends Annotation> annotation();

    }
}
