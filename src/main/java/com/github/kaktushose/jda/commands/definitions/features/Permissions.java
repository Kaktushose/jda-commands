package com.github.kaktushose.jda.commands.definitions.features;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public sealed interface Permissions extends Invokeable permits InteractionDefinition {

    @NotNull
    Collection<String> permissions();

    @NotNull
    default Collection<String> permissions(@NotNull Permissions parent) {
        var permission = method().annotation(com.github.kaktushose.jda.commands.annotations.interactions.Permissions.class);
        if (permission.isPresent()) {
            HashSet<String> mergedPermissions = new HashSet<>(parent.permissions());
            mergedPermissions.addAll(Set.of(permission.get().value()));
            return Collections.unmodifiableSet(mergedPermissions);
        }
        return parent.permissions();
    }

}
