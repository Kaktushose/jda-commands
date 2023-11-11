package adapting.mock;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;

public class JDACommandsMock extends JDACommands {

    public JDACommandsMock() {
    }

    @Override
    public ImplementationRegistry getImplementationRegistry() {
       return new ImplementationRegistry(new DependencyInjector(), new MiddlewareRegistry(), new TypeAdapterRegistry(), new ValidatorRegistry());
    }
}
