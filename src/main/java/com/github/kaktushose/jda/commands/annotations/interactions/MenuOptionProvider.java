package com.github.kaktushose.jda.commands.annotations.interactions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this method is used to provide
 * {@link net.dv8tion.jda.api.interactions.components.selections.SelectOption SelectOptions} for a
 * {@link StringSelectMenu}
 *
 * <p>Therefore the method must be declared inside a class that is annotated with
 * {@link Interaction}.
 * Furthermore, the method signature has to meet the following conditions:
 * <ul>
 * <li>First parameter must be of type
 * {@link com.github.kaktushose.jda.commands.dispatching.interactions.components.OptionResolver OptionResolver}</li>
 * <li>Method must be public</li>
 * <li>Method must not have a return type</li>
 * </ul>
 * <br>Example:
 * <pre>
 * {@code
 * @MenuOptionProvider
 * public void onResolveOptions(OptionResolver resolver) {
 *      resolver.add(SelectOption.of("Option 1", "option-1").withDefault(true))
 *              .add("Option 2", "option-2");
 * }
 * }
 * </pre>
 * </p>
 *
 * @see com.github.kaktushose.jda.commands.dispatching.interactions.components.OptionResolver OptionResolver
 * @see DynamicOptions
 * @since 4.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MenuOptionProvider {
}
