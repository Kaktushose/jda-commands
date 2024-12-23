package com.github.kaktushose.jda.commands.definitions.api.interactions;

import com.github.kaktushose.jda.commands.definitions.api.features.Invokeable;
import com.github.kaktushose.jda.commands.definitions.api.features.Permissions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public sealed interface PermissionsInteraction extends Invokeable, Permissions permits ButtonDefinition, CommandDefinition, ModalDefinition, SelectMenuDefinition {
    
    default Collection<String> permissions(Permissions parent) {
        com.github.kaktushose.jda.commands.annotations.interactions.Permissions permission =
                method().getAnnotation(com.github.kaktushose.jda.commands.annotations.interactions.Permissions.class);
        if (permission != null) {
            HashSet<String> mergedPermissions = new HashSet<>(parent.permissions());
            mergedPermissions.addAll(Set.of(permission.value()));
            return Collections.unmodifiableSet(mergedPermissions);
        }
        return parent.permissions();
    }

}
