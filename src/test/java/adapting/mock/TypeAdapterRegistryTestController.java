package adapting.mock;

import adapting.CustomType;
import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.annotations.Optional;
import com.github.kaktushose.jda.commands.dispatching.CommandEvent;

@CommandController
public class TypeAdapterRegistryTestController {

    public static final String OPTIONAL_DEFAULT = "default";

    @Command
    public void stringArray(CommandEvent event, String[] args) {

    }

    @Command
    public void inputLength(CommandEvent event, int i) {

    }

    @Command
    public void optionalNull(CommandEvent event, @Optional int i) {

    }

    @Command
    public void optionalDefault(CommandEvent event, @Optional(OPTIONAL_DEFAULT) String s) {

    }

    @Command
    public void noAdapter(CommandEvent event, CustomType type) {

    }

    @Command
    public void wrongArgument(CommandEvent event, int i) {

    }

}
