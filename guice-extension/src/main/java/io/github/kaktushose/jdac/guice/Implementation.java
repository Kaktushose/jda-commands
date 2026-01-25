package io.github.kaktushose.jdac.guice;

import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.PropertyProvider;
import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.dispatching.middleware.Priority;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.message.emoji.EmojiResolver;
import io.github.kaktushose.jdac.message.emoji.EmojiSource;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.i18n.Localizer;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import io.github.kaktushose.jdac.permissions.PermissionsProvider;
import io.github.kaktushose.jdac.scope.GuildScopeProvider;
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
/// - [EmojiSource]
///
/// Additionally, the following classes can be also automatically registered via their dedicated annotations.
///
/// - [`Middleware`][io.github.kaktushose.jdac.dispatching.middleware.Middleware] (via [`@Implementation.Middleware`][Middleware])
/// - [`TypeAdapter`][io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter] (via [`@Implementation.TypeAdapter`][TypeAdapter])
/// - [`Validator`][io.github.kaktushose.jdac.dispatching.validation.Validator] (via [`@Implementation.Validator`][Validator])
///
/// ## Dependencies on other framework components
/// If you need access to other components of [JDACommands], you can get all [Properties][Property] of stage [Stage#CONFIGURATION]
/// by either injecting (see list underneath) or using [Introspection].
///
/// Please note that this could lead to cycling dependencies errors,
/// for more information see [PropertyProvider].
///
/// Following components are directly injectable by Guice:
/// - [I18n]
/// - [MessageResolver]
/// - [EmojiResolver]
/// - [Descriptor]
/// - [ClassFinder]
/// - [Introspection]
///
/// If you need other properties of stage [Stage#CONFIGURATION], just inject the [Introspection] instance and retrieve
/// them manually.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Scope
public @interface Implementation {

    /// A class annotated with [Middleware] will be automatically searched for and instantiated as a
    /// [`Middleware`][io.github.kaktushose.jdac.dispatching.middleware.Middleware] by guice.
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

        /// Gets the [Priority] to register the [`Middleware`][io.github.kaktushose.jdac.dispatching.middleware.Middleware] with.
        ///
        /// @return the [Priority]
        Priority priority() default Priority.NORMAL;

    }

    /// A class annotated with [TypeAdapter] will be automatically searched for and instantiated as a
    /// [`TypeAdapter`][io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter] by guice.
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

        /// Gets the [Class] this [`TypeAdapter`][io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter] will convert from.
        ///
        /// @return the class the [`TypeAdapter`][io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter]
        /// will convert from
        Class<?> source();

        /// Gets the [Class] this [`TypeAdapter`][io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter] will convert into.
        ///
        /// @return the class the [`TypeAdapter`][io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter]
        /// will convert into
        Class<?> target();

    }

    /// A class annotated with [Validator] will be automatically searched for and instantiated as a
    /// [`Validator`][io.github.kaktushose.jdac.dispatching.validation.Validator] by guice.
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

        /// Gets the annotation the [`Validator`][io.github.kaktushose.jdac.dispatching.validation.Validator]
        /// should be mapped to.
        ///
        /// @return the annotation the [`Validator`][io.github.kaktushose.jdac.dispatching.validation.Validator]
        /// should be mapped to
        Class<? extends Annotation> annotation();

    }
}
