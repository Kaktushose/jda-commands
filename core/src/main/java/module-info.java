import io.github.kaktushose.jdac.property.extension.Extension;
import org.jspecify.annotations.NullMarked;

/// The base module of jda-commands.
///
@NullMarked
module io.github.kaktushose.jdac.core {
    requires tools.jackson.databind;
    requires net.dv8tion.jda;
    requires java.desktop;
    requires org.slf4j;
    requires io.github.kaktushose.proteus;
    requires net.fellbaum.jemoji;

    requires org.jspecify;
    requires org.jetbrains.annotations;
    requires transitive jakarta.inject;
    requires transitive dev.goldmensch.fluava;
    requires org.apache.commons.collections4;
    requires io.github.classgraph;
    requires java.logging;
    requires static io.github.kaktushose.jdac.processor;
    requires dev.goldmensch.propane;

    // base package
    exports io.github.kaktushose.jdac;

    // annotations
    exports io.github.kaktushose.jdac.annotations.constraints;
    exports io.github.kaktushose.jdac.annotations.interactions;
    exports io.github.kaktushose.jdac.annotations.i18n;

    // definitions
    exports io.github.kaktushose.jdac.definitions;
    exports io.github.kaktushose.jdac.definitions.interactions;
    exports io.github.kaktushose.jdac.definitions.interactions.command;
    exports io.github.kaktushose.jdac.definitions.interactions.component;
    exports io.github.kaktushose.jdac.definitions.interactions.component.menu;
    exports io.github.kaktushose.jdac.definitions.description;
    exports io.github.kaktushose.jdac.definitions.features;

    // dispatching api
    exports io.github.kaktushose.jdac.dispatching.expiration;
    exports io.github.kaktushose.jdac.dispatching.context;

    exports io.github.kaktushose.jdac.dispatching.reply;
    exports io.github.kaktushose.jdac.dispatching.reply.dynamic;
    exports io.github.kaktushose.jdac.dispatching.reply.dynamic.menu;

    exports io.github.kaktushose.jdac.dispatching.adapter;

    exports io.github.kaktushose.jdac.dispatching.events;
    exports io.github.kaktushose.jdac.dispatching.events.interactions;

    exports io.github.kaktushose.jdac.dispatching.middleware;

    exports io.github.kaktushose.jdac.dispatching.validation;

    exports io.github.kaktushose.jdac.dispatching.instance;

    // embed
    exports io.github.kaktushose.jdac.embeds;
    exports io.github.kaktushose.jdac.embeds.error;

    // permissions api
    exports io.github.kaktushose.jdac.permissions;

    // command scope api
    exports io.github.kaktushose.jdac.scope;

    // extensions + property
    exports io.github.kaktushose.jdac.property;
    exports io.github.kaktushose.jdac.property.extension;
    exports io.github.kaktushose.jdac.property.events;

    // i18n/messages/emojis/placeholder
    exports io.github.kaktushose.jdac.message.i18n;
    exports io.github.kaktushose.jdac.message.emoji;
    exports io.github.kaktushose.jdac.message.placeholder;
    exports io.github.kaktushose.jdac.message.resolver;

    // components
    exports io.github.kaktushose.jdac.components;
    exports io.github.kaktushose.jdac.components.container;
    exports io.github.kaktushose.jdac.components.pagination;
    exports io.github.kaktushose.jdac.components.pagination.layout;


    uses Extension;
}
