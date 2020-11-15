package com.github.kaktushose.jda.commands.internal;

import com.github.kaktushose.jda.commands.annotations.Optional;
import com.github.kaktushose.jda.commands.annotations.*;
import com.github.kaktushose.jda.commands.entities.CommandCallable;
import com.github.kaktushose.jda.commands.entities.CommandEvent;
import com.github.kaktushose.jda.commands.entities.CommandSettings;
import com.github.kaktushose.jda.commands.entities.Parameter;
import com.github.kaktushose.jda.commands.exceptions.CommandException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

final class CommandRegistry {

    private static final Logger log = LoggerFactory.getLogger(CommandRegistry.class);
    private final Set<CommandCallable> commands;
    private final CommandSettings settings;
    private final DependencyInjector dependencyInjector;

    CommandRegistry(CommandSettings settings, DependencyInjector dependencyInjector) {
        this.commands = new HashSet<>();
        this.settings = settings;
        this.dependencyInjector = dependencyInjector;
    }

    final Set<CommandCallable> getCommands() {
        return commands;
    }

    void indexCommands() {
        log.debug("Indexing commands...");

        Reflections reflections = new Reflections("");
        Set<Class<?>> controllerSet = reflections.getTypesAnnotatedWith(CommandController.class);
        for (Class<?> controllerClass : controllerSet) {
            // validating CommandController
            log.debug("Found CommandController {}", controllerClass.getName());
            CommandController commandController = controllerClass.getAnnotation(CommandController.class);
            if (!commandController.isActive()) {
                log.warn("CommandController {} is set inactive. Skipping the controller and its commands", controllerClass.getName());
                continue;
            }
            // creating instance for method invoking
            Object instance;
            try {
                instance = controllerClass.getConstructors()[0].newInstance();
            } catch (Exception e) {

                continue;
            }

            List<Field> fields = new ArrayList<>();
            boolean hasValidFields = true;
            for (Field field : instance.getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(Inject.class)) {
                    continue;
                }
                if (!Modifier.isPublic(field.getModifiers())) {
                    logError(String.format("Field %s has invalid access modifiers", field), controllerClass);
                    hasValidFields = false;
                    break;
                }
                fields.add(field);
            }
            if (!hasValidFields) {
                continue;
            }
            dependencyInjector.addDependency(instance, fields);

            Set<String> permissions = new HashSet<>();
            if (controllerClass.isAnnotationPresent(Permission.class)) {
                Permission permission = controllerClass.getAnnotation(Permission.class);
                permissions.addAll(Arrays.asList(permission.value()));
            }

            // validating each Command
            for (Method method : controllerClass.getDeclaredMethods()) {
                log.debug("Found command {}", method.getName());

                if (!method.isAnnotationPresent(Command.class)) {
                    continue;
                }
                Command command = method.getAnnotation(Command.class);
                if (!command.isActive()) {
                    log.warn("Command {} is set inactive. Skipping this command", method.getName());
                    continue;
                }
                if (!method.getReturnType().equals(Void.TYPE) || !Modifier.isPublic(method.getModifiers())) {
                    logError("Command method has an invalid return type or access modifier!", method);
                    continue;
                }

                // creating all possible labels for a command
                List<String> labels = new ArrayList<>();
                for (String controllerLabel : commandController.value()) {
                    for (String commandLabel : command.value()) {
                        String toAdd = (controllerLabel + " " + commandLabel).trim();
                        if (settings.isIgnoreLabelCase()) {
                            toAdd = toAdd.toLowerCase();
                        }
                        labels.add(toAdd);
                    }
                }

                if (method.isAnnotationPresent(Permission.class)) {
                    Permission permission = method.getAnnotation(Permission.class);
                    permissions.addAll(Arrays.asList(permission.value()));
                }

                // validating method signature
                List<Parameter> parameters = new ArrayList<>();
                AnnotatedType[] parameterTypes = method.getAnnotatedParameterTypes();
                boolean hasOptional = false; // if true the checked parameter must be optional
                boolean isValid = true; // false if the method signature is invalid thus method is skipped (L165)
                for (int i = 0; i < parameterTypes.length; i++) {
                    AnnotatedType parameterType = parameterTypes[i];
                    String name = ParameterType.wrap(parameterType.getType().getTypeName());

                    // first method argument is not a CommandEvent
                    if (i == 0) {
                        isValid = name.equals(CommandEvent.class.getName());
                        if (!isValid) {
                            logError(String.format("Command method has an invalid method signature! First parameter must be of type %s!",
                                    CommandEvent.class.getName()), method);
                            break;
                        }
                        continue;
                    }

                    // check if parameter type is supported
                    isValid = ParameterType.validate(name);
                    if (!isValid) {
                        logError(String.format("Command method has an invalid method signature! %s is an unsupported method parameter!",
                                name), method);
                        break;
                    }

                    if (name.equals(ParameterType.ARRAY.name)) {
                        parameters.add(new Parameter(false, false, "", ParameterType.ARRAY));
                        break;
                    }

                    // check for parameter annotations
                    boolean isConcat = false;
                    boolean isOptional = false;
                    int optionalIndex = -1;
                    String defaultValue = "";
                    for (Annotation parameterAnnotation : method.getParameterAnnotations()[i]) {
                        Class<? extends Annotation> annotationClass = parameterAnnotation.annotationType();

                        // check if String concatenation is enabled
                        if (annotationClass.equals(Concat.class)) {
                            // must be last parameter
                            if (i != parameterTypes.length - 1) {
                                logError("Command method has an invalid method signature! " +
                                        "Concatenation may be only enabled for the last parameter", method);
                                isValid = false;
                                break;
                            }
                            // must be a String
                            if (!name.equals("java.lang.String")) {
                                logError("Command method has an invalid method signature! " +
                                        "Concatenation is enabled but last parameter is not a String!", method);
                                isValid = false;
                                break;
                            }
                            isConcat = true;
                        }

                        // if method already had an optional parameter (hasOptional) this one must be optional too
                        if (hasOptional && optionalIndex != i && !annotationClass.equals(Optional.class)) {
                            logError("Command method has an invalid method signature! " +
                                    "An optional parameter may not be followed by a non-optional parameter!", method);
                            isValid = false;
                            break;
                        }
                        // get optionals default value
                        if (annotationClass.equals(Optional.class)) {
                            isOptional = true;
                            hasOptional = true;
                            optionalIndex = i;
                            defaultValue = ((Optional) parameterAnnotation).value();
                        }
                    }
                    parameters.add(new Parameter(isConcat, isOptional, defaultValue, ParameterType.getByName(name)));
                }

                if (!isValid) {
                    continue;
                }

                // this must change if command overloading is implemented
                if (commands.stream().anyMatch(commandCallable -> commandCallable.getLabels().stream().anyMatch(labels::contains))) {
                    logError("The labels for the command are already registered!", method);
                    continue;
                }
                CommandCallable commandCallable = new CommandCallable(labels,
                        command.name(),
                        command.desc(),
                        command.usage(),
                        command.category(),
                        parameters,
                        permissions,
                        method,
                        instance);
                commands.add(commandCallable);
                log.debug("Registered command {}", commandCallable);
            }
        }
        log.info("Command indexing done! Indexed a total of {} commands!", commands.size());
    }

    private void logError(String message, Class<?> controllerClass) {
        log.error("An error has occurred! Skipping CommandController {} and its commands!",
                controllerClass.getCanonicalName(),
                new CommandException(message));

    }

    private void logError(String message, Method commandMethod) {
        log.error("An error has occurred! Skipping Command {}!",
                commandMethod.getName(),
                new CommandException(message));
    }

}
