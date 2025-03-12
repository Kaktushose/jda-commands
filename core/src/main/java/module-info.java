/// The base module of jda-commands.
module io.github.kaktushose.jda.commands.core {
    requires com.google.gson;
    requires net.dv8tion.jda;
    requires org.reflections;
    requires java.desktop;
    requires org.slf4j;

    requires transitive org.jetbrains.annotations;
    requires transitive jakarta.inject;
    requires java.naming;

    // base package
    exports com.github.kaktushose.jda.commands;

    // annotations
    exports com.github.kaktushose.jda.commands.annotations.constraints;
    exports com.github.kaktushose.jda.commands.annotations.interactions;

    // definitions
    exports com.github.kaktushose.jda.commands.definitions;
    exports com.github.kaktushose.jda.commands.definitions.interactions;
    exports com.github.kaktushose.jda.commands.definitions.interactions.command;
    exports com.github.kaktushose.jda.commands.definitions.interactions.component;
    exports com.github.kaktushose.jda.commands.definitions.interactions.component.menu;
    exports com.github.kaktushose.jda.commands.definitions.description;
    exports com.github.kaktushose.jda.commands.definitions.features;

    // dispatching api
    exports com.github.kaktushose.jda.commands.dispatching.expiration;
    exports com.github.kaktushose.jda.commands.dispatching.context;

    exports com.github.kaktushose.jda.commands.dispatching.reply;
    exports com.github.kaktushose.jda.commands.dispatching.reply.dynamic;
    exports com.github.kaktushose.jda.commands.dispatching.reply.dynamic.menu;

    exports com.github.kaktushose.jda.commands.dispatching.adapter;
    exports com.github.kaktushose.jda.commands.dispatching.adapter.impl;

    exports com.github.kaktushose.jda.commands.dispatching.events;
    exports com.github.kaktushose.jda.commands.dispatching.events.interactions;

    exports com.github.kaktushose.jda.commands.dispatching.middleware;

    exports com.github.kaktushose.jda.commands.dispatching.validation;

    exports com.github.kaktushose.jda.commands.dispatching.instance;

    // embed
    exports com.github.kaktushose.jda.commands.embeds;
    exports com.github.kaktushose.jda.commands.embeds.error;

    // permissions api
    exports com.github.kaktushose.jda.commands.permissions;

    // command scope api
    exports com.github.kaktushose.jda.commands.scope;

    // extensions
    exports com.github.kaktushose.jda.commands.extension;

    // i18n
    exports com.github.kaktushose.jda.commands.i18n;

    uses com.github.kaktushose.jda.commands.extension.Extension;
}