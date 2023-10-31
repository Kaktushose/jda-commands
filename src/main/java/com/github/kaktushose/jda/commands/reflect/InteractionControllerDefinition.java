package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.Inject;
import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.ButtonDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.ModalDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.GenericCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.GenericSelectMenuDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.StringSelectMenuDefinition;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Representation of an interaction controller.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 2.0.0
 */
public class InteractionControllerDefinition {

    private static final Logger log = LoggerFactory.getLogger(InteractionControllerDefinition.class);
    private final List<GenericCommandDefinition> commands;
    private final List<ButtonDefinition> buttons;
    private final List<GenericSelectMenuDefinition<? extends SelectMenu>> selectMenus;
    private final List<AutoCompleteDefinition> autoCompletes;
    private final List<ModalDefinition> modals;

    private InteractionControllerDefinition(List<GenericCommandDefinition> commands,
                                            List<ButtonDefinition> buttons,
                                            List<GenericSelectMenuDefinition<? extends SelectMenu>> selectMenus,
                                            List<AutoCompleteDefinition> autoCompletes,
                                            List<ModalDefinition> modals) {
        this.commands = commands;
        this.buttons = buttons;
        this.selectMenus = selectMenus;
        this.autoCompletes = autoCompletes;
        this.modals = modals;
    }

    /**
     * Builds a new ControllerDefinition.
     *
     * @param interactionClass     the {@link Class} of the controller
     * @param validatorRegistry    the corresponding {@link ValidatorRegistry}
     * @param dependencyInjector   the corresponding {@link DependencyInjector}
     * @param localizationFunction the {@link LocalizationFunction} to use
     * @return an {@link Optional} holding the ControllerDefinition
     */
    public static Optional<InteractionControllerDefinition> build(@NotNull Class<?> interactionClass,
                                                                  @NotNull ValidatorRegistry validatorRegistry,
                                                                  @NotNull DependencyInjector dependencyInjector,
                                                                  @NotNull LocalizationFunction localizationFunction) {
        Interaction interaction = interactionClass.getAnnotation(Interaction.class);

        if (!interaction.isActive()) {
            log.warn("Interaction class {} is set inactive. Skipping the controller and its commands", interactionClass.getName());
            return Optional.empty();
        }

        List<Field> fields = new ArrayList<>();
        for (Field field : interactionClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class)) {
                continue;
            }
            fields.add(field);
        }
        dependencyInjector.registerDependencies(interactionClass, fields);

        Set<String> permissions = new HashSet<>();
        // index controller level permissions
        if (interactionClass.isAnnotationPresent(Permissions.class)) {
            Permissions permission = interactionClass.getAnnotation(Permissions.class);
            permissions = Arrays.stream(permission.value()).collect(Collectors.toSet());
        }

        // get controller level cooldown and use it if no command level cooldown is present
        CooldownDefinition cooldown = null;
        if (interactionClass.isAnnotationPresent(Cooldown.class)) {
            cooldown = CooldownDefinition.build(interactionClass.getAnnotation(Cooldown.class));
        }

        // index interactions
        List<GenericCommandDefinition> commands = new ArrayList<>();
        List<ButtonDefinition> buttons = new ArrayList<>();
        List<GenericSelectMenuDefinition<? extends SelectMenu>> selectMenus = new ArrayList<>();
        List<AutoCompleteDefinition> autoCompletes = new ArrayList<>();
        List<ModalDefinition> modals = new ArrayList<>();
        for (Method method : interactionClass.getDeclaredMethods()) {

            // index commands
            if (method.isAnnotationPresent(SlashCommand.class)) {
                Optional<SlashCommandDefinition> optional = SlashCommandDefinition.build(method, validatorRegistry, localizationFunction);
                if (optional.isEmpty()) {
                    continue;
                }
                SlashCommandDefinition commandDefinition = optional.get();
                commandDefinition.getPermissions().addAll(permissions);
                if (commandDefinition.getCooldown().getDelay() == 0) {
                    commandDefinition.getCooldown().set(cooldown);
                }
                if (interaction.ephemeral()) {
                    commandDefinition.setEphemeral(true);
                }
                commands.add(commandDefinition);
            }
            if (method.isAnnotationPresent(ContextCommand.class)) {
                Optional<ContextCommandDefinition> optional = ContextCommandDefinition.build(method, localizationFunction);
                if (optional.isEmpty()) {
                    continue;
                }
                ContextCommandDefinition commandDefinition = optional.get();
                commandDefinition.getPermissions().addAll(permissions);
                if (interaction.ephemeral()) {
                    commandDefinition.setEphemeral(true);
                }
                commands.add(commandDefinition);
            }

            // index components
            if (method.isAnnotationPresent(Button.class)) {
                ButtonDefinition.build(method).ifPresent(button -> {
                    if (interaction.ephemeral()) {
                        button.setEphemeral(true);
                    }
                    buttons.add(button);
                });
            }
            if (method.isAnnotationPresent(EntitySelectMenu.class)) {
                EntitySelectMenuDefinition.build(method).ifPresent(menu -> {
                    if (interaction.ephemeral()) {
                        menu.setEphemeral(true);
                    }
                    selectMenus.add(menu);
                });
            }
            if (method.isAnnotationPresent(StringSelectMenu.class)) {
                StringSelectMenuDefinition.build(method).ifPresent(menu -> {
                    if (interaction.ephemeral()) {
                        menu.setEphemeral(true);
                    }
                    selectMenus.add(menu);
                });
            }

            //index modals
            if (method.isAnnotationPresent(Modal.class)) {
                ModalDefinition.build(method).ifPresent(modal -> {
                    if (interaction.ephemeral()) {
                        modal.setEphemeral(true);
                    }
                    modals.add(modal);
                });
            }
        }

        //loop again and index auto complete
        for (Method method : interactionClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AutoComplete.class)) {
                AutoCompleteDefinition.build(
                        method,
                        autoCompletes.stream().flatMap(it -> it.getCommandNames().stream()).collect(Collectors.toList())
                ).ifPresent(autoComplete -> {
                    autoCompletes.add(autoComplete);

                    Set<String> commandNames = new HashSet<>();
                    autoComplete.getCommandNames().forEach(name -> commands.stream()
                            .filter(it -> it.getCommandType() == Command.Type.SLASH)
                            .filter(command -> command.getName().startsWith(name))
                            .forEach(command -> {
                                ((SlashCommandDefinition) command).setAutoComplete(true);
                                commandNames.add(command.getName());
                            })
                    );
                    autoComplete.setCommandNames(commandNames);
                });
            }
        }

        return Optional.of(new InteractionControllerDefinition(commands, buttons, selectMenus, autoCompletes, modals));
    }

    /**
     * Gets a possibly-empty list of all {@link GenericCommandDefinition CommandDefinitions}.
     *
     * @return a possibly-empty list of all {@link GenericCommandDefinition CommandDefinitions}
     */
    public List<GenericCommandDefinition> getCommands() {
        return commands;
    }

    /**
     * Gets a possibly-empty list of all buttons.
     *
     * @return a possibly-empty list of all buttons
     */
    public List<ButtonDefinition> getButtons() {
        return buttons;
    }

    /**
     * Gets a possibly-empty list of all select menus.
     *
     * @return a possibly-empty list of all select menus
     */
    public List<GenericSelectMenuDefinition<? extends SelectMenu>> getSelectMenus() {
        return selectMenus;
    }

    /**
     * Gets a possibly-empty list of all {@link AutoCompleteDefinition AutoCompleteDefinitions}.
     *
     * @return a possibly-empty list of all {@link AutoCompleteDefinition AutoCompleteDefinitions}
     */
    public Collection<AutoCompleteDefinition> getAutoCompletes() {
        return autoCompletes;
    }

    /**
     * Gets a possibly-empty list of all {@link ModalDefinition ModalDefinitions}.
     *
     * @return a possibly-empty list of all {@link ModalDefinition ModalDefinitions}
     */
    public List<ModalDefinition> getModals() {
        return modals;
    }

    @Override
    public String toString() {
        return "ControllerDefinition{" +
                "commands=" + commands +
                ", buttons=" + buttons +
                ", selectMenus=" + selectMenus +
                ", autoCompletes=" + autoCompletes +
                ", modals=" + modals +
                '}';
    }

}
