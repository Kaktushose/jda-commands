/// An extension to JDA-Commands providing Google's Guice as a dependency injection framework.
module io.github.kaktushose.jda.commands.extension.guice {
    requires transitive io.github.kaktushose.jda.commands.core;
    requires com.google.guice;
    requires org.jetbrains.annotations;
    requires net.dv8tion.jda;
    requires org.slf4j;
    requires io.github.kaktushose.proteus;

    exports com.github.kaktushose.jda.commands.guice;

    provides com.github.kaktushose.jda.commands.extension.Extension with com.github.kaktushose.jda.commands.guice.GuiceExtension;
}