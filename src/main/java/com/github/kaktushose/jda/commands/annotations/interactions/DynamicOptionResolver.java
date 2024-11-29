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
 * <li>Method must be public</li>
 * <li>Method must have a return type of Set<SelectOption></li>
 * <li>Method must not have parameters</li>
 * </ul>
 * <br>Example:
 * <pre>
 * {@code
 * @DynamicOptionResolver
 * public Set<SelectOption> onResolveOptions() {
 *      return Set.of(SelectOption.of("Pizza", "option-1"), SelectOption.of("Hamburger", "option-2"));
 * }
 * }
 * </pre>
 * </p>
 *
 * @see DynamicOptions
 * @since 4.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicOptionResolver {
}
