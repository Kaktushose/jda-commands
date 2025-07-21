package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.dynamic.ButtonComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.dynamic.internal.UnspecificComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.dynamic.menu.EntitySelectMenuComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.dynamic.menu.SelectMenuComponent;
import com.github.kaktushose.jda.commands.dispatching.reply.dynamic.menu.StringSelectComponent;
import com.github.kaktushose.jda.commands.i18n.I18n;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

/// Represents a component, namely [Button], [StringSelectMenu] or [EntitySelectMenu], that should be added to a reply.
///
/// Also holds the following two settings:
/// ### enabled:
///
/// to enable or disable the component
/// ### independent:
///
/// whether the component should be executed in the same [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) as the command it is bound to or not. If `true`,
/// every component interaction will create a new [`Runtime`]({@docRoot}/index.html#runtime-concept-heading). Furthermore, the component cannot expire and will always
/// get executed, even after a bot restart.
///
/// ## Example:
/// ```java
/// @SlashCommand("example command")
/// public void onCommand(CommandEvent event) {
///     event.with().components(Components.of(true, false, "onButton")).reply();
/// }
/// ```
///
/// ## Component type specific implementations
/// If using one of:
/// - [Component#button(Class, String, I18n.Entry...)]
/// - [Component#entitySelect(Class, String, I18n.Entry...)]
/// - [Component#stringSelect(Class, String, I18n.Entry...)]
///
/// a specific implementation is returned which allows for further modification.
///
/// ### Example without localization
/// This example overrides the buttons label with "Modified Label"
/// ```java
/// @Command("example command")
/// public void onCommand(CommandEvent event) {
///     event.with().components(Components.button("onButton")
///                     .label("Modified Label"))
///                 .reply();
/// }
/// ```
///
/// ### Example with localization
/// ```java
/// @Bundle("example")
/// @Command("example command")
/// public void onCommand(CommandEvent event, User user) {
///     event.with().components(Components.button("onButton")
///                     .label("label", I18n.entry("name", user.getName())))
///                 .reply();
/// }
/// ```
///
/// @param <S> the concrete subtype of [Component]
/// @param <T> the type of [ActionComponent] the [ComponentDefinition] represents
/// @param <B> the type of builder the [ActionComponent uses]
/// @param <D> the type of [ComponentDefinition] this [Component] represents
/// @see ButtonComponent
/// @see StringSelectComponent
/// @see EntitySelectMenuComponent
public abstract sealed class Component<S extends Component<S, T, B, D>, T extends ActionComponent, B, D extends ComponentDefinition<T>>
        permits ButtonComponent, UnspecificComponent, SelectMenuComponent {
    private final I18n.Entry[] placeholder;
    private boolean enabled = true;
    private boolean independent = false;
    private Function<B, B> callback = Function.identity();

    private final String method;
    private final Class<?> origin;

    protected Component(String method, @Nullable Class<?> origin, I18n.Entry[] placeholder) {
        this.method = method;
        this.origin = origin;
        this.placeholder = placeholder;
    }

    /// @param enabled whether the component should be enabled
    /// @see ActionComponent#withDisabled(boolean)
    public S enabled(boolean enabled) {
        this.enabled = enabled;
        return self();
    }

    /// @param independent whether the [Component] should be runtime-bound or independent
    public S independent(boolean independent) {
        this.independent = independent;
        return self();
    }

    /// @param callback a [Function] that allows to modify the resulting jda object.
    ///                                 The passed function will be called after all modifications are made by jda-commands,
    ///                                 shortly before the component is registered in the message
    public S modify(Function<B, B> callback) {
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

    protected Optional<Class<?>> origin() {
        return Optional.ofNullable(origin);
    }

    protected Function<B, B> callback() {
        return callback;
    }

    protected I18n.Entry[] placeholder() {
        return placeholder;
    }

    @SuppressWarnings("unchecked")
    protected S self() {
        return (S) this;
    }

    protected abstract Class<D> definitionClass();

    protected abstract D build(D definition);


    /// Adds an enabled, runtime-bound [Component] to the reply.
    ///
    /// @param component the name of the method that represents the component
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, I18n.Entry...) ]
    public static Component<?, ?, ?, ?> enabled(String component, I18n.Entry... placeholder) {
        return of(true, false, component, placeholder);
    }

    /// Adds disabled, runtime-bound [Component]s to the reply.
    ///
    /// @param component the name of the method that represents the component
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, I18n.Entry...) ]
    public static Component<?, ?, ?, ?> disabled(String component, I18n.Entry... placeholder) {
        return of(false, false, component, placeholder);
    }

    /// Adds an enabled, runtime-independent [Component] to the reply.
    ///
    /// Every component interaction will create a new [`Runtime`]({@docRoot}/index.html#runtime-concept-heading).
    /// Furthermore, the component cannot expire and will always get executed, even after a bot restart.
    ///
    /// @param component the name of the method that represents the component
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, I18n.Entry...) ]
    public static Component<?, ?, ?, ?> independent(String component, I18n.Entry... placeholder) {
        return of(true, true, component, placeholder);
    }

    /// Adds an enabled [Component] to the reply that is defined in a different class. This [Component] will always be
    /// runtime-independent.
    ///
    /// @param origin    the [Class] the `component` is defined in
    /// @param component the name of the method that represents the component
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, I18n.Entry...) ]
    public static Component<?, ?, ?, ?> enabled(Class<?> origin, String component, I18n.Entry... placeholder) {
        return of(true, origin, component, placeholder);
    }

    /// Adds a disabled [Component] to the reply that is defined in a different class. This [Component] will always be
    /// runtime-independent.
    ///
    /// @param origin    the [Class] the `component` is defined in
    /// @param component the name of the method that represents the component
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, I18n.Entry...) ]
    public static Component<?, ?, ?, ?> disabled(Class<?> origin, String component, I18n.Entry... placeholder) {
        return of(false, origin, component, placeholder);
    }

    /// Adds a [Component] with the passed configuration to the reply.
    ///
    /// @param enabled     whether the [Component] should be enabled or disabled
    /// @param independent whether the [Component] should be runtime-bound or independent
    /// @param component   the name of the method that represents the component
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, I18n.Entry...) ]
    public static Component<?, ?, ?, ?> of(boolean enabled, boolean independent, String component, I18n.Entry... placeholder) {
        return new UnspecificComponent(enabled, independent, component, null, placeholder);
    }

    /// Adds a [Component] with the passed configuration to the reply.
    ///
    /// @param enabled   whether the [Component] should be enabled or disabled
    /// @param origin    the [Class] the `component` is defined in
    /// @param component the name of the method that represents the component
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, I18n.Entry...) ]
    public static Component<?, ?, ?, ?> of(boolean enabled, Class<?> origin, String component, I18n.Entry... placeholder) {
        return new UnspecificComponent(enabled, true, component, origin, placeholder);
    }

    /// Adds a [Component] with the passed configuration to the reply.
    ///
    /// @param enabled     whether the [Component] should be enabled or disabled
    /// @param independent whether the [Component] should be runtime-bound or independent
    /// @param origin      the [Class] the `component` is defined in
    /// @param component   the name of the method that represents the component
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, I18n.Entry...) ]
    public static Component<?, ?, ?, ?> of(boolean enabled, boolean independent, Class<?> origin, String component, I18n.Entry... placeholder) {
        return new UnspecificComponent(enabled, independent, component, origin, placeholder);
    }

    /// Adds a [ButtonComponent] to the reply.
    ///
    /// @param component the name of the method that represents the button
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, I18n.Entry...) ]
    public static ButtonComponent button(String component, I18n.Entry... placeholder) {
        return button(null, component, placeholder);
    }

    /// Adds a [ButtonComponent] to the reply.
    ///
    /// @param origin    the [Class] the `component` is defined in
    /// @param component the name of the method that represents the button
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, I18n.Entry...) ]
    public static ButtonComponent button(Class<?> origin, String component, I18n.Entry... placeholder) {
        return new ButtonComponent(component, origin, placeholder);
    }

    /// Adds an [EntitySelectMenuComponent] to the reply.
    ///
    /// @param component the name of the method that represents the entity select menu
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, I18n.Entry...) ]
    public static EntitySelectMenuComponent entitySelect(String component, I18n.Entry... placeholder) {
        return entitySelect(null, component, placeholder);
    }
    /// Adds an [EntitySelectMenuComponent] to the reply.
    ///
    /// @param origin    the [Class] the `menu` is defined in
    /// @param component the name of the method that represents the entity select menu
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, I18n.Entry...) ]
    public static EntitySelectMenuComponent entitySelect(Class<?> origin, String component, I18n.Entry... placeholder) {
        return new EntitySelectMenuComponent(component, origin, placeholder);
    }

    /// Adds a [StringSelectComponent] to the reply.
    ///
    /// @param component the name of the method that represents the string select menu
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, I18n.Entry...) ]
    public static StringSelectComponent stringSelect(String component, I18n.Entry... placeholder) {
        return stringSelect(null, component, placeholder);
    }

    /// Adds a [StringSelectComponent] to the reply.
    ///
    /// @param origin    the [Class] the `component` is defined in
    /// @param component the name of the method that represents the string select menu
    /// @param placeholder the placeholders to use to perform localization, see [I18n#localize(Locale, String, I18n.Entry...) ]
    public static StringSelectComponent stringSelect(Class<?> origin, String component, I18n.Entry... placeholder) {
        return new StringSelectComponent(component, origin, placeholder);
    }

}
