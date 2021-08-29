package commands;

import com.github.kaktushose.jda.commands.rewrite.adapters.ParameterAdapterRegistry;
import com.github.kaktushose.jda.commands.rewrite.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.rewrite.reflect.ParameterDefinition;
import org.junit.jupiter.api.BeforeAll;

public class CommandDefinitionTest {

    private ParameterAdapterRegistry registry;

    @BeforeAll
    public void setup() {
        // make sure that this type is not registered before testing
        registry = new ParameterAdapterRegistry();
        registry.unregister(UnsupportedType.class);
    }

    public void test() {
    }

}
