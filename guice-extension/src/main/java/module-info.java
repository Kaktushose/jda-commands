import io.github.kaktushose.jdac.configuration.Extension;
import io.github.kaktushose.jdac.guice.internal.GuiceExtension;
import org.jspecify.annotations.NullMarked;

/// An extension to JDA-Commands providing Google's Guice as a dependency injection framework.
///
@NullMarked
module io.github.kaktushose.jdac.guice {
    requires transitive org.jspecify;
    requires transitive io.github.kaktushose.jdac.core;
    requires transitive org.jetbrains.annotations;

    requires com.google.guice;
    requires net.dv8tion.jda;
    requires org.slf4j;
    requires io.github.kaktushose.proteus;

    exports io.github.kaktushose.jdac.guice;

    provides Extension with GuiceExtension;
}