module jda.commands.guice {
    requires transitive jda.commands;
    requires com.google.guice;
    requires org.jetbrains.annotations;
    requires net.dv8tion.jda;

    exports com.github.kaktushose.jda.commands.guice;

    provides com.github.kaktushose.jda.commands.extension.Extension with com.github.kaktushose.jda.commands.guice.GuiceExtension;
}