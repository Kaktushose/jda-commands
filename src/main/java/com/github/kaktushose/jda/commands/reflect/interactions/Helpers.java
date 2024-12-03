package com.github.kaktushose.jda.commands.reflect.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.reflect.MethodBuildContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Helpers {

    private static final Logger log = LoggerFactory.getLogger(Helpers.class);

    public static Set<String> permissions(MethodBuildContext context) {
        Permissions permission = context.method().getAnnotation(Permissions.class);

        if (permission != null) {
            HashSet<String> mergedPermissions = new HashSet<>(context.permissions());
            mergedPermissions.addAll(Set.of(permission.value()));
            return Collections.unmodifiableSet(mergedPermissions);
        }
        return context.permissions();
    }

    public static boolean ephemeral(MethodBuildContext context, boolean localEphemeral) {
        return context.interaction().ephemeral() || localEphemeral;
    }

    public static boolean isIncorrectParameterType(Method method, int index, Class<?> type) {
        if (!type.isAssignableFrom(method.getParameters()[index].getType())) {
            log.error("An error has occurred! Skipping Interaction {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format("%d. parameter must be of type %s", index+1, type.getSimpleName())));
            return true;
        }
        return false;
    }

    public static boolean isIncorrectParameterAmount(Method method, int amount) {
        if (method.getParameters().length != amount) {
            log.error("An error has occurred! Skipping Interaction {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format(
                            "Invalid amount of parameters!. Expected: %d Actual: %d",
                            amount,
                            method.getParameters().length
                    )));
            return true;
        }
        return false;
    }


}
