package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.annotations.Inject;
import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.command.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.ValidatorRegistry;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.*;
import java.util.function.Predicate;

import static com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition.CooldownDefinition;

/// Central registry for all [InteractionDefinition]s.
public record InteractionRegistry(DependencyInjector dependencyInjector,
                                  ValidatorRegistry validatorRegistry,
                                  LocalizationFunction localizationFunction,
                                  Descriptor descriptor,
                                  Set<Definition> definitions
) {

    private static final Logger log = LoggerFactory.getLogger(InteractionRegistry.class);

    /// Constructs a new [InteractionRegistry]
    ///
    /// @param injector   the [DependencyInjector] to use
    /// @param registry   the corresponding [ValidatorRegistry]
    /// @param function   the [LocalizationFunction] to use
    /// @param descriptor the [Descriptor] to use
    public InteractionRegistry(DependencyInjector injector, ValidatorRegistry registry, LocalizationFunction function, Descriptor descriptor) {
        this(injector, registry, function, descriptor, new HashSet<>());
    }

    /// Scans all given classes and registers the interactions defined in them.
    ///
    /// @param classes the [Class]es to build the interactions from
    public void index(Iterable<Class<?>> classes) {
        int oldSize = definitions.size();

        int count = 0;
        for (Class<?> clazz : classes) {
            log.debug("Found interaction controller {}", clazz.getName());
            definitions.addAll(indexInteractionClass(descriptor.apply(clazz)));
            count++;
        }

        log.debug("Successfully registered {} interaction controller(s) with a total of {} interaction(s)!",
                count,
                definitions.size() - oldSize);
    }

    private Collection<Definition> indexInteractionClass(ClassDescription clazz) {
        var interaction = clazz.annotation(Interaction.class).orElseThrow();

        var fields = Arrays.stream(clazz.clazz().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .toList();
        dependencyInjector.registerDependencies(clazz.clazz(), fields);

        final Set<String> permissions = clazz.annotation(Permissions.class).map(value -> Set.of(value.value())).orElseGet(Set::of);
        // get controller level cooldown and use it if no command level cooldown is present
        CooldownDefinition cooldown = clazz.annotation(Cooldown.class).map(CooldownDefinition::build).orElse(null);

        var autoCompletes = autoCompleteDefinitions(clazz);

        // index interactions
        var interactionDefinitions = interactionDefinitions(
                clazz,
                validatorRegistry,
                localizationFunction,
                interaction,
                permissions,
                cooldown,
                autoCompletes
        );

        // validate auto completes
        var commandDefinitions = interactionDefinitions.stream()
                .filter(SlashCommandDefinition.class::isInstance)
                .map(SlashCommandDefinition.class::cast)
                .toList();

        autoCompletes.stream()
                .map(AutoCompleteDefinition::commands)
                .flatMap(Collection::stream)
                .filter(name -> commandDefinitions.stream().noneMatch(command -> command.name().startsWith(name)))
                .forEach(s -> log.warn("No Command found for auto complete {}", s));

        return interactionDefinitions;
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
                                                   ValidatorRegistry validatorRegistry,
                                                   LocalizationFunction localizationFunction,
                                                   Interaction interaction,
                                                   Set<String> permissions,
                                                   CooldownDefinition cooldown,
                                                   Collection<AutoCompleteDefinition> autocompletes) {
        Set<Definition> definitions = new HashSet<>(autocompletes);
        for (MethodDescription method : clazz.methods()) {
            final MethodBuildContext context = new MethodBuildContext(
                    validatorRegistry,
                    localizationFunction,
                    interaction,
                    permissions,
                    cooldown,
                    clazz,
                    method,
                    autocompletes
            );

            Optional<? extends Definition> definition = Optional.empty();
            // index commands
            if (method.annotation(SlashCommand.class).isPresent()) {
                definition = SlashCommandDefinition.build(context);
            }
            if (method.annotation(ContextCommand.class).isPresent()) {
                definition = ContextCommandDefinition.build(context);
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

            definition.ifPresent(definitions::add);
        }
        return definitions;
    }

    /// Attempts to find a [Definition] of type [T] based on the given [Predicate].
    ///
    /// @param type          the type of the [Definition] to find
    /// @param internalError `true` if the [Definition] must be found and not finding it
    ///                      indicates a framework bug
    /// @param predicate     the [Predicate] used to find the [Definition]
    /// @param <T>           a subtype of [Definition]
    /// @return [T]
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
                        ? new IllegalStateException("No interaction found! Please report this error the the devs of jda-commands.")
                        : new IllegalArgumentException("No interaction found! Please check that the referenced interaction method exists.")
                );
    }

    /// Attempts to find all [Definition]s of type [T] based on the given [Predicate].
    ///
    /// @param type          the type of the [Definition] to find
    ///                      indicates a framework bug
    /// @param predicate     the [Predicate] used to find the [Definition]s
    /// @param <T>           a subtype of [Definition]
    /// @return a possibly-empty [Collection] of all [Definition]s that match the given [Predicate]
    /// @throws IllegalStateException    if no [Definition] was found, although this mandatory should have been the case.
    ///                                  This is a rare occasion and can be considered a framework bug
    /// @throws IllegalArgumentException if no [Definition] was found, because the [Predicate] didn't include any elements
    public <T extends Definition> Collection<T> find(Class<T> type, Predicate<T> predicate) {
        return definitions.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .filter(predicate)
                .toList();
    }
}
