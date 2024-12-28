package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.annotations.Inject;
import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.ModalDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.menu.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.menu.StringSelectMenuDefinition;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.*;
import java.util.function.Predicate;

import static com.github.kaktushose.jda.commands.definitions.interactions.impl.command.SlashCommandDefinition.CooldownDefinition;

public final class InteractionRegistry {

    private static final Logger log = LoggerFactory.getLogger(InteractionRegistry.class);
    private final DependencyInjector dependencyInjector;
    private final ValidatorRegistry validatorRegistry;
    private final LocalizationFunction localizationFunction;
    private final Descriptor descriptor;
    private final Set<Definition> definitions = new HashSet<>();

    public InteractionRegistry(DependencyInjector dependencyInjector, ValidatorRegistry validatorRegistry, LocalizationFunction localizationFunction, Descriptor descriptor) {
        this.dependencyInjector = dependencyInjector;
        this.validatorRegistry = validatorRegistry;
        this.localizationFunction = localizationFunction;
        this.descriptor = descriptor;
    }

    public void index(Iterable<Class<?>> classes) {
        int oldSize = definitions.size();

        int c = 0;
        for (Class<?> clazz : classes) {
            log.debug("Found interaction controller {}", clazz.getName());
            definitions.addAll(indexInteractionClass(descriptor.apply(clazz)));
            c++;
        }

        log.debug("Successfully registered {} interaction controller(s) with a total of {} interaction(s)!",
                c,
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
                .map(method -> Optional.ofNullable(AutoCompleteDefinition.build(clazz, method)))
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

            Optional<Definition> definition = Optional.empty();
            // index commands
            if (method.annotation(SlashCommand.class).isPresent()) {
                definition = SlashCommandDefinition.build(context);
            }
            if (method.annotation(ContextCommand.class).isPresent()) {
                definition = CommandDefinition.build(context);
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

    public <T extends Definition> Collection<T> find(Class<T> type, Predicate<T> predicate) {
        return definitions.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .filter(predicate)
                .toList();
    }
}
