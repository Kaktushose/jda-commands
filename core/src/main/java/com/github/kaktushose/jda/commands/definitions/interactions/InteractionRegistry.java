package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;

import static com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition.CooldownDefinition;

/// Central registry for all [InteractionDefinition]s.
public record InteractionRegistry(Validators validators,
                                  LocalizationFunction localizationFunction,
                                  Descriptor descriptor,
                                  Set<Definition> definitions
) {

    private static final Logger log = LoggerFactory.getLogger(InteractionRegistry.class);

    /// Constructs a new [InteractionRegistry]
    ///
    /// @param registry   the corresponding [Validators]
    /// @param function   the [LocalizationFunction] to use
    /// @param descriptor the [Descriptor] to use
    public InteractionRegistry(Validators registry, LocalizationFunction function, Descriptor descriptor) {
        this(registry, function, descriptor, new HashSet<>());
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

    private Collection<Definition> indexInteractionClass(ClassDescription clazz, CommandDefinition.CommandConfig globalCommandConfig, Collection<AutoCompleteDefinition> autoCompletes) {
        var interaction = clazz.annotation(Interaction.class).orElseThrow();

        final Set<String> permissions = clazz.annotation(Permissions.class).map(value -> Set.of(value.value())).orElseGet(Set::of);
        // get controller level cooldown and use it if no command level cooldown is present
        CooldownDefinition cooldown = clazz.annotation(Cooldown.class).map(CooldownDefinition::build).orElse(null);

        // index interactions
        return interactionDefinitions(
                clazz,
                validators,
                localizationFunction,
                interaction,
                permissions,
                cooldown,
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
                .filter(it -> it.annotation(AutoComplete.class).isPresent())
                .map(method -> AutoCompleteDefinition.build(clazz, method))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Set<Definition> interactionDefinitions(ClassDescription clazz,
                                                   Validators validators,
                                                   LocalizationFunction localizationFunction,
                                                   Interaction interaction,
                                                   Set<String> permissions,
                                                   @Nullable CooldownDefinition cooldown,
                                                   Collection<AutoCompleteDefinition> autocompletes,
                                                   CommandDefinition.CommandConfig globalCommandConfig) {
        Set<Definition> definitions = new HashSet<>(autocompletes);
        for (MethodDescription method : clazz.methods()) {
            final MethodBuildContext context = new MethodBuildContext(
                    validators,
                    localizationFunction,
                    interaction,
                    permissions,
                    cooldown,
                    clazz,
                    method,
                    autocompletes,
                    globalCommandConfig
            );

            Optional<? extends Definition> definition = Optional.empty();
            // index commands
            if (method.annotation(Command.class).isPresent()) {
                Command command = method.annotation(Command.class).get();
                definition = switch (command.type()) {
                    case SLASH -> SlashCommandDefinition.build(context);
                    case USER, MESSAGE -> ContextCommandDefinition.build(context);
                    case UNKNOWN -> Optional.empty();
                };
            }

            // index components
            if (method.annotation(Button.class).isPresent()) {
                definition = ButtonDefinition.build(context);
            }
            if (method.annotation(EntitySelectMenu.class).isPresent()) {
                definition = EntitySelectMenuDefinition.build(context);
            }
            if (method.annotation(StringSelectMenu.class).isPresent()) {
                definition = StringSelectMenuDefinition.build(context);
            }

            //index modals
            if (method.annotation(Modal.class).isPresent()) {
                definition = ModalDefinition.build(context);
            }

            definition.ifPresent(it -> log.debug("Found interaction: {}", it));
            definition.ifPresent(definitions::add);
        }
        return definitions;
    }

    /// Attempts to find a [Definition] of type [T] based on the given [Predicate].
    ///
    /// @param type          the type of the [Definition] to find
    /// @param internalError `true` if the [Definition] must be found and not finding it
    ///                                                                indicates a framework bug
    /// @param predicate     the [Predicate] used to find the [Definition]
    /// @param <T>           a subtype of [Definition]
    /// @return [T]
    /// @throws IllegalStateException    if no [Definition] was found, although this mandatory should have been the case.
    ///                                                                                                    This is a rare occasion and can be considered a framework bug
    /// @throws IllegalArgumentException if no [Definition] was found, because the [Predicate] didn't include any elements
    public <T extends Definition> T find(Class<T> type, boolean internalError, Predicate<T> predicate) {
        return definitions.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .filter(predicate)
                .findFirst()
                .orElseThrow(() -> internalError
                        ? new IllegalStateException("No interaction found! Please report this error to the devs of jda-commands.")
                        : new IllegalArgumentException("No interaction found! Please check that the referenced interaction method exists.")
                );
    }

    /// Attempts to find all [Definition]s of type [T] based on the given [Predicate].
    ///
    /// @param type      the type of the [Definition] to find
    ///                                                        indicates a framework bug
    /// @param predicate the [Predicate] used to find the [Definition]s
    /// @param <T>       a subtype of [Definition]
    /// @return a possibly-empty [Collection] of all [Definition]s that match the given [Predicate]
    /// @throws IllegalStateException    if no [Definition] was found, although this mandatory should have been the case.
    ///                                                                                                    This is a rare occasion and can be considered a framework bug
    /// @throws IllegalArgumentException if no [Definition] was found, because the [Predicate] didn't include any elements
    public <T extends Definition> Collection<T> find(Class<T> type, Predicate<T> predicate) {
        return definitions.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .filter(predicate)
                .toList();
    }
}
