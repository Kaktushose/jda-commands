package io.github.kaktushose.jdac.annotations.interactions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/// Annotation used to add choices to command options.
///
/// # Static Choices
/// Most of the time, choices are static and can be passed to the annotation directly:
/// ```
/// public void onCommand(CommandEvent event, @Choices({"Apple", "Banana", "Cherry"}) String option) {...}
/// ```
/// The example above will use the given String for both the name and the value. You can use the name:value format to
/// specify both.
///
/// # Dynamic Choices
/// If needed, choices can also be provided by a public static method returning a `List<String>`.
/// ```
/// public void onCommand(CommandEvent event, @Choices(provider = "getChoices") String option) {...}
///
/// public static List<String> getChoices() {
///     return List.of("Apple", "Banana", "Cherry");
/// }
/// ```
/// Providers can also be defined in a different class than the command:
/// ```
/// public void onCommand(CommandEvent event, @Choices(source = Other.class, provider = "getChoices") String option) {...}
/// ```
/// If both static values and a provider is present, the values will be combined.
///
/// ## Dependency Injection
/// This static provider method also supports dependency injection via the Guice Extension.
/// ```
/// public static List<String> getChoices(MyChoiceProvider provider) {
///     return provider.getChoices();
/// }
/// ```
/// If the provider method is overloaded, all methods will be called and combined.
///
/// @see Command
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Choices {

    /// Returns the choices of a command option.
    ///
    /// @return the choices of the command option
    String[] value() default "";

    /// Returns the name of the choices provider method
    String provider() default "";

    /// Returns the class of the provider method
    Class<?> source() default Choices.class;

}
