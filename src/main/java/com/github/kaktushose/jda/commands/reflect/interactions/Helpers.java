package com.github.kaktushose.jda.commands.reflect.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.reflect.MethodBuildContext;

import java.util.*;

public final class Helpers {
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
}
