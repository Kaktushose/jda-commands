package io.github.kaktushose.jdac.dispatching.validation;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.annotations.constraints.Constraint;
import io.github.kaktushose.jdac.annotations.constraints.NotPerm;
import io.github.kaktushose.jdac.annotations.constraints.Perm;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.introspection.internal.IntrospectionAccess;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.lang.annotation.Annotation;

/// Validators check if a command option fulfills the given constraint.
///
/// Register them at the [JDACBuilder#validator(Class, Validator)()] or use the
/// [`@Implementation.Validator`]({@docRoot}/io.github.kaktushose.jda.commands.extension.guice/com/github/kaktushose/jda/commands/guice/Implementation.Validator.html)
/// annotation of the guice extension.
///
/// ### Example
/// ```java
/// @Target(ElementType.PARAMETER)
/// @Retention(RetentionPolicy.RUNTIME)
/// @Constraint(String.class)
/// public @interface MaxString {
///     int value();
/// }
///
/// public class MaxStringLengthValidator implements Validator<String, MaxString> {
///
///     @Override
///     public boolean apply(String argument, MaxString annotation, Context ctx) {
///         if (argument.length() < maxString.value()) {
///             context.fail("The given String is too long");
///         }
///     }
/// }
/// ```
///
/// ### [Perm] and [NotPerm] Validators localization
/// The fail messages of the two default [Validator]s for [Perm] and [NotPerm]
/// can be localized with the localization keys `validator.noperm.fail` and
/// `validator.perm.fail` respectively.
/// @see Constraint
@FunctionalInterface
public interface Validator<T, A extends Annotation> {

    /// Validates an argument.
    ///
    /// If the parameter doesn't pass the validation, you can cancel this interaction by invoking
    /// [Context#fail(String, Entry...)] with an appropriated error message.
    ///
    /// @param argument   the argument to validate
    /// @param annotation the corresponding annotation
    /// @param context    the corresponding [InvocationContext]
    @io.github.kaktushose.jdac.annotations.IntrospectionAccess(Stage.INTERACTION)
    void apply(T argument, A annotation, Context context);

    /// This context provides access to the [InvocationContext] of this interaction and
    /// has some utility methods useful for error messages.
    class Context {
        private final InvocationContext<?> invocationContext;
        private final ErrorMessageFactory errorMessageFactory;

        /// @param invocationContext   the interactions [InvocationContext]
        /// @param errorMessageFactory the [ErrorMessageFactory] to be used to construct the cancel message
        public Context(InvocationContext<?> invocationContext, ErrorMessageFactory errorMessageFactory) {
            this.invocationContext = invocationContext;
            this.errorMessageFactory = errorMessageFactory;
        }

        /// @return the [InvocationContext] of this interaction
        public InvocationContext<?> invocationContext() {
            return invocationContext;
        }

        /// This method returns a formatted, optionally resolved (and localized), error message based on [ErrorMessageFactory#getConstraintFailedMessage(ErrorMessageFactory.ErrorContext, String)].
        ///
        /// @param content     the message or localization key
        /// @param placeholder the variables used for localization
        ///
        /// @see MessageResolver
        /// @see I18n
        public MessageCreateData failMessage(String content, Entry... placeholder) {
            String localized = IntrospectionAccess.scopedMessageResolver()
                    .resolve(content, IntrospectionAccess.scopedUserLocale(), Entry.toMap(placeholder));

            return errorMessageFactory.getConstraintFailedMessage(invocationContext, localized);
        }

        /// Used to fail a validation and cancel the [InvocationContext]
        ///
        /// @param failMessage the message or localization key
        /// @param placeholder the variables used for localization
        /// @see InvocationContext#cancel(MessageCreateData)
        public void fail(String failMessage, Entry... placeholder) {
            invocationContext.cancel(failMessage(failMessage, placeholder));
        }
    }

}
