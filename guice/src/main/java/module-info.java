module jda.commands.guice {
    requires transitive jda.commands;
    //noinspection requires-transitive-automatic -- must be
    requires transitive com.google.guice;
    requires org.jetbrains.annotations;

    exports com.github.kaktushose.jda.commands.guice;

    provides com.github.kaktushose.jda.commands.extension.Extension with com.github.kaktushose.jda.commands.guice.GuiceExtension;
}