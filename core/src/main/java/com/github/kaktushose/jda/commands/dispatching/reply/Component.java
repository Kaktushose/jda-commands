package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu;
import com.github.kaktushose.jda.commands.dispatching.reply.dynamic.ButtonBuilder;
import com.github.kaktushose.jda.commands.dispatching.reply.dynamic.ComponentBuilder;
import com.github.kaktushose.jda.commands.dispatching.reply.dynamic.EntitySelectMenuBuilder;
import com.github.kaktushose.jda.commands.dispatching.reply.dynamic.StringSelectMenuBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/// Represents a component, namely [Button], [StringSelectMenu] or [EntitySelectMenu], that should be added to a reply.
///
/// Also holds the following two settings:
/// - enabled:
///
/// to enable or disable the component
/// - independent:
///
/// whether the component should be executed in the same [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) as the command it is bound to or not. If `true`,
/// every component interaction will create a new [`Runtime`]({@docRoot}/index.html#runtime-concept-heading). Furthermore, the component cannot expire and will always
/// get executed, even after a bot restart.
///
/// ### Example:
/// ```
/// @SlashCommand("example command")
/// public void onCommand(CommandEvent event) {
///     event.with().components(Components.of(true, false, "onButton")).reply();
/// }
///```
public record Component<T extends ComponentBuilder>(boolean enabled,
                           boolean independent,
                           @NotNull String name,
                           @Nullable Class<?> origin,
                           @NotNull Consumer<T> consumer) {

    /// Adds an enabled, runtime-bound [Component] to the reply.
    ///
    /// @param component the name of the method that represents the component
    public static Component<ComponentBuilder> enabled(String component) {
        return of(true, false, component);
    }

    /// Adds disabled, runtime-bound [Component]s to the reply.
    ///
    /// @param component the name of the method that represents the component
    public static Component<ComponentBuilder> disabled(String component) {
        return of(false, false, component);
    }

    /// Adds an enabled, runtime-independent [Component] to the reply.
    ///
    /// Every component interaction will create a new [`Runtime`]({@docRoot}/index.html#runtime-concept-heading). Furthermore, the component cannot expire and
    /// will always get executed, even after a bot restart.
    ///
    /// @param component the name of the method that represents the component
    public static Component<ComponentBuilder> independent(String component) {
        return of(true, true, component);
    }

    /// Adds an enabled [Component] to the reply that is defined in a different class. This [Component] will always be
    /// runtime-independent.
    ///
    /// @param origin    the [Class] the `component` is defined in
    /// @param component the name of the method that represents the component
    public static Component<ComponentBuilder> enabled(Class<?> origin, String component) {
        return of(true, origin, component);
    }

    /// Adds a disabled [Component] to the reply that is defined in a different class. This [Component] will always be
    /// runtime-independent.
    ///
    /// @param origin    the [Class] the `component` is defined in
    /// @param component the name of the method that represents the component
    public static Component<ComponentBuilder> disabled(Class<?> origin, String component) {
        return of(false, origin, component);
    }

    /// Adds [Component]s with the passed configuration to the reply.
    ///
    /// @param enabled     whether the [Component] should be enabled or disabled
    /// @param independent whether the [Component] should be runtime-bound or independent
    /// @param component   the name of the method that represents the component
    public static Component<ComponentBuilder> of(boolean enabled, boolean independent, String component) {
        return new Component<>(enabled, independent, component, null, _ -> {});
    }

    /// Adds [Component]s with the passed configuration to the reply.
    ///
    /// @param enabled   whether the [Component] should be enabled or disabled
    /// @param origin    the [Class] the `component` is defined in
    /// @param component the name of the method that represents the component
    public static Component<ComponentBuilder> of(boolean enabled, Class<?> origin, String component) {
        return new Component<>(enabled, true, component, origin, _ -> {});
    }

    /// Adds [Component]s with the passed configuration to the reply.
    ///
    /// @param enabled     whether the [Component] should be enabled or disabled
    /// @param independent whether the [Component] should be runtime-bound or independent
    /// @param origin      the [Class] the `component` is defined in
    /// @param component   the name of the method that represents the component
    public static Component<ComponentBuilder> of(boolean enabled, boolean independent, Class<?> origin, String component) {
        return new Component<>(enabled, independent, component, origin, _ -> {});
    }

    public static Component<ButtonBuilder> button(String component, Consumer<ButtonBuilder> consumer) {
        return new Component<>(true, false, component, null, consumer);
    }

    public static Component<StringSelectMenuBuilder> stringSelect(String component, Consumer<StringSelectMenuBuilder> consumer) {
        return new Component<>(true, false, component, null, consumer);
    }

    public static Component<EntitySelectMenuBuilder> entitySelect(String component, Consumer<EntitySelectMenuBuilder> consumer) {
        return new Component<>(true, false, component, null, consumer);
    }

}
