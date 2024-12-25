package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.definitions.interactions.impl.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public sealed interface PermissionsInteraction extends Interaction
        permits ButtonDefinition, ContextCommandDefinition, EntitySelectMenuDefinition, ModalDefinition, SlashCommandDefinition, StringSelectMenuDefinition {

    @NotNull
    Collection<String> permissions();

    @NotNull
    default Collection<String> permissions(@NotNull PermissionsInteraction parent) {
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
