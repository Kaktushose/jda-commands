module jda.commands {
    requires com.google.gson;
    requires jsr305;
    requires net.dv8tion.jda;
    requires org.jetbrains.annotations;
    requires org.reflections;
    requires java.desktop;
    requires org.slf4j;
    requires kotlin.stdlib;


    // dispatching api
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

}