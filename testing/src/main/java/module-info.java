module io.github.kaktushose.jdac.testing {
    requires transitive io.github.kaktushose.jdac.core;
    requires transitive io.github.kaktushose.jdac.guice;
    requires net.dv8tion.jda;
    requires org.jetbrains.annotations;
    requires org.mockito;
    requires io.github.kaktushose.proteus;

    exports io.github.kaktushose.jdac.testing;

    exports io.github.kaktushose.jdac.testing.reply;

    exports io.github.kaktushose.jdac.testing.invocation;
    exports io.github.kaktushose.jdac.testing.invocation.components;
    exports io.github.kaktushose.jdac.testing.invocation.commands;
}
