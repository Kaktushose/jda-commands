package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.component.ButtonComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.component.EntitySelectMenuComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public abstract sealed class Component<Self extends Component<Self, D>, D extends ComponentDefinition<?>> permits ButtonComponent, EntitySelectMenuComponent {
    protected boolean enabled;
    protected boolean independent;
    protected String name;
    protected Class<?> origin;

    public Self enabled(boolean enabled) {
        this.enabled = enabled;
        return self();
    }

    public Self independent(boolean independent) {
        this.independent = independent;
        return self();
    }

    public Self name(@NotNull String name) {
        this.name = name;
        return self();
    }

    public Self origin(@Nullable Class<?> origin) {
        this.origin = origin;
        return self();
    }

    protected boolean enabled() {
        return enabled;
    }

    protected boolean independent() {
        return independent;
    }

    protected String name() {
        return name;
    }

    protected Class<?> origin() {
        return origin;
    }

    @SuppressWarnings("unchecked")
    private Self self() {
        return (Self) this;
    }

    public abstract D build(D definition);
}
