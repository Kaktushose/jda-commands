package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.component.ButtonComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.component.UnspecificComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.component.menu.EntitySelectMenuComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.component.menu.SelectMenuComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.component.menu.StringSelectComponent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;

import java.util.function.Function;

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
public abstract sealed class Component<Self extends Component<Self, T, D>, T extends ActionComponent, D extends ComponentDefinition<T>> permits SelectMenuComponent, ButtonComponent, UnspecificComponent {
    private boolean enabled = true;
    private boolean independent = false;
    private Function<T, T> callback = Function.identity();

    private final String method;
    private final Class<?> origin;

    public Component(String method, Class<?> origin) {
        this.method = method;
        this.origin = origin;
    }

    public Self enabled(boolean enabled) {
        this.enabled = enabled;
        return self();
    }

    public Self independent(boolean independent) {
        this.independent = independent;
        return self();
    }

    public Self modify(Function<T, T> callback) {
        this.callback = callback;
        return self();
    }

    protected boolean enabled() {
        return enabled;
    }

    protected boolean independent() {
        return independent;
    }

    protected String name() {
        return method;
    }

    protected Class<?> origin() {
        return origin;
    }

    protected Function<T, T> callback() {return callback;}

    @SuppressWarnings("unchecked")
    protected Self self() {
        return (Self) this;
    }

    protected abstract Class<D> definitionClass();
    protected abstract D build(D definition);


    /// Adds an enabled, runtime-bound [Component] to the reply.
    ///
    /// @param component the name of the method that represents the component
    public static Component<?, ?, ?> enabled(String component) {
        return of(true, false, component);
    }

    /// Adds disabled, runtime-bound [Component]s to the reply.
    ///
    /// @param component the name of the method that represents the component
    public static Component<?, ?, ?> disabled(String component) {
        return of(false, false, component);
    }

    /// Adds an enabled, runtime-independent [Component] to the reply.
    ///
    /// Every component interaction will create a new [`Runtime`]({@docRoot}/index.html#runtime-concept-heading). Furthermore, the component cannot expire and
    /// will always get executed, even after a bot restart.
    ///
    /// @param component the name of the method that represents the component
    public static Component<?, ?, ?> independent(String component) {
        return of(true, true, component);
    }

    /// Adds an enabled [Component] to the reply that is defined in a different class. This [Component] will always be
    /// runtime-independent.
    ///
    /// @param origin    the [Class] the `component` is defined in
    /// @param component the name of the method that represents the component
    public static Component<?, ?, ?> enabled(Class<?> origin, String component) {
        return of(true, origin, component);
    }

    /// Adds a disabled [Component] to the reply that is defined in a different class. This [Component] will always be
    /// runtime-independent.
    ///
    /// @param origin    the [Class] the `component` is defined in
    /// @param component the name of the method that represents the component
    public static Component<?, ?, ?> disabled(Class<?> origin, String component) {
        return of(false, origin, component);
    }

    /// Adds [Component]s with the passed configuration to the reply.
    ///
    /// @param enabled     whether the [Component] should be enabled or disabled
    /// @param independent whether the [Component] should be runtime-bound or independent
    /// @param component   the name of the method that represents the component
    public static Component<?, ?, ?> of(boolean enabled, boolean independent, String component) {
        return new UnspecificComponent(enabled, independent, component, null);
    }

    /// Adds [Component]s with the passed configuration to the reply.
    ///
    /// @param enabled   whether the [Component] should be enabled or disabled
    /// @param origin    the [Class] the `component` is defined in
    /// @param component the name of the method that represents the component
    public static Component<?, ?, ?> of(boolean enabled, Class<?> origin, String component) {
        return new UnspecificComponent(enabled, true, component, origin);
    }

    public static Component<?, ?, ?> of(boolean enabled, boolean independent, Class<?> origin, String component) {
        return new UnspecificComponent(enabled, independent, component, origin);
    }

    public static ButtonComponent button(Class<?> origin, String method) {
        return new ButtonComponent(method, origin);
    }

    public static ButtonComponent button(String method) {
        return button(null, method);
    }

    public static EntitySelectMenuComponent entitySelect(Class<?> origin, String method) {
        return new EntitySelectMenuComponent(method, origin);
    }

    public static EntitySelectMenuComponent entitySelect(String method) {
        return entitySelect(null, method);
    }

    public static StringSelectComponent stringSelect(Class<?> origin, String method) {
        return new StringSelectComponent(method, origin);
    }

    public static StringSelectComponent stringSelect(String method) {
        return stringSelect(null, method);
    }
}
