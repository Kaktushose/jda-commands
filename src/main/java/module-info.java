/// The base module of jda-commands
module jda.commands {
    requires com.google.gson;
    requires jsr305;
    requires net.dv8tion.jda;
    requires org.jetbrains.annotations;
    requires org.reflections;
    requires java.desktop;
    requires org.slf4j;
    requires kotlin.stdlib;

    // base package
    exports com.github.kaktushose.jda.commands;

    // annotations
    exports com.github.kaktushose.jda.commands.annotations;
    exports com.github.kaktushose.jda.commands.annotations.constraints;
    exports com.github.kaktushose.jda.commands.annotations.interactions;

    // dependency injection
    exports com.github.kaktushose.jda.commands.dependency;

    // dispatching api
    exports com.github.kaktushose.jda.commands.dispatching;
    exports com.github.kaktushose.jda.commands.dispatching.adapter;
    exports com.github.kaktushose.jda.commands.dispatching.adapter.impl;

    exports com.github.kaktushose.jda.commands.dispatching.context;
    exports com.github.kaktushose.jda.commands.dispatching.events;
    exports com.github.kaktushose.jda.commands.dispatching.events.interactions;

    exports com.github.kaktushose.jda.commands.dispatching.middleware;
    exports com.github.kaktushose.jda.commands.dispatching.middleware.impl;

    exports com.github.kaktushose.jda.commands.dispatching.reply;

    exports com.github.kaktushose.jda.commands.dispatching.validation;
    exports com.github.kaktushose.jda.commands.dispatching.validation.impl;

    // embed
    exports com.github.kaktushose.jda.commands.embeds;
    exports com.github.kaktushose.jda.commands.embeds.error;

    // permissions api
    exports com.github.kaktushose.jda.commands.permissions;

    // command scope api
    exports com.github.kaktushose.jda.commands.scope;
}