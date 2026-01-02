package io.github.kaktushose.jdac.definitions.interactions;

import io.github.kaktushose.jdac.annotations.interactions.*;
import io.github.kaktushose.jdac.definitions.Definition;
import io.github.kaktushose.jdac.definitions.description.ClassDescription;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.ContextCommandDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.SlashCommandDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.ButtonDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.StringSelectMenuDefinition;
import io.github.kaktushose.jdac.dispatching.validation.internal.Validators;
import io.github.kaktushose.jdac.exceptions.InternalException;
import io.github.kaktushose.jdac.exceptions.InvalidDeclarationException;
import io.github.kaktushose.jdac.internal.logging.JDACLogger;
import io.github.kaktushose.jdac.introspection.Definitions;
import io.github.kaktushose.jdac.message.i18n.I18n;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Predicate;

/// Central registry for all [InteractionDefinition]s.
public record InteractionRegistry(Validators validators,
                                  I18n i18n,
                                  LocalizationFunction localizationFunction,
                                  Descriptor descriptor,
                                  Set<InteractionDefinition> definitions
) implements Definitions {

    private static final Logger log = JDACLogger.getLogger(InteractionRegistry.class);

    /// Constructs a new [InteractionRegistry]
    ///
    /// @param registry   the corresponding [Validators]
    /// @param function   the [LocalizationFunction] to use
    /// @param i18n       the [I18n] instance to use
    /// @param descriptor the [Descriptor] to use
    public InteractionRegistry(Validators registry, I18n i18n, LocalizationFunction function, Descriptor descriptor) {
        this(registry, i18n, function, descriptor, new HashSet<>());
    }

    /// Scans all given classes and registers the interactions defined in them.
    ///
    /// @param classes the [Class]es to build the interactions from
    public void index(Iterable<Class<?>> classes, CommandDefinition.CommandConfig globalCommandConfig) {
        int oldSize = definitions.size();

        var autoCompletes = indexAutoCompletes(classes);
        definitions.addAll(autoCompletes);

        int count = 0;
        for (Class<?> clazz : classes) {
            log.debug("Found controller: {}", clazz.getName());
            definitions.addAll(indexInteractionClass(descriptor.describe(clazz), globalCommandConfig, autoCompletes));
            count++;
        }

        // validate auto completes
        var commandDefinitions = definitions.stream()
                .filter(SlashCommandDefinition.class::isInstance)
                .map(SlashCommandDefinition.class::cast)
                .toList();
        autoCompletes.stream()
                .map(AutoCompleteDefinition::rules)
                .flatMap(Collection::stream)
                .filter(rule -> commandDefinitions.stream().noneMatch(command ->
                        command.name().startsWith(rule.command()) || command.methodDescription().name().equals(rule.command()))
                ).forEach(s -> log.warn("No slash commands found matching {}", s));

        log.debug("Successfully registered {} interaction controller(s) with a total of {} interaction(s)!",
                count,
                definitions.size() - oldSize);
    }

    private Collection<InteractionDefinition> indexInteractionClass(ClassDescription clazz, CommandDefinition.CommandConfig globalCommandConfig, Collection<AutoCompleteDefinition> autoCompletes) {
        var interaction = clazz.annotation(Interaction.class);

        final Set<String> permissions = clazz.findAnnotation(Permissions.class).map(value -> Set.of(value.value())).orElseGet(Set::of);

        // index interactions
        return interactionDefinitions(
                clazz,
                validators,
                localizationFunction,
                i18n,
                interaction,
                permissions,
                autoCompletes,
                globalCommandConfig
        );
    }

    private Collection<AutoCompleteDefinition> indexAutoCompletes(Iterable<Class<?>> classes) {
        var result = new ArrayList<AutoCompleteDefinition>();
        for (Class<?> clazz : classes) {
            result.addAll(autoCompleteDefinitions(descriptor.describe(clazz)));
        }
        return result;
    }

    private Collection<AutoCompleteDefinition> autoCompleteDefinitions(ClassDescription clazz) {
        return clazz.methods().stream()
                .filter(it -> it.findAnnotation(AutoComplete.class).isPresent())
                .map(method -> AutoCompleteDefinition.build(clazz, method))
                .toList();
    }


    private Set<InteractionDefinition> interactionDefinitions(ClassDescription clazz,
                                                   Validators validators,
                                                   LocalizationFunction localizationFunction,
                                                   I18n i18n,
                                                   Interaction interaction,
                                                   Set<String> permissions,
                                                   Collection<AutoCompleteDefinition> autocompletes,
                                                   CommandDefinition.CommandConfig globalCommandConfig) {
        Set<InteractionDefinition> definitions = new HashSet<>(autocompletes);
        for (MethodDescription method : clazz.methods()) {
            final MethodBuildContext context = new MethodBuildContext(
                    validators,
                    localizationFunction,
                    i18n,
                    interaction,
                    permissions,
                    clazz,
                    method,
                    autocompletes,
                    globalCommandConfig
            );


            ScopedValue.where(InvalidDeclarationException.CONTEXT, method).run(() -> {
                InteractionDefinition definition = construct(method, context);

                if (definition != null) {
                    log.debug("Found interaction: {}", definition);
                    definitions.add(definition);
                }
            });
        }
        return definitions;
    }

    @Nullable
    private InteractionDefinition construct(MethodDescription method, MethodBuildContext context) {
        // index commands
        if (method.hasAnnotation(Command.class)) {
            Command command = method.findAnnotation(Command.class).get();
            return switch (command.type()) {
                case SLASH -> SlashCommandDefinition.build(context);
                case USER, MESSAGE -> ContextCommandDefinition.build(context);
                case UNKNOWN -> throw new InvalidDeclarationException("unknown-command-type");
            };
        }

        // index components
        if (method.hasAnnotation(Button.class)) {
            return ButtonDefinition.build(context);
        }
        if (method.hasAnnotation(EntityMenu.class)) {
            return EntitySelectMenuDefinition.build(context);
        }
        if (method.hasAnnotation(StringMenu.class)) {
            return StringSelectMenuDefinition.build(context);
        }

        //index modals
        if (method.hasAnnotation(Modal.class)) {
            return ModalDefinition.build(context);
        }

        return null;
    }

    /// Attempts to find a [Definition] of type [T] based on the given [Predicate].
    ///
    /// @param type          the type of the [Definition] to find
    /// @param internalError `true` if the [Definition] must be found and not finding it
    ///                       indicates a framework bug
    /// @param predicate     the [Predicate] used to find the [Definition]
    /// @param <T>           a subtype of [Definition]
    /// @return [T]          the definition
    /// @throws IllegalStateException    if no [Definition] was found, although this mandatory should have been the case.
    ///                                  This is a rare occasion and can be considered a framework bug
    /// @throws IllegalArgumentException if no [Definition] was found, because the [Predicate] didn't include any elements
    public <T extends Definition> T find(Class<T> type, boolean internalError, Predicate<T> predicate) {
        return definitions.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .filter(predicate)
                .findFirst()
                .orElseThrow(() -> internalError
                        ? new InternalException("no-interaction-found")
                        : new IllegalArgumentException("No interaction found! Please check that the referenced interaction method exists.")
                );
    }

    @Override
    public <T extends Definition> T findFirst(Class<T> type, Predicate<T> predicate) {
        return find(type, false, predicate);
    }

    /// Attempts to find all [Definition]s of type [T] based on the given [Predicate].
    ///
    /// @param type      the type of the [Definition] to find
    /// @param predicate the [Predicate] used to find the [Definition]s
    /// @param <T>       a subtype of [Definition]
    /// @return a possibly-empty [Collection] of all [Definition]s that match the given [Predicate]
    /// @throws IllegalStateException    if no [Definition] was found, although this mandatory should have been the case.
    ///                                  This is a rare occasion and can be considered a framework bug
    /// @throws IllegalArgumentException if no [Definition] was found, because the [Predicate] didn't include any elements
    @Override
    public <T extends Definition> Collection<T> find(Class<T> type, Predicate<T> predicate) {
        return definitions.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .filter(predicate)
                .toList();
    }

    @Override
    public Collection<InteractionDefinition> all() {
        return Collections.unmodifiableSet(definitions);
    }
}
