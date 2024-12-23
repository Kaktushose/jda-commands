package com.github.kaktushose.jda.commands.definitions.api.features;

import com.github.kaktushose.jda.commands.definitions.api.interactions.PermissionsInteraction;

import java.util.Collection;

public sealed interface Permissions permits PermissionsInteraction {

    Collection<String> permissions();

}
