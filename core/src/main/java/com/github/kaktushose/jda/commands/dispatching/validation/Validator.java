package com.github.kaktushose.jda.commands.dispatching.validation;

import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

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
///     String message() default "The given String is too long";
/// }
///
/// public class MaxStringLengthValidator implements Validator {
///
///     @Override
///     public boolean apply(Object argument, Object annotation, InvocationContext<? context) {
///         MaxString maxString = (MaxString) annotation;
///         return String.valueOf(argument).length() < maxString.value();
///     }
/// }
/// ```
/// @see Constraint
@FunctionalInterface
public interface Validator<T, A extends Annotation> {

    /// Validates an argument.
    ///
    /// If the parameter doesn't pass the validation, you can cancel this interaction by invoking
    /// [InvocationContext#cancel(MessageCreateData)] with an appropriated error message.
    ///
    /// @param argument   the argument to validate
    /// @param annotation the corresponding annotation
    /// @param context    the corresponding [InvocationContext]
    void apply(@NotNull T argument, @NotNull A annotation, @NotNull Context context);

    class Context {
        private final InvocationContext<?> invocationContext;
        private final ErrorMessageFactory errorMessageFactory;

        public Context(InvocationContext<?> invocationContext, ErrorMessageFactory errorMessageFactory) {
            this.invocationContext = invocationContext;
            this.errorMessageFactory = errorMessageFactory;
        }

        public InvocationContext<?> invocationContext() {
            return invocationContext;
        }

        public MessageCreateData failMessage(String content) {
            return errorMessageFactory.getConstraintFailedMessage(invocationContext, content);
        }

        public void cancel(String failMessage) {
            invocationContext.cancel(failMessage(failMessage));
        }
    }

}
