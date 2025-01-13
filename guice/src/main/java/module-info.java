import com.github.kaktushose.jda.commands.dispatching.instantiation.spi.InstantiatorProvider;
import com.github.kaktushose.jda.commands.guice.GuiceInstantiatorProvider;

module jda.commands.guice {
    requires jda.commands;
    requires com.google.guice;

    provides InstantiatorProvider with GuiceInstantiatorProvider;
}