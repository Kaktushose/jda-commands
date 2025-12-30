package io.github.kaktushose.jdac.annotations.interactions;

import net.dv8tion.jda.api.components.textinput.TextInputStyle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Annotation used to add TextInputs to [Modal]s.
///
/// @see Modal
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface TextInput {

    /// The label shown above this text input box.
    ///
    /// @return Placeholder
    String value();

    /// The placeholder of this TextInput.
    ///
    /// This is the short hint that describes the expected value of the TextInput field.
    ///
    /// @return Label for the input
    String placeholder() default "";

    /// The default value of this TextInput.
    ///
    /// This sets a pre-populated text for this TextInput field
    ///
    /// @return default value
    String defaultValue() default "";

    /// The minimum length. This is -1 if none has been set.
    ///
    /// @return Minimum length or -1
    int minValue() default -1;

    /// The maximum length. This is -1 if none has been set.
    ///
    /// @return Maximum length or -1
    int maxValue() default -1;

    /// The [TextInputStyle]. The default value is [TextInputStyle#PARAGRAPH].
    ///
    /// @return The TextInputStyle
    TextInputStyle style() default TextInputStyle.PARAGRAPH;

    /// Whether this TextInput is required.
    ///
    /// If this is True, the user must populate this TextInput field before they can submit the Modal.
    ///
    /// @return True if this TextInput is required
    /// @see net.dv8tion.jda.api.components.textinput.TextInput#isRequired()
    boolean required() default true;

}
