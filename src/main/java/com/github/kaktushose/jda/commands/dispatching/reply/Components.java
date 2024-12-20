package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu;
import com.github.kaktushose.jda.commands.dispatching.Runtime;

import java.util.Arrays;

/// Represents a component, namely [Button], [StringSelectMenu] or [EntitySelectMenu], that should be added to a reply.
///
/// Also holds the following two settings:
/// - enabled:
///
/// to enable or disable the component
/// - independent:
///
/// whether the component should be executed in the same [Runtime] as the command it is bound to or not. If `true`,
/// every component interaction will create a new [Runtime]. Furthermore, the component cannot expire and will always
/// get executed, even after a bot restart.
///
/// ### Example:
/// ```
/// @SlashCommand("example command")
/// public void onCommand(CommandEvent event) {
///     event.with().components(Components.of(true, false, "onButton")).reply();
/// }
///```
/// @since 2.3.0
public record Components(boolean enabled, boolean independent, String name) {

    /// Adds disabled, runtime-bound [Components] to the reply.
    ///
    /// @param components the name of the method that represents the component
    public static Components[] disabled(String... components) {
        return of(false, false, components);
    }

    /// Adds enabled, runtime-independent [Components] to the reply.
    ///
    /// Every component interaction will create a new [Runtime]. Furthermore, the component cannot expire and
    /// will always get executed, even after a bot restart.
    ///
    /// @param components the name of the method that represents the component
    public static Components[] independent(String... components) {
        return of(true, true, components);
    }

    /// Adds [Components] with the passed configuration to the reply.
    ///
    /// @param enabled     whether the [Components] should be enabled or disabled
    /// @param independent whether the [Components] should be runtime-bound or independent
    /// @param components  the name of the method that represents the component
    public static Components[] of(boolean enabled, boolean independent, String... components) {
        return Arrays.stream(components)
                .map(it -> new Components(enabled, independent, it))
                .toArray(Components[]::new);
    }
}
