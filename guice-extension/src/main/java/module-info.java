import org.jspecify.annotations.NullMarked;

/// An extension to JDA-Commands providing Google's Guice as a dependency injection framework.
///
@NullMarked
module io.github.kaktushose.jda.commands.extension.guice {
    requires transitive org.jspecify;
    requires transitive io.github.kaktushose.jda.commands.core;
    requires transitive org.jetbrains.annotations;

    requires com.google.guice;
    requires net.dv8tion.jda;
    requires org.slf4j;
    requires io.github.kaktushose.proteus;

    exports com.github.kaktushose.jda.commands.guice;

    provides com.github.kaktushose.jda.commands.extension.Extension with com.github.kaktushose.jda.commands.guice.GuiceExtension;
}